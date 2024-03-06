/*% if (feature.MWM_TA_TrajectoryAnnotator) { %*/
package es.udc.lbd.gema.lps.component.sensor_data_collector.model.domain.taxonomypredicates;

import es.udc.lbd.gema.lps.component.sensor_data_collector.model.domain.Location;
import es.udc.lbd.gema.lps.component.sensor_data_collector.model.domain.contextinformation.SpatialFeature;
import java.util.Set;

public enum SpatialOperator {
  WITHIN {

    @Override
    public Boolean evaluate(Set<Location> l1, Set<SpatialFeature> l2) {
      Boolean result = Boolean.FALSE;
      for (Location l : l1) {
        for (SpatialFeature f : l2) {
          if (l.getPosition().within(f.getPosition())) {
            result = Boolean.TRUE;
          }
        }
      }
      return result;
    }
  },
  CONTAINS {

    @Override
    public Boolean evaluate(Set<Location> l1, Set<SpatialFeature> l2) {
      Boolean result = Boolean.FALSE;
      for (Location l : l1) {
        for (SpatialFeature f : l2) {
          if (l.getPosition().contains(f.getPosition())) {
            result = Boolean.TRUE;
          }
        }
      }
      return result;
    }
  },
  EQUALS {

    @Override
    public Boolean evaluate(Set<Location> l1, Set<SpatialFeature> l2) {
      Boolean result = Boolean.FALSE;
      for (Location l : l1) {
        for (SpatialFeature f : l2) {
          if (l.getPosition().equals(f.getPosition())) {
            result = Boolean.TRUE;
          }
        }
      }
      return result;
    }
  },
  TOUCHES {

    @Override
    public Boolean evaluate(Set<Location> l1, Set<SpatialFeature> l2) {
      Boolean result = Boolean.FALSE;
      for (Location l : l1) {
        for (SpatialFeature f : l2) {
          if (l.getPosition().touches(f.getPosition())) {
            result = Boolean.TRUE;
          }
        }
      }
      return result;
    }
  },
  CROSSES {

    @Override
    public Boolean evaluate(Set<Location> l1, Set<SpatialFeature> l2) {

      Boolean result = Boolean.FALSE;
      for (Location l : l1) {
        for (SpatialFeature f : l2) {
          if (l.getPosition().crosses(f.getPosition())) {
            result = Boolean.TRUE;
          }
        }
      }
      return result;
    }
  },
  OVERLAPS {

    @Override
    public Boolean evaluate(Set<Location> l1, Set<SpatialFeature> l2) {

      Boolean result = Boolean.FALSE;
      for (Location l : l1) {
        for (SpatialFeature f : l2) {
          if (l.getPosition().overlaps(f.getPosition())) {
            result = Boolean.TRUE;
          }
        }
      }
      return result;
    }
  },
  DISJOINT {

    @Override
    public Boolean evaluate(Set<Location> l1, Set<SpatialFeature> l2) {
      Boolean result = Boolean.FALSE;
      for (Location l : l1) {
        for (SpatialFeature f : l2) {
          if (l.getPosition().disjoint(f.getPosition())) {
            result = Boolean.TRUE;
          }
        }
      }
      return result;
    }
  },
  INTERSECTS {

    @Override
    public Boolean evaluate(Set<Location> l1, Set<SpatialFeature> l2) {
      Boolean result = Boolean.FALSE;
      for (Location l : l1) {
        for (SpatialFeature f : l2) {
          if (l.getPosition().intersects(f.getPosition())) {
            result = Boolean.TRUE;
          }
        }
      }
      return result;
    }
  },
  DWITHIN {

    @Override
    public Boolean evaluate(Set<Location> l1, Set<SpatialFeature> l2) {
      // TODO Auto-generated method stub
      return null;
    }
  };

  public abstract Boolean evaluate(Set<Location> l1, Set<SpatialFeature> l2);
}

/*% } %*/