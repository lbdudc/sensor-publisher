/*% if (feature.MWM_TA_TrajectoryAnnotator) { %*/
package es.udc.lbd.gema.lps.component.sensor_data_collector.model.domain.activitytaxonomy;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import es.udc.lbd.gema.lps.component.sensor_data_collector.model.domain.activitytaxonomy.processingnodes.DecisionNode;
import es.udc.lbd.gema.lps.component.sensor_data_collector.model.domain.activitytaxonomy.processingnodes.GroupNode;
import es.udc.lbd.gema.lps.component.sensor_data_collector.model.domain.activitytaxonomy.processingnodes.TagNode;
import java.util.List;

@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.PROPERTY,
    property = "node_type")
@JsonSubTypes({
  @Type(value = DecisionNode.class, name = "Decision"),
  @Type(value = GroupNode.class, name = "Group"),
  @Type(value = TagNode.class, name = "Tag")
})
public abstract class TaxonomyNode {

  private Long id;
  private List<TaxonomyEdge> children;

  public TaxonomyNode() {}

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public List<TaxonomyEdge> getChildren() {
    return children;
  }

  public void setChildren(List<TaxonomyEdge> children) {
    this.children = children;
  }

  public abstract List<TaxonomySegment> annotate(List<TaxonomySegment> segments);
}

/*% } %*/