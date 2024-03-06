/*% if (feature.MWM_TA_TrajectoryAnnotator) { %*/
package es.udc.lbd.gema.lps.component.sensor_data_collector.model.domain.activitytaxonomy.processingnodes;

import es.udc.lbd.gema.lps.component.sensor_data_collector.model.domain.activitytaxonomy.TaxonomyEdge;
import es.udc.lbd.gema.lps.component.sensor_data_collector.model.domain.activitytaxonomy.TaxonomyNode;
import es.udc.lbd.gema.lps.component.sensor_data_collector.model.domain.activitytaxonomy.TaxonomySegment;
import java.util.ArrayList;
import java.util.List;

public class DecisionNode extends TaxonomyNode {

  public DecisionNode() {}

  @Override
  public List<TaxonomySegment> annotate(List<TaxonomySegment> segments) {
    List<TaxonomySegment> result = new ArrayList<>();
    for (TaxonomyEdge edge : this.getChildren()) {
      List<TaxonomySegment> segmentsT = edge.evaluate(segments);
      edge.getChild().annotate(segmentsT);
      result.addAll(segmentsT);
      segments.removeAll(segmentsT);
    }
    return result;
  }
}

/*% } %*/