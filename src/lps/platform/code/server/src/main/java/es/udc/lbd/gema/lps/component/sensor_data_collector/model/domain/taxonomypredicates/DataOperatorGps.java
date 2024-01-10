/*% if (feature.MWM_TA_TrajectoryAnnotator) { %*/
package es.udc.lbd.gema.lps.component.sensor_data_collector.model.domain.taxonomypredicates;

public enum DataOperatorGps {
  GREATER {
    @Override
    public Boolean evaluate(Double locAtribute, Double value) {
      // TODO Auto-generated method stub
      return null;
    }
  },
  GREATER_EQUAL {
    @Override
    public Boolean evaluate(Double locAtribute, Double value) {
      // TODO Auto-generated method stub
      return null;
    }
  },
  EQUAL {
    @Override
    public Boolean evaluate(Double locAtribute, Double value) {
      // TODO Auto-generated method stub
      return null;
    }
  },
  LESS_EQUAL {
    @Override
    public Boolean evaluate(Double locAtribute, Double value) {
      // TODO Auto-generated method stub
      return null;
    }
  },
  LESS {
    @Override
    public Boolean evaluate(Double locAtribute, Double value) {
      // TODO Auto-generated method stub
      return null;
    }
  },
  DISTINCT {
    @Override
    public Boolean evaluate(Double locAtribute, Double value) {
      // TODO Auto-generated method stub
      return null;
    }
  };

  public abstract Boolean evaluate(Double locAtribute, Double value);
}

/*% } %*/