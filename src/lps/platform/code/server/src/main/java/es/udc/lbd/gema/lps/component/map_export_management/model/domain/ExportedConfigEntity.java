/*% if (feature.MV_T_E_F_URL) { %*/
package es.udc.lbd.gema.lps.component.map_export_management.model.domain;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.persistence.GeneratedValue;
import javax.persistence.SequenceGenerator;
import javax.persistence.GenerationType;
import org.hibernate.annotations.Type;
import org.hibernate.type.TextType;

@Entity
@Table(name = "mem_exported_config")
public class ExportedConfigEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "exportedConfigGen")
    @SequenceGenerator(name = "exportedConfigGen", sequenceName = "mem_exported_config_id_seq", allocationSize = 1)
    private Long id;

    @NotNull
    private String token;

    @NotNull
    @Type(type="org.hibernate.type.TextType")
    private String json;

    public ExportedConfigEntity() {

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getJson() {
        return json;
    }

    public void setJson(String json) {
        this.json = json;
    }
}
/*% } %*/
