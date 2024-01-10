/*% if (feature.DM_DS_Address || feature.DM_A_G_Batch) { %*/
package es.udc.lbd.gema.lps.component.geolocation.model.repository;

import es.udc.lbd.gema.lps.component.geolocation.custom.CountryJSON;
import es.udc.lbd.gema.lps.component.geolocation.custom.LocationNameJSON;
import es.udc.lbd.gema.lps.component.geolocation.custom.SubdivisionJSON;
import es.udc.lbd.gema.lps.component.geolocation.custom.TownJSON;

import java.util.List;

import javax.persistence.EntityManager;

import org.hibernate.Session;
import org.hibernate.query.Query;
import org.hibernate.transform.Transformers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class GeonameRepositoryImpl implements GeonameRepositoryCustom {

    private static final Logger logger = LoggerFactory.getLogger(GeonameRepositoryImpl.class);
    private static final String COALESCE_NAME = "" +
        "     COALESCE(" +
        "         ( SELECT alternatename " +
        "           FROM geonames.alternatename " +
        "           WHERE isolanguage LIKE :lang AND geonameid = g.geonameid " +
        "           ORDER BY isshortname, ispreferredname LIMIT 1)" +
        "     , g.name) ";
    @Autowired
    EntityManager entityManager;
    /*% if (feature.DM_DS_Address) { %*/

    @Override
    public List<CountryJSON> findAllCountries(String lang) {
        String sqlQuery = "" +
            " SELECT g.geonameid as id, g.country as code, g.type as type, " +
            "     COALESCE(" +
            "         ( SELECT alternatename " +
            "           FROM geonames.alternatename " +
            "           WHERE isolanguage LIKE :lang AND geonameid = g.geonameid " +
            "           ORDER BY isshortname, ispreferredname LIMIT 1)" +
            "     , g.name) as name" +
            " FROM geonames.geoname g" +
            " WHERE g.type LIKE 'COUNTRY'" +
            " ORDER BY 4";

        Query query = entityManager.unwrap(Session.class).createNativeQuery(sqlQuery);
        query.setParameter("lang", lang);
        query.setResultTransformer(Transformers.aliasToBean(CountryJSON.class));

        return query.list();
    }

    @Override
    public List<SubdivisionJSON> findAdm1ByCountry(String countryCode, String lang) {
        String sqlQuery = "" +
            " SELECT g.geonameid as id, g.type as type , g.admin1 as code, " + COALESCE_NAME + " as name" +
            " FROM geonames.geoname g" +
            " WHERE g.country LIKE :country AND g.type LIKE 'ADM1'" +
            " ORDER BY 4";

        Query query = entityManager.unwrap(Session.class).createNativeQuery(sqlQuery);
        query.setParameter("country", countryCode.toUpperCase());
        query.setParameter("lang", lang);
        query.setResultTransformer(Transformers.aliasToBean(SubdivisionJSON.class));

        return query.list();
    }

    @Override
    public List<SubdivisionJSON> findAdm2ByCodes(String countryCode, String adm1Code, String lang) {
        String sqlQuery = "" +
            " SELECT g.geonameid as id, g.type as type, g.admin2 as code, " + COALESCE_NAME + " as name" +
            " FROM geonames.geoname g" +
            " WHERE g.country LIKE :country AND g.admin1 LIKE :adm1Code AND g.type LIKE 'ADM2' " +
            " ORDER BY 4";

        Query query = entityManager.unwrap(Session.class).createNativeQuery(sqlQuery);
        query.setParameter("country", countryCode.toUpperCase());
        query.setParameter("adm1Code", adm1Code);
        query.setParameter("lang", lang);
        query.setResultTransformer(Transformers.aliasToBean(SubdivisionJSON.class));

        return query.list();
    }

    @Override
    public List<SubdivisionJSON> findAdm3ByCodes(String countryCode, String adm1Code, String adm2Code, String lang) {
        String sqlQuery = "" +
            " SELECT g.geonameid as id, g.type as type, g.admin3 as code, " + COALESCE_NAME + " as name" +
            " FROM geonames.geoname g" +
            " WHERE g.country LIKE :country AND g.admin1 LIKE :adm1Code AND g.admin2 LIKE :adm2Code AND g.type LIKE 'ADM3' " +
            " ORDER BY 4";

        Query query = entityManager.unwrap(Session.class).createNativeQuery(sqlQuery);
        query.setParameter("country", countryCode.toUpperCase());
        query.setParameter("adm1Code", adm1Code);
        query.setParameter("adm2Code", adm2Code);
        query.setParameter("lang", lang);
        query.setResultTransformer(Transformers.aliasToBean(SubdivisionJSON.class));

        return query.list();
    }

    @Override
    public List<SubdivisionJSON> findAdm4ByCodes(String countryCode, String adm1Code, String adm2Code, String adm3Code, String lang) {
        String sqlQuery = "" +
            " SELECT g.geonameid as id, g.type as type, g.admin4 as code, " + COALESCE_NAME + " as name" +
            " FROM geonames.geoname g" +
            " WHERE g.country LIKE :country AND g.admin1 LIKE :adm1Code AND g.admin2 LIKE :adm2Code AND g.admin3 LIKE :adm3Code AND g.type LIKE 'ADM4' " +
            " ORDER BY 4";

        Query query = entityManager.unwrap(Session.class).createNativeQuery(sqlQuery);
        query.setParameter("country", countryCode.toUpperCase());
        query.setParameter("adm1Code", adm1Code);
        query.setParameter("adm2Code", adm2Code);
        query.setParameter("adm3Code", adm3Code);
        query.setParameter("lang", lang);
        query.setResultTransformer(Transformers.aliasToBean(SubdivisionJSON.class));

        return query.list();
    }

    @Override
    public List<TownJSON> findTownsByCodes(String countryCode, String adm1Code, String adm2Code, String adm3Code, String adm4Code, String lang) {
        String sqlQuery = "" +
            " SELECT g.geonameid as id, g.type as type, " + COALESCE_NAME + " as name" +
            " FROM geonames.geoname g" +
            " WHERE g.country LIKE :country " +
            (adm1Code != null ? " AND g.admin1 LIKE :adm1Code" : " AND g.admin1 IS NULL") +
            (adm2Code != null ? " AND g.admin2 LIKE :adm2Code" : " AND g.admin2 IS NULL") +
            (adm3Code != null ? " AND g.admin3 LIKE :adm3Code" : " AND g.admin3 IS NULL") +
            (adm4Code != null ? " AND g.admin4 LIKE :adm4Code" : " AND g.admin4 IS NULL") +
            " AND g.type LIKE 'TOWN' " +
            " ORDER BY 3";

        Query query = entityManager.unwrap(Session.class).createNativeQuery(sqlQuery);
        query.setParameter("country", countryCode.toUpperCase());
        // @formatter:off
        if (adm1Code != null) { query.setParameter("adm1Code", adm1Code); }
        if (adm2Code != null) { query.setParameter("adm2Code", adm2Code); }
        if (adm3Code != null) { query.setParameter("adm3Code", adm3Code); }
        if (adm4Code != null) { query.setParameter("adm4Code", adm4Code); }
        // @formatter:on
        query.setParameter("lang", lang);
        query.setResultTransformer(Transformers.aliasToBean(TownJSON.class));

        return query.list();
    }

    /*% } %*/
    @Override
    public LocationNameJSON getLocationName(Integer geonameId, String lang) {
        String sqlQuery = "SELECT " + COALESCE_NAME + " FROM geonames.geoname g WHERE geonameid = :geonameId";

        Query query = entityManager.unwrap(Session.class).createNativeQuery(sqlQuery);
        query.setParameter("geonameId", geonameId);
        query.setParameter("lang", lang);

        LocationNameJSON location = new LocationNameJSON();
        location.setName((String) query.uniqueResult());
        logger.debug("GeonameId: {}, Name: {}", geonameId, location.getName());
        return location;
    }

}
/*% } %*/
