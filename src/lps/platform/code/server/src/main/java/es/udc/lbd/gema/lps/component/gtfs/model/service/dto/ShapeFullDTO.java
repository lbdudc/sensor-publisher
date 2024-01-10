/*% if (feature.DM_DS_GTFS) { %*/
package es.udc.lbd.gema.lps.component.gtfs.model.service.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import es.udc.lbd.gema.lps.component.gtfs.model.domain.Shape;
import es.udc.lbd.gema.lps.model.domain.*;
import es.udc.lbd.gema.lps.model.util.jackson.CustomGeometryDeserializer;
import es.udc.lbd.gema.lps.model.util.jackson.CustomGeometrySerializer;
import org.locationtech.jts.geom.Point;

public class ShapeFullDTO {
  private Long id;
  private String shapeId;
  private Integer shapePtSequence;

  @JsonSerialize(using = CustomGeometrySerializer.class)
  @JsonDeserialize(using = CustomGeometryDeserializer.class)
  private Point shapePtLoc;

  private Float shapeDistTraveled;

  public ShapeFullDTO() {}

  public ShapeFullDTO(Shape shape) {
    this.id = shape.getId();
    this.shapeId = shape.getShapeId();
    this.shapePtSequence = shape.getShapePtSequence();
    this.shapePtLoc = shape.getShapePtLoc();
    this.shapeDistTraveled = shape.getShapeDistTraveled();
  }

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

  public Shape toShape() {
    Shape shape = new Shape();
    shape.setId(this.getId());
    shape.setShapeId(this.getShapeId());
    shape.setShapePtSequence(this.getShapePtSequence());
    shape.setShapePtLoc(this.getShapePtLoc());
    shape.setShapeDistTraveled(this.getShapeDistTraveled());
    return shape;
  }
}
/*% } %*/
