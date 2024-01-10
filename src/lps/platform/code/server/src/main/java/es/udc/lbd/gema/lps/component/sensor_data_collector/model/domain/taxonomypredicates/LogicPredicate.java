/*% if (feature.MWM_TA_TrajectoryAnnotator) { %*/
package es.udc.lbd.gema.lps.component.sensor_data_collector.model.domain.taxonomypredicates;

import es.udc.lbd.gema.lps.component.sensor_data_collector.model.domain.activitytaxonomy.TaxonomySegment;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.OneToMany;

public class LogicPredicate extends Predicate {

  private logicOperator op;

  @OneToMany private Set<Predicate> children = new HashSet<>();

  public LogicPredicate() {}

  public logicOperator getOp() {
    return op;
  }

  public void setOp(logicOperator op) {
    this.op = op;
  }

  public Set<Predicate> getChildren() {
    return children;
  }

  public void setChildren(Set<Predicate> children) {
    this.children = children;
  }

  @Override
  public Boolean evaluate(TaxonomySegment s) {
    Boolean result = null;
    switch (this.op) {
      case AND:
        result = true;
        for (Predicate predicate : children) {
          result = result && predicate.evaluate(s);
        }
      case OR:
        result = false;
        for (Predicate predicate : children) {
          if (predicate.evaluate(s)) result = Boolean.TRUE;
        }
      case NOT:
        for (Predicate predicate : children) {
          result = !predicate.evaluate(s);
        }
    }
    return result;
  }
}

/*% } %*/