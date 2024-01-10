/*% if (feature.MWM_TrajectoryAnnotation) { %*/
package es.udc.lbd.gema.lps.component.sensor_data_collector.model.domain;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import es.udc.lbd.gema.lps.component.gema.model.domain.Employee;
import es.udc.lbd.gema.lps.model.util.jackson.CustomGeometryDeserializer;
import es.udc.lbd.gema.lps.model.util.jackson.CustomGeometrySerializer;
import java.time.LocalDateTime;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.SequenceGenerator;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import org.locationtech.jts.geom.Point;

@Entity
@Table(name = "ta_location")
public class Location {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "locationGen")
  @SequenceGenerator(name = "locationGen", sequenceName = "ta_location_id_seq", allocationSize = 1)
  private Long id;

  private String provider;
  private LocalDateTime time;

  @JsonSerialize(using = CustomGeometrySerializer.class)
  @JsonDeserialize(using = CustomGeometryDeserializer.class)
  private Point position;

  private LocalDateTime creationDate;

  private Double altitude;
  private Double speed;
  private Double bearing;
  private Double accuracy;

  @ManyToOne(optional = false)
  private Employee employee;

  public Location() {}

  public Location(
      Long id,
      String provider,
      LocalDateTime time,
      Point position,
      LocalDateTime creationDate,
      Double altitude,
      Double speed,
      Double bearing,
      Double accuracy,
      Employee employee) {
    this.id = id;
    this.provider = provider;
    this.time = time;
    this.position = position;
    this.creationDate = creationDate;
    this.altitude = altitude;
    this.speed = speed;
    this.bearing = bearing;
    this.accuracy = accuracy;
    this.employee = employee;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getProvider() {
    return provider;
  }

  public void setProvider(String provider) {
    this.provider = provider;
  }

  public LocalDateTime getTime() {
    return time;
  }

  public void setTime(LocalDateTime time) {
    this.time = time;
  }

  public Point getPosition() {
    return position;
  }

  public void setPosition(Point position) {
    this.position = position;
  }

  public Double getAltitude() {
    return altitude;
  }

  public void setAltitude(Double altitude) {
    this.altitude = altitude;
  }

  public Double getSpeed() {
    return speed;
  }

  public void setSpeed(Double speed) {
    this.speed = speed;
  }

  public Double getBearing() {
    return bearing;
  }

  public void setBearing(Double bearing) {
    this.bearing = bearing;
  }

  public Double getAccuracy() {
    return accuracy;
  }

  public void setAccuracy(Double accuracy) {
    this.accuracy = accuracy;
  }

  public Employee getEmployee() {
    return employee;
  }

  public void setEmployee(Employee employee) {
    this.employee = employee;
  }

  public LocalDateTime getCreationDate() {
    return creationDate;
  }

  public void setCreationDate(LocalDateTime creationDate) {
    this.creationDate = creationDate;
  }

  @Override
  public String toString() {
    return "Location [id="
        + id
        + ", provider="
        + provider
        + ", time="
        + time
        + ", position="
        + position
        + ", creationDate="
        + creationDate
        + ", altitude="
        + altitude
        + ", speed="
        + speed
        + ", bearing="
        + bearing
        + ", accuracy="
        + accuracy
        + ", employee="
        + employee
        + "]";
  }
}

/*% } %*/
