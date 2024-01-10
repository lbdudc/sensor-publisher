/*% if (feature.MWM_TrajectoryExploitation) { %*/
package es.udc.lbd.gema.lps.web.rest.util.specification_utils.spatial_specifications;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import org.hibernate.spatial.predicate.SpatialPredicates;
import org.locationtech.jts.geom.Geometry;
import org.springframework.data.jpa.domain.Specification;

public class WithinSpecification<T> implements Specification<T> {

  private Geometry geometry;
  private String propertyName;

  public WithinSpecification(Geometry geometry, String propertyName) {
    this.geometry = geometry;
    this.propertyName = propertyName;
  }

  @Override
  public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder builder) {
    if (geometry != null) {
      return SpatialPredicates.within(builder, root.get(propertyName), geometry);
    }
    return null;
  }
}
/*% } %*/
