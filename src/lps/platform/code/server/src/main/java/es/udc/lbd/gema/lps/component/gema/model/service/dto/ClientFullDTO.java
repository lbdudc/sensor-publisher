/*% if (feature.MWM_M_Client) { %*/
package es.udc.lbd.gema.lps.component.gema.model.service.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import javax.validation.constraints.NotEmpty;
import es.udc.lbd.gema.lps.component.gema.model.domain.Client;
import es.udc.lbd.gema.lps.model.util.jackson.CustomGeometryDeserializer;
import es.udc.lbd.gema.lps.model.util.jackson.CustomGeometrySerializer;
import org.locationtech.jts.geom.Point;

public class ClientFullDTO {

  private Long id;
  private String label;
  @NotEmpty
  private String fullName;
  private String email;
  private String phone;
  private String address;
  /*% if (feature.MWM_TE_T_ClientCustomLink) { %*/
  private String detailUrl;
  /*% } %*/

  @JsonSerialize(using = CustomGeometrySerializer.class)
  @JsonDeserialize(using = CustomGeometryDeserializer.class)
  private Point location;

  private Boolean active;

  public ClientFullDTO() {}

  public ClientFullDTO(Client client/*% if (feature.MWM_TE_T_ClientCustomLink) { %*/, String detailUrl/*% } %*/) {
    this.id = client.getId();
    this.label = client.getLabel();
    this.location = client.getLocation();
    this.fullName = client.getFullName();
    this.email = client.getEmail();
    this.phone = client.getPhone();
    this.address = client.getAddress();
    /*% if (feature.MWM_TE_T_ClientCustomLink) { %*/
    this.detailUrl = detailUrl.replace("{id}", this.id.toString());
    /*% } %*/
    this.active = client.getActive();
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getLabel() {
    return label;
  }

  public void setLabel(String label) {
    this.label = label;
  }

  public Point getLocation() {
    return location;
  }

  public void setLocation(Point location) {
    this.location = location;
  }

  public String getFullName() {
    return fullName;
  }

  public void setFullName(String fullName) {
    this.fullName = fullName;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getPhone() {
    return phone;
  }

  public void setPhone(String phone) {
    this.phone = phone;
  }

  public String getAddress() {
    return address;
  }

  public void setAddress(String address) {
    this.address = address;
  }

  /*% if (feature.MWM_TE_T_ClientCustomLink) { %*/
  public String getDetailUrl() {
    return detailUrl;
  }

  public void setDetailUrl(String detailUrl) {
    this.detailUrl = detailUrl;
  }
  /*% } %*/
  public Boolean getActive() {
    return active;
  }
  
  public void setActive(Boolean active) {
    this.active = active;
  }
}
/*% } %*/
