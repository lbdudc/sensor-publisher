/*% if (feature.MWM_TA_TrajectoryAnnotator) { %*/
package es.udc.lbd.gema.lps.component.sensor_data_collector.model.domain.activitytaxonomy.processingnodes;

import es.udc.lbd.gema.lps.component.sensor_data_collector.model.domain.Tag;
import es.udc.lbd.gema.lps.component.sensor_data_collector.model.domain.activitytaxonomy.TaxonomyNode;
import es.udc.lbd.gema.lps.component.sensor_data_collector.model.domain.activitytaxonomy.TaxonomySegment;
import java.util.List;

public class TagNode extends TaxonomyNode {

  private String tag;

  public TagNode() {}

  public TagNode(String string) {
    this.tag = string;
  }

  public String getTag() {
    return tag;
  }

  public void setTag(String tag) {
    this.tag = tag;
  }

  @Override
  public List<TaxonomySegment> annotate(List<TaxonomySegment> segments) {
    Tag t = new Tag();
    t.setName(tag);
    for (TaxonomySegment segment : segments) {
      segment.setTag(t);
    }
    return segments;
  }
}

/*% } %*/