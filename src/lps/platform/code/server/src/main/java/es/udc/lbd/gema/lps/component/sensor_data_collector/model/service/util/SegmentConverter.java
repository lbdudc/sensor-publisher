/*% if (feature.MWM_TA_TrajectoryAnnotator) { %*/
package es.udc.lbd.gema.lps.component.sensor_data_collector.model.service.util;

import es.udc.lbd.gema.lps.component.sensor_data_collector.model.domain.Location;
import es.udc.lbd.gema.lps.component.sensor_data_collector.model.domain.Segment;
import es.udc.lbd.gema.lps.component.sensor_data_collector.model.domain.SegmentFusion;
import es.udc.lbd.gema.lps.component.sensor_data_collector.model.domain.activitytaxonomy.TaxonomySegment;
import es.udc.lbd.gema.lps.component.sensor_data_collector.model.domain.state_machine.Event;
import es.udc.lbd.gema.lps.component.sensor_data_collector.model.domain.state_machine.EventState;
import es.udc.lbd.gema.lps.component.sensor_data_collector.model.domain.state_machine.EventType;
import es.udc.lbd.gema.lps.component.sensor_data_collector.model.repository.SegmentFusionRepository;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import javax.inject.Inject;
import javax.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class SegmentConverter {

  @Inject private SegmentFusionRepository segmentFusionRepository;

  public List<TaxonomySegment> toTaxonomySegmentList(
      List<Segment> segmentList, List<Location> locationList) {

    EventState eventState = EventState.BEGINING;
    List<TaxonomySegment> taxonomySegmentList = new ArrayList<>();
    List<Event> events = createEvents(segmentList, locationList);
    TaxonomySegment mockSegment = new TaxonomySegment();

    for (Event event : events) {
      // Perform actions before transition
      switch (eventState) {
        case BEGINING:
          switch (event.getType()) {
            case SEGMENT_START:
              mockSegment = new TaxonomySegment();
              mockSegment.setStartTime(event.getTime());
              mockSegment.setTag(event.getSegment().getTag());
              mockSegment.setEmployee(event.getSegment().getEmployee());
              break;
            case LOCATION:
              mockSegment = new TaxonomySegment();
              mockSegment.setStartTime(event.getTime());
              mockSegment.setEndTime(event.getTime());
              mockSegment.setTag(null); // Undefined //Convert to individual item
              mockSegment.getLocations().add(event.getLocation());
              mockSegment.setEmployee(event.getLocation().getEmployee());
              break;
          }
          break;
        case IN_SEGMENT:
          switch (event.getType()) {
            case SEGMENT_END:
              mockSegment.setEndTime(event.getTime());
              segmentFusionRepository.save(new SegmentFusion(mockSegment));
              taxonomySegmentList.add(mockSegment);
              break;
            case LOCATION:
              mockSegment.setEndTime(event.getTime());
              segmentFusionRepository.save(new SegmentFusion(mockSegment));
              taxonomySegmentList.add(mockSegment);
              TaxonomySegment oldMockSegment =
                  taxonomySegmentList.get(taxonomySegmentList.size() - 1);
              mockSegment = new TaxonomySegment();
              mockSegment.setStartTime(event.getTime());
              mockSegment.setTag(oldMockSegment.getTag());
              mockSegment.getLocations().add(event.getLocation());
              mockSegment.setEmployee(event.getLocation().getEmployee());
              break;
          }
          break;
        case NO_SEGMENT_LOCATIONS:
          switch (event.getType()) {
            case SEGMENT_START:
              mockSegment.setEndTime(event.getTime());
              segmentFusionRepository.save(new SegmentFusion(mockSegment));
              taxonomySegmentList.add(mockSegment);
              TaxonomySegment oldMockSegment =
                  taxonomySegmentList.get(taxonomySegmentList.size() - 1);
              mockSegment = new TaxonomySegment();
              mockSegment.setStartTime(event.getTime());
              mockSegment.setTag(event.getSegment().getTag());
              mockSegment.setEmployee(event.getSegment().getEmployee());
              mockSegment
                  .getLocations()
                  .add(
                      oldMockSegment
                          .getLocations()
                          .get(oldMockSegment.getLocations().size() - 1)); // Convert to list
              break;
            case LOCATION:
              mockSegment.setEndTime(event.getTime());
              segmentFusionRepository.save(new SegmentFusion(mockSegment));
              taxonomySegmentList.add(mockSegment);
              mockSegment = new TaxonomySegment();
              mockSegment.setStartTime(event.getTime());
              mockSegment.setEndTime(event.getTime());
              mockSegment.setTag(null); // Undefined
              mockSegment.getLocations().add(event.getLocation());
              mockSegment.setEmployee(event.getLocation().getEmployee());
              break;
          }
          break;
      }
      // Transition
      eventState = eventState.nextState(event);
    }

    return taxonomySegmentList;
  }

  private static List<Event> createEvents(List<Segment> segmentList, List<Location> locationList) {
    List<Event> events = new ArrayList<>();
    for (Iterator<Segment> iterator = segmentList.iterator(); iterator.hasNext(); ) {
      Segment segment = (Segment) iterator.next();
      Event startEvent = new Event();
      Event endEvent = new Event();

      startEvent.setType(EventType.SEGMENT_START);
      startEvent.setTime(segment.getStartTime());
      startEvent.setSegment(segment);

      endEvent.setType(EventType.SEGMENT_END);
      endEvent.setTime(segment.getEndTime());

      events.add(startEvent);
      events.add(endEvent);
    }
    for (Iterator<Location> iterator = locationList.iterator(); iterator.hasNext(); ) {
      Location location = (Location) iterator.next();
      Event locationEvent = new Event();

      locationEvent.setType(EventType.LOCATION);
      locationEvent.setTime(location.getTime());
      locationEvent.setLocation(location);

      events.add(locationEvent);
    }

    Collections.sort(events);

    return events;
  }
}

/*% } %*/