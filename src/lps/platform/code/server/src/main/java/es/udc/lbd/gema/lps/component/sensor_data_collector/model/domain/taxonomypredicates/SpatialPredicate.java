/*% if (feature.MWM_TA_TrajectoryAnnotator) { %*/
package es.udc.lbd.gema.lps.component.sensor_data_collector.model.domain.taxonomypredicates;

import es.udc.lbd.gema.lps.component.sensor_data_collector.model.domain.Location;
import es.udc.lbd.gema.lps.component.sensor_data_collector.model.domain.activitytaxonomy.TaxonomySegment;
import es.udc.lbd.gema.lps.component.sensor_data_collector.model.domain.contextinformation.*;
import es.udc.lbd.gema.lps.component.sensor_data_collector.model.repository.SpatialFeatureCollectionRepository;
import java.util.List;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;

public class SpatialPredicate extends Predicate {

  @Autowired private SpatialFeatureCollectionRepository spatialFeatureCollectionRepository;

  private String spatialFeatureCollection;

  private Double value;

  private SpatialOperator operator;

  public SpatialPredicate(String name, SpatialOperator op) {
    this.spatialFeatureCollection = name;
    this.operator = op;
  }

  public SpatialPredicate(String name, SpatialOperator op, Double value) {
    this.spatialFeatureCollection = name;
    this.operator = op;
    this.value = value;
  }

  public SpatialPredicate() {}

  public Double getValue() {
    return value;
  }

  public void setValue(Double value) {
    this.value = value;
  }

  public SpatialOperator getOperator() {
    return operator;
  }

  public void setOperator(SpatialOperator operator) {
    this.operator = operator;
  }

  public String getSpatialFeatureCollection() {
    return spatialFeatureCollection;
  }

  public void setSpatialFeatureCollection(String spatialFeatureCollection) {
    this.spatialFeatureCollection = spatialFeatureCollection;
  }

  @Override
  public Boolean evaluate(TaxonomySegment s) {
    SpatialFeatureCollection collection =
        spatialFeatureCollectionRepository.findByName(spatialFeatureCollection);
    /*test*/
    if (this.value == null) {
      return auxEvaluate(this.operator, s.getLocations(), collection.getFeatures());
    } else {
      return auxEvaluate(this.value, s.getLocations(), collection.getFeatures());
    }
  }

  private Boolean auxEvaluate(
      Double value, List<Location> locations, Set<SpatialFeature> features) {
    // DWITHIN
    return null;
  }

  private Boolean auxEvaluate(SpatialOperator operator, List<Location> l1, Set<SpatialFeature> l2) {
    Boolean result = Boolean.FALSE;
    switch (operator) {
      case WITHIN:
        for (Location l : l1) {
          for (SpatialFeature f : l2) {
            if (l.getPosition().within(f.getPosition())) {
              result = Boolean.TRUE;
            }
          }
        }
      case CONTAINS:
        for (Location l : l1) {
          for (SpatialFeature f : l2) {
            if (l.getPosition().contains(f.getPosition())) {
              result = Boolean.TRUE;
            }
          }
        }
      case EQUALS:
        for (Location l : l1) {
          for (SpatialFeature f : l2) {
            if (l.getPosition().equals(f.getPosition())) {
              result = Boolean.TRUE;
            }
          }
        }
      case TOUCHES:
        for (Location l : l1) {
          for (SpatialFeature f : l2) {
            if (l.getPosition().touches(f.getPosition())) {
              result = Boolean.TRUE;
            }
          }
        }
      case CROSSES:
        for (Location l : l1) {
          for (SpatialFeature f : l2) {
            if (l.getPosition().crosses(f.getPosition())) {
              result = Boolean.TRUE;
            }
          }
        }
      case OVERLAPS:
        for (Location l : l1) {
          for (SpatialFeature f : l2) {
            if (l.getPosition().overlaps(f.getPosition())) {
              result = Boolean.TRUE;
            }
          }
        }
      case DISJOINT:
        for (Location l : l1) {
          for (SpatialFeature f : l2) {
            if (l.getPosition().disjoint(f.getPosition())) {
              result = Boolean.TRUE;
            }
          }
        }
      case INTERSECTS:
        for (Location l : l1) {
          for (SpatialFeature f : l2) {
            if (l.getPosition().intersects(f.getPosition())) {
              result = Boolean.TRUE;
            }
          }
        }
    }
    return result;
  }
}

/*% } %*/