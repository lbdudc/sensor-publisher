/*% if (feature.DM_DS_GTFS) { %*/
package es.udc.lbd.gema.lps.component.gtfs.model.domain;

import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import es.udc.lbd.gema.lps.model.util.jackson.CustomGeometryDeserializer;
import es.udc.lbd.gema.lps.model.util.jackson.CustomGeometrySerializer;
import es.udc.lbd.gema.lps.model.views.Views;
import javax.persistence.*;
import javax.persistence.Column;
import javax.persistence.SequenceGenerator;
import org.locationtech.jts.geom.Point;

@Entity(name = "t_shapes")
@Table(name = "t_shapes")
public class Shape {
  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "shapesid")
  @SequenceGenerator(
      name = "shapesid",
      sequenceName = "t_shapes_id_seq",
      initialValue = 1,
      allocationSize = 1)
  @JsonView(Views.General.class)
  @Column(name = "id", unique = true)
  private Long id;

  @JsonView(Views.General.class)
  @Column(name = "shape_id")
  private String shapeId;

  @JsonView(Views.General.class)
  @Column(name = "shape_pt_sequence")
  private Integer shapePtSequence;

  @JsonView(Views.Detailed.class)
  @JsonSerialize(using = CustomGeometrySerializer.class)
  @JsonDeserialize(using = CustomGeometryDeserializer.class)
  @Column(name = "shape_pt_loc", columnDefinition = "geometry(Point, 4326)")
  private Point shapePtLoc;

  @JsonView(Views.General.class)
  @Column(name = "shape_dist_traveled")
  private Float shapeDistTraveled;

  public Shape() {}

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getShapeId() {
    return shapeId;
  }

  public void setShapeId(String shapeId) {
    this.shapeId = shapeId;
  }

  public Integer getShapePtSequence() {
    return shapePtSequence;
  }

  public void setShapePtSequence(Integer shapePtSequence) {
    this.shapePtSequence = shapePtSequence;
  }

  public Point getShapePtLoc() {
    return shapePtLoc;
  }

  public void setShapePtLoc(Point shapePtLoc) {
    this.shapePtLoc = shapePtLoc;
  }

  public Float getShapeDistTraveled() {
    return shapeDistTraveled;
  }

  public void setShapeDistTraveled(Float shapeDistTraveled) {
    this.shapeDistTraveled = shapeDistTraveled;
  }
}
/*% } %*/
