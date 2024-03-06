/*% if (feature.DM_A_C_NetworkTracing) { %*/
package es.udc.lbd.gema.lps.component.connectivity.model;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.locationtech.jts.geom.Geometry;

import es.udc.lbd.gema.lps.model.util.jackson.CustomGeometrySerializer;

public class NetworkCoverage {

    public NetworkCoverage() {
    }

    @JsonSerialize(using = CustomGeometrySerializer.class)
    private Geometry geom;

    public Geometry getGeom() {
        return geom;
    }

    public void setGeom(Geometry geom) {
        this.geom = geom;
    }
}
/*% } %*/
