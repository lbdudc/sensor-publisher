/*% if (feature.DM_DS_Address) { %*/
package es.udc.lbd.gema.lps.component.geolocation;

import es.udc.lbd.gema.lps.component.geolocation.custom.CountryJSON;
import es.udc.lbd.gema.lps.component.geolocation.custom.LocationNameJSON;
import es.udc.lbd.gema.lps.component.geolocation.custom.SubdivisionJSON;
import es.udc.lbd.gema.lps.component.geolocation.custom.TownJSON;
import es.udc.lbd.gema.lps.component.geolocation.model.repository.GeonameRepository;

import java.util.List;

import javax.inject.Inject;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
public class GeolocationServiceImpl implements GeolocationService {

    @Inject
    GeonameRepository geonameRepository;

    @Override
    public List<CountryJSON> getCountriesByLocale(String lang) {
        return geonameRepository.findAllCountries(lang);
    }

    @Override
    public List<SubdivisionJSON> getAdm1ByCountry(String countryCode, String lang) {
        return geonameRepository.findAdm1ByCountry(countryCode, lang);
    }

    @Override
    public List<SubdivisionJSON> getAdm2ByCodes(String countryCode, String adm1Code, String lang) {
        return geonameRepository.findAdm2ByCodes(countryCode, adm1Code, lang);
    }

    @Override
    public List<SubdivisionJSON> getAdm3ByCodes(String countryCode, String adm1Code, String adm2Code, String lang) {
        return geonameRepository.findAdm3ByCodes(countryCode, adm1Code, adm2Code, lang);
    }

    @Override
    public List<SubdivisionJSON> getAdm4ByCodes(String countryCode, String adm1Code, String adm2Code, String adm3Code, String lang) {
        return geonameRepository.findAdm4ByCodes(countryCode, adm1Code, adm2Code, adm3Code, lang);
    }

    @Override
    public List<TownJSON> getTownsByCodes(String countryCode, String adm1Code, String adm2Code, String adm3Code, String adm4Code, String lang) {
        return geonameRepository.findTownsByCodes(countryCode, adm1Code, adm2Code, adm3Code, adm4Code, lang);
    }

    @Override
    public LocationNameJSON getLocationName(Integer geonameId, String lang) {
        return geonameRepository.getLocationName(geonameId, lang);
    }

}
/*% } %*/
