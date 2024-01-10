/*% if (feature.DM_DS_Address || feature.DM_A_G_Batch) { %*/
package es.udc.lbd.gema.lps.component.geolocation;

/*% if (feature.DM_A_G_GoogleMaps) { %*/
import es.udc.lbd.gema.lps.component.geolocation.auto_assign.AutoAssignLocationGoogleMapsService;
/*% } %*/
/*% if (feature.DM_A_G_Nominatim) { %*/
import es.udc.lbd.gema.lps.component.geolocation.auto_assign.AutoAssignLocationNominatimService;
/*% } %*/
/*% if (feature.DM_A_G_Batch) { %*/
import es.udc.lbd.gema.lps.component.geolocation.auto_assign.AutoAssignLocationService;
import es.udc.lbd.gema.lps.component.geolocation.custom.AutoAssignJSON;
import es.udc.lbd.gema.lps.component.geolocation.custom.AutoAssignResultJSON;
import es.udc.lbd.gema.lps.component.geolocation.exceptions.GeolocationException;
import es.udc.lbd.gema.lps.component.geolocation.exceptions.TypeNotSupportedException;
import es.udc.lbd.gema.lps.component.geolocation.exceptions.client.GeolocatorNotSupportedException;

import java.util.HashSet;
import java.util.Set;
import org.springframework.web.bind.annotation.RequestBody;
/*% } %*/
/*% if (feature.DM_DS_Address) { %*/
import es.udc.lbd.gema.lps.component.geolocation.custom.CountryJSON;
import es.udc.lbd.gema.lps.component.geolocation.custom.LocationNameJSON;
import es.udc.lbd.gema.lps.component.geolocation.custom.SubdivisionJSON;
import es.udc.lbd.gema.lps.component.geolocation.custom.TownJSON;

import java.util.List;
import java.util.Locale;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
/*% } %*/


import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(GeolocationRestController.LOCATION_URL)
public class GeolocationRestController {

    public final static String LOCATION_URL = "/api/geolocation";

    private static final Logger logger = LoggerFactory.getLogger(GeolocationRestController.class);
    /*% if (feature.DM_A_G_Nominatim) { %*/
    @Inject
    private AutoAssignLocationNominatimService autoAssignLocationNominatimService;
    /*% } %*/
    /*% if (feature.DM_A_G_GoogleMaps) { %*/
    @Inject
    private AutoAssignLocationGoogleMapsService autoAssignLocationGoogleMapsService;
    /*% } %*/
    /*% if (feature.DM_DS_Address) { %*/
    @Autowired
    private GeolocationService geolocationService;

    /**
     * @return All countries in 'locale' language
     */
    @RequestMapping(value = "/countries", method = RequestMethod.GET)
    public List<CountryJSON> getCountries(Locale locale) {
        logger.debug(LOCATION_URL + "/countries called (Locale '{}')", locale.getLanguage());
        return geolocationService.getCountriesByLocale(locale.getLanguage());
    }

    /**
     * @param countryCode Country code. (Ex: 'UG')
     * @return First order administrative division of the country. Ex: State, 'CCAA'
     */
    @RequestMapping(value = "/id/{countryCode}", method = RequestMethod.GET)
    public List<SubdivisionJSON> getAdm1ByCountry(@PathVariable("countryCode") String countryCode, Locale locale) {
        logger.debug(LOCATION_URL + "/id/{} called (Locale '{}')", countryCode, locale.getLanguage());
        return geolocationService.getAdm1ByCountry(countryCode, locale.getLanguage());
    }

    /**
     * @param countryCode Country code (Ex: 'UG')
     * @param adm1code    First order administrative division code. (Ex: 'E')
     * @return Second order administrative divisions of the country. Ex: Provincia
     */
    @RequestMapping(value = "/id/{countryCode}/{adm1code}", method = RequestMethod.GET)
    public List<SubdivisionJSON> getAdm2ByCodes(
        @PathVariable("countryCode") String countryCode,
        @PathVariable("adm1code") String adm1code,
        Locale locale) {
        logger.debug(LOCATION_URL + "/id/{}/{} called (Locale '{}')", countryCode, adm1code, locale.getLanguage());
        return geolocationService.getAdm2ByCodes(countryCode, adm1code, locale.getLanguage());
    }

    /**
     * @param countryCode Country code (Ex. 'UG')
     * @param adm1code    First order administrative division code. (Ex: 'E')
     * @param adm2code    Second order administrative division code. (Ex: 'G9')
     * @return Third order administrative divisions of the country. Ex: Provincia
     */
    @RequestMapping(value = "/id/{countryCode}/{adm1code}/{adm2code}", method = RequestMethod.GET)
    public List<SubdivisionJSON> getAdm3ByCodes(
        @PathVariable("countryCode") String countryCode,
        @PathVariable("adm1code") String adm1code,
        @PathVariable("adm2code") String adm2code,
        Locale locale) {
        logger.debug(LOCATION_URL + "/id/{}/{}/{} called (Locale '{}')", countryCode, adm1code, adm2code, locale.getLanguage());
        return geolocationService.getAdm3ByCodes(countryCode, adm1code, adm2code, locale.getLanguage());
    }

    /**
     * @param countryCode Country code (Ex. 'UG')
     * @param adm1code    First order administrative division code. (Ex: 'E')
     * @param adm2code    Second order administrative division code. (Ex: 'G9')
     * @param adm3code    Third order administrative division code. (Ex: '7732908')
     * @return Fourth order administrative divisions of the country. Ex: Provincia
     */
    @RequestMapping(value = "/id/{countryCode}/{adm1code}/{adm2code}/{adm3code}", method = RequestMethod.GET)
    public List<SubdivisionJSON> getAdm4ByCodes(
        @PathVariable("countryCode") String countryCode,
        @PathVariable("adm1code") String adm1code,
        @PathVariable("adm2code") String adm2code,
        @PathVariable("adm3code") String adm3code,
        Locale locale) {
        logger.debug(LOCATION_URL + "/id/{}/{}/{}/{} called (Locale '{}')", countryCode, adm1code, adm2code, adm3code, locale.getLanguage());
        return geolocationService.getAdm4ByCodes(countryCode, adm1code, adm2code, adm3code, locale.getLanguage());
    }

    /**
     * @param countryCode Country code (Ex. 'UG')
     * @param adm1code    First order administrative division code. (Ex: 'E')
     * @param adm2code    Second order administrative division code. (Ex: 'G9')
     * @param adm3code    Third order administrative division code. (Ex: '7732908')
     * @param adm4code    Fourth order administrative division code. (Ex: '227840')
     * @return Towns of the administrative division of the country.
     */
    @RequestMapping(value = "/id/{countryCode}/{adm1code}/{adm2code}/{adm3code}/{adm4code}", method = RequestMethod.GET)
    public List<TownJSON> getTownsByCodes(
        @PathVariable("countryCode") String countryCode,
        @PathVariable("adm1code") String adm1code,
        @PathVariable("adm2code") String adm2code,
        @PathVariable("adm3code") String adm3code,
        @PathVariable("adm4code") String adm4code,
        Locale locale) {
        logger.debug(LOCATION_URL + "/id/{}/{}/{}/{}/{} called (Locale '{}')", countryCode, adm1code, adm2code, adm3code, adm4code, locale.getLanguage());

        String adm1 = adm1code.equals("null") ? null : adm1code;
        String adm2 = adm2code.equals("null") ? null : adm2code;
        String adm3 = adm3code.equals("null") ? null : adm3code;
        String adm4 = adm4code.equals("null") ? null : adm4code;
        return geolocationService.getTownsByCodes(countryCode, adm1, adm2, adm3, adm4, locale.getLanguage());
    }

    /**
     * @return Name in specified language or generic name if no name was found
     * in <code>alternatename</code> table for specified language.
     */
    @RequestMapping(value = "/name/{geonameId}", method = RequestMethod.GET)
    public LocationNameJSON getAlternateName(@PathVariable("geonameId") Integer geonameId, Locale locale) {
        return geolocationService.getLocationName(geonameId, locale.getLanguage());
    }
    /*% } %*/
    /*% if (feature.DM_A_G_Batch) { %*/

    /**
     * Geolocate entities using fields specified in data field
     *
     * @param data Parameters to use in geolocation service
     */
    @RequestMapping(value = "/autoassign", method = RequestMethod.POST)
    public AutoAssignResultJSON autoassignLocations(@RequestBody AutoAssignJSON data) throws GeolocationException {
        logger.debug(LOCATION_URL + "/autoassign called");

        Set<AutoAssignLocationService> assigners = new HashSet<AutoAssignLocationService>();
        /*% if (feature.DM_A_G_Nominatim) { %*/
        assigners.add(autoAssignLocationNominatimService);
        /*% } %*/
        /*% if (feature.DM_A_G_GoogleMaps) { %*/
        assigners.add(autoAssignLocationGoogleMapsService);
        /*% } %*/

        for (AutoAssignLocationService assigner : assigners) {
            try {
                return assigner.process(data);
            } catch (TypeNotSupportedException e) {
                // Try with next geolocater
            }
        }
        throw new GeolocatorNotSupportedException();
    }

    /*% } %*/
}
/*% } %*/
