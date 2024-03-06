/*% if (feature.MWM_TA_TrajectoryAnnotator) { %*/
package es.udc.lbd.gema.lps.component.sensor_data_collector.model.domain.state_machine;

public enum EventState {
  NO_SEGMENT_LOCATIONS {
    @Override
    public EventState nextState(Event event) {
      switch (event.getType()) {
        case LOCATION:
          return EventState.NO_SEGMENT_LOCATIONS;
        case SEGMENT_START:
          return EventState.IN_SEGMENT;
        default:
          return null;
      }
    }
  },

  IN_SEGMENT {
    @Override
    public EventState nextState(Event event) {
      switch (event.getType()) {
        case LOCATION:
          return EventState.IN_SEGMENT;
        case SEGMENT_END:
          return EventState.BEGINING;
        default:
          return null;
      }
    }
  },

  BEGINING {
    @Override
    public EventState nextState(Event event) {
      switch (event.getType()) {
        case LOCATION:
          return EventState.NO_SEGMENT_LOCATIONS;
        case SEGMENT_START:
          return EventState.IN_SEGMENT;
        default:
          return null;
      }
    }
  };

  public abstract EventState nextState(Event event);
}

/*% } %*/