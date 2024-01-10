/*% if (feature.DM_DS_Address || feature.DM_A_G_Batch) { %*/
package es.udc.lbd.gema.lps.component.geolocation.model.repository;

import es.udc.lbd.gema.lps.component.geolocation.model.domain.Geoname;

import org.springframework.data.jpa.repository.JpaRepository;

public interface GeonameRepository extends JpaRepository<Geoname, Integer>, GeonameRepositoryCustom {

}
/*% } %*/
