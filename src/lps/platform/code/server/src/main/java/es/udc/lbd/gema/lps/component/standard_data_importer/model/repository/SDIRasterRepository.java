/*% if (feature.DM_DI_DF_Raster) { %*/
package es.udc.lbd.gema.lps.component.standard_data_importer.model.repository;

import es.udc.lbd.gema.lps.component.standard_data_importer.model.domain.SDIRaster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface SDIRasterRepository
  extends JpaRepository<SDIRaster, Long>,
  JpaSpecificationExecutor<SDIRaster>,
  PagingAndSortingRepository<SDIRaster, Long> {

}
/*% } %*/
