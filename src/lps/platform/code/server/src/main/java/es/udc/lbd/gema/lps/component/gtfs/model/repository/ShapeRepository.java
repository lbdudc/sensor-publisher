/*% if (feature.DM_DS_GTFS) { %*/
package es.udc.lbd.gema.lps.component.gtfs.model.repository;

import es.udc.lbd.gema.lps.component.gtfs.model.domain.Shape;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ShapeRepository
    extends JpaRepository<Shape, Long>, JpaSpecificationExecutor<Shape> {

  Shape save(Shape shapes);

  Optional<Shape> findById(Long pk);

  Page<Shape> findByIdIn(List<Long> pk, Pageable pageable);
}
/*% } %*/
