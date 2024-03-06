/*% if (feature.MWM_TA_SensorDataCollector) { %*/
package es.udc.lbd.gema.lps.component.sensor_data_collector.model.service.dto;

import java.time.LocalDateTime;

import org.locationtech.jts.geom.Point;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import es.udc.lbd.gema.lps.component.sensor_data_collector.model.domain.Location;
import es.udc.lbd.gema.lps.component.gema.model.service.dto.EmployeeDTO;
import es.udc.lbd.gema.lps.model.util.jackson.CustomGeometryDeserializer;
import es.udc.lbd.gema.lps.model.util.jackson.CustomGeometrySerializer;

public class LocationDTO {

	private Long id;
	private String provider;
	private LocalDateTime time;
	@JsonSerialize(using = CustomGeometrySerializer.class)
	@JsonDeserialize(using = CustomGeometryDeserializer.class)
	private Point position;
	private Double altitude;
	private Double speed;
	private Double bearing;
	private Double accuracy;
	private EmployeeDTO employee;

	public LocationDTO() {

	}

	public LocationDTO(Location location) {
		this.id = location.getId();
		this.provider = location.getProvider();
		this.time = location.getTime();
		this.position = location.getPosition();
		this.altitude = location.getAltitude();
		this.speed = location.getSpeed();
		this.bearing = location.getBearing();
		this.accuracy = location.getAccuracy();
		this.employee = new EmployeeDTO(location.getEmployee());
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

	public EmployeeDTO getEmployee() {
		return employee;
	}

	public void setEmployee(EmployeeDTO employee) {
		this.employee = employee;
	}

}

/*% } %*/
