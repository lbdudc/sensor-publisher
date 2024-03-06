/*% if (feature.DM_DS_Address || feature.DM_A_G_Batch) { %*/
package es.udc.lbd.gema.lps.component.geolocation.model.repository;

import es.udc.lbd.gema.lps.component.geolocation.custom.CountryJSON;
import es.udc.lbd.gema.lps.component.geolocation.custom.LocationNameJSON;
import es.udc.lbd.gema.lps.component.geolocation.custom.SubdivisionJSON;
import es.udc.lbd.gema.lps.component.geolocation.custom.TownJSON;

import java.util.List;

public interface GeonameRepositoryCustom {
    /*% if (feature.DM_DS_Address) { %*/

    List<CountryJSON> findAllCountries(String lang);

    List<SubdivisionJSON> findAdm1ByCountry(String countryCode, String lang);

    List<SubdivisionJSON> findAdm2ByCodes(String countryCode, String adm1Code, String lang);

    List<SubdivisionJSON> findAdm3ByCodes(String countryCode, String adm1Code, String adm2Code, String lang);

    List<SubdivisionJSON> findAdm4ByCodes(String countryCode, String adm1Code, String adm2Code, String adm3Code, String lang);

    List<TownJSON> findTownsByCodes(String countryCode, String adm1Code, String adm2Code, String adm3Code, String adm4Code, String lang);

    /*% } %*/
    LocationNameJSON getLocationName(Integer geonameId, String lang);
}
/*% } %*/
