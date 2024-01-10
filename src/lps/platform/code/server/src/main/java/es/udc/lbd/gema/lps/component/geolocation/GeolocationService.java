/*% if (feature.DM_DS_Address) { %*/
package es.udc.lbd.gema.lps.component.geolocation;

import es.udc.lbd.gema.lps.component.geolocation.custom.CountryJSON;
import es.udc.lbd.gema.lps.component.geolocation.custom.LocationNameJSON;
import es.udc.lbd.gema.lps.component.geolocation.custom.SubdivisionJSON;
import es.udc.lbd.gema.lps.component.geolocation.custom.TownJSON;

import java.util.List;

public interface GeolocationService {

    List<CountryJSON> getCountriesByLocale(String lang);

    List<SubdivisionJSON> getAdm1ByCountry(String countryCode, String lang);

    List<SubdivisionJSON> getAdm2ByCodes(String countryCode, String adm1Code, String lang);

    List<SubdivisionJSON> getAdm3ByCodes(String countryCode, String adm1Code, String adm2Code, String lang);

    List<SubdivisionJSON> getAdm4ByCodes(String countryCode, String adm1Code, String adm2Code, String adm3Code, String lang);

    List<TownJSON> getTownsByCodes(String countryCode, String adm1Code, String adm2Code, String adm3Code, String adm4Code, String lang);

    LocationNameJSON getLocationName(Integer geonameId, String lang);
}
/*% } %*/
