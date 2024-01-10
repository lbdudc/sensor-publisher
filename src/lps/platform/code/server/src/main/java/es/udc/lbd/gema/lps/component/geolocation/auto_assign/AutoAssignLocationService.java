/*% if (feature.DM_A_G_Batch) { %*/
package es.udc.lbd.gema.lps.component.geolocation.auto_assign;

import es.udc.lbd.gema.lps.component.geolocation.auto_assign.CommonJSON.GeolocationWsParams;
import es.udc.lbd.gema.lps.component.geolocation.custom.AutoAssignJSON;
import es.udc.lbd.gema.lps.component.geolocation.custom.AutoAssignResultJSON;
import es.udc.lbd.gema.lps.component.geolocation.exceptions.TypeNotSupportedException;
/*% if (feature.DM_DS_Address) { %*/
import es.udc.lbd.gema.lps.component.geolocation.model.domain.GCAddress;
/*% } %*/
import es.udc.lbd.gema.lps.component.geolocation.model.domain.Geoname;
import es.udc.lbd.gema.lps.component.geolocation.model.repository.GeonameRepository;
import es.udc.lbd.gema.lps.model.service.exceptions.AppRuntimeException;
import es.udc.lbd.gema.lps.model.repository._repositoriesBasePackage;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import javax.inject.Inject;
import javax.persistence.Id;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.locationtech.jts.geom.Point;

@Service
@Transactional(rollbackFor = Exception.class)
public abstract class AutoAssignLocationService {
    private static final Logger logger = LoggerFactory.getLogger(AutoAssignLocationService.class);

    private static final String pointType = "org.locationtech.jts.geom.Point";

    @Inject
    ListableBeanFactory beanFactory;

    @Inject
    GeonameRepository geonameRepository;

    @Value("${server.pageSize}")
    private int pageSize;

    /**
     * Geolocates entities of ({@code data.entity} class
     *
     * @param data Parameters used to geolocate data
     * @return Geolocation result
     * @throws TypeNotSupportedException If no geolocator found for speficited strategy {@code data.strategy}
     */
    public AutoAssignResultJSON process(AutoAssignJSON data) throws TypeNotSupportedException {
        // Check if this geolocator works with specified strategy
        this.useThisGeolocator(data.getStrategy());

        // Retrieve class of target entity
        Class<?> clazzEntity = this.getTargetObjectClass(data.getEntity());

        // Retrieve class of the repository of target entity
        String clazzRepositoryName = clazzEntity.getSimpleName() + "Repository";
        String clazzRepositoryPackageName = StringUtils.removeEnd(_repositoriesBasePackage.class.getName(), _repositoriesBasePackage.class.getSimpleName());
        Class<?> clazzRepository = this.getTargetObjectClass(clazzRepositoryPackageName + clazzRepositoryName);

        // WARNING: We need to declare a .findAll method in every Repository class! => Try to avoid this
        Method findAllMethod = this.getMethod(clazzRepository, "findAll", new Class<?>[]{Pageable.class});

        return this.autoassignLocations(data, clazzEntity, clazzRepositoryName, findAllMethod);
    }

    /**
     * Validates geolocator is valid for specified strategy
     *
     * @param strategy Geolocator strategy
     * @throws TypeNotSupportedException If this geolocator does not work with specified strategy
     */
    protected void useThisGeolocator(String strategy) throws TypeNotSupportedException {
        if (!getGeolocatorType().equals(strategy)) {
            throw new TypeNotSupportedException();
        }
    }

    /**
     * Assigns locations for all the instances of the entities specified. <br>
     *
     * @param data
     * @param clazzObject
     * @param clazzRepositoryName
     * @param findAllMethod
     * @return
     */
    private AutoAssignResultJSON autoassignLocations(AutoAssignJSON data, Class<?> clazzObject, String clazzRepositoryName, Method findAllMethod) {

        // Retrieve objects from database in pages
        int currentPage = 1;
        int totalPages = 1;


        AutoAssignResultJSON result = new AutoAssignResultJSON();
        while (currentPage <= totalPages) {
            try {
                // Obtain id field using reflection
                String idField = FieldUtils.getFieldsListWithAnnotation(clazzObject, Id.class).iterator().next().getName();

                Pageable pageable = PageRequest.of(currentPage - 1, pageSize, Sort.Direction.ASC, idField);
                Page<?> page = Page.class.cast(findAllMethod.invoke(beanFactory.getBean(StringUtils.uncapitalize(clazzRepositoryName)), pageable));
                totalPages = page.getTotalPages();
                logger.debug("Page {} of {}, Elements: {}..{} of {} ", currentPage, totalPages,
                    (currentPage) * pageSize - 1, (currentPage - 1) * pageSize + page.getNumberOfElements(), page.getTotalElements());
                currentPage++;

                // Retrieve properties values
                for (Object item : page.getContent()) {
                    logger.debug("ITEM");
                    boolean geolocateOnlyNotLocated = data.isGeolocateOnlyNotLocated();
                    boolean alreadyLocated = getValue(clazzObject, item, data.getPosition(), Point.class) != null;

                    // Geolocate all OR geolocate only not located
                    if (!geolocateOnlyNotLocated || (geolocateOnlyNotLocated && !alreadyLocated)) {
                        GeolocationWsParams params = this.calculateWsParams(data, clazzObject, item);
                        Point p = this.geolocate(params);
                        if (p != null) {
                            setValue(clazzObject, item, data.getPosition(), Point.class, p);
                            result.addGeolocated();
                        } else {
                            result.addNotGeolocated();
                        }
                    } else {
                        result.addIgnored();
                    }
                }
            } catch (IllegalAccessException | InvocationTargetException e) {
                throw new AppRuntimeException("Unable to invoke method", e);
            }
        }
        return result;
    }

    /**
     * Builds and object with the values set in the specified fields
     *
     * @param data  Object with fields names
     * @param clazz Object class
     * @param item  Object instance
     * @return Plain object with values
     */
    private GeolocationWsParams calculateWsParams(AutoAssignJSON data, Class<?> clazz, Object item) {
        // @formatter:off
        GeolocationWsParams params = new GeolocationWsParams();
        params.setAddress(     data.getAddress()      != null ? getValue(clazz, item, data.getAddress()     , String.class) : null);
        params.setTown(        data.getTown()         != null ? getValue(clazz, item, data.getTown()        , String.class) : null);
        params.setSubdivision1(data.getSubdivision1() != null ? getValue(clazz, item, data.getSubdivision1(), String.class) : null);
        params.setSubdivision2(data.getSubdivision2() != null ? getValue(clazz, item, data.getSubdivision2(), String.class) : null);
        params.setSubdivision3(data.getSubdivision3() != null ? getValue(clazz, item, data.getSubdivision3(), String.class) : null);
        params.setSubdivision4(data.getSubdivision4() != null ? getValue(clazz, item, data.getSubdivision4(), String.class) : null);
        params.setCountry     (                                 getValue(clazz, item, data.getCountry()     , String.class)       );
        // @formatter:on
        logger.debug("\n\n==> Id: {}, Params: {}", getValue(clazz, item, "id", Long.class), params);

        return params;
    }

    /**
     * Obtains type supported by this geolocator
     *
     * @return Geolocator strategy supported
     */
    protected abstract String getGeolocatorType();

    /**
     * Geolocates location specified in {@code params}
     *
     * @param params Parameters used to geolocate item
     * @return Point with coordinates or null if no location was found or any error happened
     */
    protected abstract Point geolocate(GeolocationWsParams params);

    /**
     * Obtain target object class
     *
     * @param entity Full entity name
     * @return Entity class
     */
    protected Class<?> getTargetObjectClass(String entity) {
        Class<?> clazzObject;
        try {
            return clazzObject = Class.forName(entity);
        } catch (ClassNotFoundException e) {
            throw new AppRuntimeException("Unable to obtain class for " + entity, e);
        }
    }

    /**
     * Obtains a method using reflection
     *
     * @param repository     Class where the method belongs to
     * @param method         Method name
     * @param parameterTypes List of parameters of the method
     * @return
     */
    protected Method getMethod(Class<?> repository, String method, Class<?>... parameterTypes) {
        try {
            return repository.getMethod(method, parameterTypes);
        } catch (NoSuchMethodException | SecurityException e) {
            throw new AppRuntimeException("Unable to invoke '" + method + "' method", e);
        }

    }

    /**
     * Retrieves object field value via reflection
     *
     * @param clazzObject Object class
     * @param item        Instance of clazzObject
     * @param field       Field name (lowercase)
     * @param clazz       Field class
     * @return Object field value
     */
    protected <T> T getValue(Class<?> clazzObject, Object item, String field, Class<T> clazz) {
        Method method;
        try {
            /*% if (feature.DM_DS_Address) { %*/
            // Fields splitted with dots represents addresses
            // Improvement: If somebody needs to identify more special fields, they can be splitted with
            // some special chars, like #. Maybe an id can be added to identify the main class
            // Example: <field_name>#<subclass>.<subfield>
            //          address -> home#gcaddres.line1, home#gcaddress.country, ...
            //          other   -> test#other.field1,   test#other.field2
            if (field.contains(".")) {
                // Address field
                String parts[] = StringUtils.split(field, ".");

                GCAddress address = getValue(clazzObject, item, parts[0], GCAddress.class);
                // check address value is not null
                if (address == null) {
                  return null;
                } else {
                  if ("point".equals(parts[1]) || "line1".equals(parts[1])) {
                    return clazz.cast(getValue(address.getClass(), address, parts[1], clazz));
                  }
                  Geoname g = getValue(address.getClass(), address, parts[1], Geoname.class);
                  // Lang value here is not critical because if the location is not found in specified
                  // language a 'neutral' name is retrieved from geonames tables
                  return clazz.cast((g != null) ? geonameRepository.getLocationName(g.getId(), "es").getName() : "");
                }
            }
            /*% } %*/
            method = clazzObject.getMethod("get" + StringUtils.capitalize(field));
            return clazz.cast(method.invoke(item));
        } catch (NoSuchMethodException | SecurityException e) {
            throw new AppRuntimeException("Unable to invoke method", e);
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            throw new AppRuntimeException("Unable to invoke method on " + clazzObject.getName(), e);
        }
    }

    /**
     * Sets object field value via reflection
     *
     * @param clazzObject   Object class
     * @param item          Instance of clazzObject
     * @param field         Field name (lowercase)
     * @param parameterType Field class
     * @param value         Value to set on specified field
     */
    protected <T> void setValue(Class<?> clazzObject, Object item, String field, Class<T> parameterType, T value) {
        Method method;
        try {
            /*% if (feature.DM_DS_Address) { %*/
            if (field.contains(".")) {
                String parts[] = StringUtils.split(field, ".");
                GCAddress address = getValue(clazzObject, item, parts[0], GCAddress.class);
                setValue(address.getClass(), address, parts[1], parameterType, value);
                return;
            }
            /*% } %*/
            method = clazzObject.getMethod("set" + StringUtils.capitalize(field), parameterType);
            method.invoke(item, value);
        } catch (NoSuchMethodException | SecurityException e) {
            throw new AppRuntimeException("Unable to invoke method", e);
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            throw new AppRuntimeException("Unable to invoke method on " + clazzObject.getName(), e);
        }
    }
}
/*% } %*/
