/*% if (feature.MWM_M_Client) { %*/
package es.udc.lbd.gema.lps.component.gema.model.service.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import javax.validation.constraints.NotEmpty;
import es.udc.lbd.gema.lps.component.gema.model.domain.Client;
import es.udc.lbd.gema.lps.model.util.jackson.CustomGeometryDeserializer;
import es.udc.lbd.gema.lps.model.util.jackson.CustomGeometrySerializer;
import org.locationtech.jts.geom.Point;

public class ClientDTO {

  private Long id;

  @NotEmpty
  private String fullName;
  private String phone;
  /*% if (feature.MWM_TE_T_ClientCustomLink) { %*/
  private String detailUrl;
  /*% } %*/

  @JsonSerialize(using = CustomGeometrySerializer.class)
  @JsonDeserialize(using = CustomGeometryDeserializer.class)
  private Point location;

  private Boolean active;

  public ClientDTO() {}

  public ClientDTO(Client client) {
    this.id = client.getId();
    this.fullName = client.getFullName();
    this.phone = client.getPhone();
    this.location = client.getLocation();
    this.active = client.getActive();
  }

  /*% if (feature.MWM_TE_T_ClientCustomLink) { %*/
  public ClientDTO(Client client, String detailUrl) {
    this(client);
    if (detailUrl != null) {
      this.detailUrl = detailUrl.replace("{id}", this.id.toString());
    }
  }
  /*% } %*/

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getFullName() {
    return fullName;
  }

  public void setFullName(String fullName) {
    this.fullName = fullName;
  }

  public String getPhone() {
    return phone;
  }

  public void setPhone(String phone) {
    this.phone = phone;
  }

  public Point getLocation() {
    return location;
  }

  public void setLocation(Point location) {
    this.location = location;
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
