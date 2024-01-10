/*% if (feature.MWM_TE_VisitsRecord) { %*/
package es.udc.lbd.gema.lps.component.gema.model.service.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import es.udc.lbd.gema.lps.model.util.jackson.CustomGeometryDeserializer;
import es.udc.lbd.gema.lps.model.util.jackson.CustomGeometrySerializer;
import java.util.List;
import java.util.stream.Collectors;
import org.locationtech.jts.geom.Geometry;

public class ClusteredPlannedEventsDTO {

  private Integer count;
  private List<Long> visits;

  @JsonSerialize(using = CustomGeometrySerializer.class)
  @JsonDeserialize(using = CustomGeometryDeserializer.class)
  private Geometry geom;

  public ClusteredPlannedEventsDTO() {}

  public ClusteredPlannedEventsDTO(List<Long> visits, Geometry geom) {
    this.visits = visits;
    this.count = visits.size();
    this.geom = geom;
  }

  public Integer getCount() {
    return count;
  }

  public void setCount(Integer count) {
    this.count = count;
  }

  public List<Long> getVisits() {
    return visits;
  }

  public void setVisits(List<Long> visits) {
    this.visits = visits;
  }

  public Geometry getGeom() {
    return geom;
  }

  public void setGeom(Geometry geom) {
    this.geom = geom;
  }
}
/*% } %*/
