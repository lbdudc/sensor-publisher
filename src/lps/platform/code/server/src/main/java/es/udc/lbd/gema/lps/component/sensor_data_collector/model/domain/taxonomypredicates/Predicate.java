/*% if (feature.MWM_TA_TrajectoryAnnotator) { %*/
package es.udc.lbd.gema.lps.component.sensor_data_collector.model.domain.taxonomypredicates;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import es.udc.lbd.gema.lps.component.sensor_data_collector.model.domain.activitytaxonomy.TaxonomySegment;

@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.PROPERTY,
    property = "predicate_type")
@JsonSubTypes({
  @Type(value = ConstantPredicate.class, name = "Constant"),
  @Type(value = CountPointsPredicate.class, name = "Count Points"),
  @Type(value = GpsPredicate.class, name = "Gps"),
  @Type(value = HastagPredicate.class, name = "Has Tag"),
  @Type(value = LogicPredicate.class, name = "Logic"),
  @Type(value = NetworkSpeedPredicate.class, name = "Network Speed"),
  @Type(value = SpatialPredicate.class, name = "Spatial"),
})
public abstract class Predicate {

  private Long id;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public abstract Boolean evaluate(TaxonomySegment s);
}

/*% } %*/