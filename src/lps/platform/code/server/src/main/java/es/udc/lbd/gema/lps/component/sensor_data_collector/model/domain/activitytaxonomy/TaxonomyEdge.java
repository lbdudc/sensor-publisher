/*% if (feature.MWM_TA_TrajectoryAnnotator) { %*/
    package es.udc.lbd.gema.lps.component.sensor_data_collector.model.domain.activitytaxonomy;

import com.fasterxml.jackson.annotation.JsonIgnore;
import es.udc.lbd.gema.lps.component.sensor_data_collector.model.domain.taxonomypredicates.Predicate;
import java.util.ArrayList;
import java.util.List;

public class TaxonomyEdge {

  private Long id;

  private TaxonomyNode child;

  @JsonIgnore // Avoid infinite recursive
  private TaxonomyNode parent;

  private Predicate predicate;

  public TaxonomyEdge() {
    super();
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public TaxonomyNode getChild() {
    return child;
  }

  public void setChild(TaxonomyNode child) {
    this.child = child;
  }

  public Predicate getPredicate() {
    return predicate;
  }

  public void setPredicate(Predicate predicate) {
    this.predicate = predicate;
  }

  public List<TaxonomySegment> evaluate(List<TaxonomySegment> segments) {
    List<TaxonomySegment> segmentsT = new ArrayList<>();
    for (TaxonomySegment segment : segments) {
      if (this.predicate.evaluate(segment)) {
        segmentsT.add(segment);
      }
    }
    return segmentsT;
  }

  public TaxonomyNode getParent() {
    return parent;
  }

  public void setParent(TaxonomyNode parent) {
    this.parent = parent;
  }
}

/*% } %*/