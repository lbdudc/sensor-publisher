/*% if (feature.MWM_TA_TrajectoryAnnotator) { %*/
package es.udc.lbd.gema.lps.component.sensor_data_collector.model.domain.activitytaxonomy.processingnodes;

import es.udc.lbd.gema.lps.component.sensor_data_collector.model.domain.Tag;
import es.udc.lbd.gema.lps.component.sensor_data_collector.model.domain.activitytaxonomy.TaxonomyEdge;
import es.udc.lbd.gema.lps.component.sensor_data_collector.model.domain.activitytaxonomy.TaxonomyNode;
import es.udc.lbd.gema.lps.component.sensor_data_collector.model.domain.activitytaxonomy.TaxonomySegment;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class GroupNode extends TaxonomyNode {

  private Long duration;
  private final String trueTag;
  private final String falseTag;

  public GroupNode(Long duration) {
    trueTag = "cumple";
    falseTag = "no cumple";
    this.duration = duration;
  }

  public Long getDuration() {
    return duration;
  }

  public void setDuration(Long duration) {
    this.duration = duration;
  }

  public String getTrueTag() {
    return trueTag;
  }

  public String getFalseTag() {
    return falseTag;
  }

  @Override
  public List<TaxonomySegment> annotate(List<TaxonomySegment> segments) {
    List<TaxonomySegment> result = new ArrayList<>(segments);
    consecutiveAnnotation(segments);

    for (TaxonomyEdge edge : this.getChildren()) {
      List<TaxonomySegment> segmentsT = edge.evaluate(segments);
      result.removeAll(segmentsT);
      edge.getChild().annotate(segmentsT);
    }
    return result;
  }

  private void consecutiveAnnotation(List<TaxonomySegment> segments) {
    List<TaxonomySegment> consecutive = new ArrayList<>();
    if (segments.size() < 2) {
      consecutive.add(segments.get(0));
      auxAnnotation(consecutive, this.duration);
    } else {
      for (int i = 0; i < segments.size(); i++) {
        if (i == (segments.size() - 1)) {
          if (!segments.get(i).getStartTime().equals(segments.get(i - 1).getEndTime())) {
            consecutive.add(segments.get(i));
            auxAnnotation(consecutive, this.duration);
            break;
          }
        }
        if (segments.get(i).getEndTime().equals(segments.get(i + 1).getStartTime())) {
          consecutive.add(segments.get(i));
          continue;
        } else {
          consecutive.add(segments.get(i));
        }
        auxAnnotation(consecutive, this.duration);
        consecutive.clear();
      }
    }

    /*
     * List<TaxonomySegment> consecutive = new ArrayList<>(); TaxonomySegment prev =
     * segments.get(0); consecutive.add(prev); int count = 1; for (int i = 1; i <
     * segments.size(); i++) { TaxonomySegment next = segments.get(i); if
     * (prev.getEndTime().equals(next.getStartTime())) { consecutive.add(next);
     * count++; } else { if (count > 1) { auxAnnotation(consecutive, this.duration);
     * } consecutive.clear(); consecutive.add(next); count = 1; } prev = next; }
     */
  }

  private void auxAnnotation(List<TaxonomySegment> consecutive, Long duration) {
    Long totalDuration = 0L;
    for (TaxonomySegment segment : consecutive) {
      totalDuration +=
          (localDateTimeToMillis(segment.getEndTime())
              - localDateTimeToMillis(segment.getStartTime()));
    }
    totalDuration = TimeUnit.MILLISECONDS.toMinutes(totalDuration);
    if (totalDuration <= duration) {
      tagging(consecutive, this.falseTag);
    } else {
      tagging(consecutive, this.trueTag);
    }
  }

  private void tagging(List<TaxonomySegment> consecutive, String tag) {
    Tag t = new Tag(tag);
    for (TaxonomySegment segment : consecutive) {
      segment.setTag(t);
    }
  }

  private Long localDateTimeToMillis(LocalDateTime localDateTime) {
    return localDateTime.toInstant(ZoneOffset.UTC).toEpochMilli();
  }
}

/*% } %*/