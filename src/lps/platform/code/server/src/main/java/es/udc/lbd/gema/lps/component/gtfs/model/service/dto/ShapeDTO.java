/*% if (feature.DM_DS_GTFS) { %*/
package es.udc.lbd.gema.lps.component.gtfs.model.service.dto;

import es.udc.lbd.gema.lps.component.gtfs.model.domain.Shape;
import es.udc.lbd.gema.lps.model.domain.*;

public class ShapeDTO {

  private Long id;
  private String shapeId;
  private Integer shapePtSequence;
  private Float shapeDistTraveled;

  public ShapeDTO() {}

  public ShapeDTO(Shape shape) {
    this.id = shape.getId();
    this.shapeId = shape.getShapeId();
    this.shapePtSequence = shape.getShapePtSequence();
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
    shape.setShapeDistTraveled(this.getShapeDistTraveled());
    return shape;
  }
}
/*% } %*/
