/*% if (feature.DM_A_C_RC_pgRouting) { %*/
package es.udc.lbd.gema.lps.component.connectivity.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.locationtech.jts.geom.LineString;

import es.udc.lbd.gema.lps.model.util.jackson.CustomGeometrySerializer;
import es.udc.lbd.gema.lps.model.util.jackson.CustomGeometryDeserializer;

public class RouteSegment {

    private Long linkId;
    private Integer maxSpeed;
    private String linkName;
    private String linkType;
    private Double length;
    private Double cost;
    private Double reverse_cost;
    private Long source;
    private Long target;

    @JsonSerialize(using = CustomGeometrySerializer.class)
    @JsonDeserialize(using = CustomGeometryDeserializer.class)
    private LineString geom_way;

    public RouteSegment(){}

    public RouteSegment(Long linkId, Integer maxSpeed, String linkName,
            String linkType, Double length, Double cost, Double reverse_cost, Long source, Long target, LineString geom_way) {
        super();
        this.linkId = linkId;
        this.maxSpeed = maxSpeed;
        this.linkName = linkName;
        this.linkType = linkType;
        this.length = length;
        this.cost = cost;
        this.reverse_cost = reverse_cost;
        this.source = source;
        this.target = target;
        this.geom_way = geom_way;
    }

    public Long getLinkId() {
        return linkId;
    }

    public void setLinkId(Long linkId) {
        this.linkId = linkId;
    }

    public Integer getMaxSpeed() {
        return maxSpeed;
    }

    public void setMaxSpeed(Integer maxSpeed) {
        this.maxSpeed = maxSpeed;
    }

    public String getLinkName() {
        return linkName;
    }

    public void setLinkName(String linkName) {
        this.linkName = linkName;
    }

    public String getLinkType() {
        return linkType;
    }

    public void setLinkType(String linkType) {
        this.linkType = linkType;
    }

    public Double getLength() {
        return length;
    }

    public void setLength(Double length) {
        this.length = length;
    }

    public Double getCost() {
        return cost;
    }

    public void setCost(Double cost) {
        this.cost = cost;
    }
    
    public Double getReverse_cost() {
		return reverse_cost;
	}

	public void setReverse_cost(Double reverse_cost) {
		this.reverse_cost = reverse_cost;
	}

	public Long getSource() {
		return source;
	}

	public void setSource(Long source) {
		this.source = source;
	}

	public Long getTarget() {
		return target;
	}

	public void setTarget(Long target) {
		this.target = target;
	}

	public LineString getGeom_way() {
        return geom_way;
    }

    public void setGeom_way(LineString geom_way) {
        this.geom_way = geom_way;
    }
}
/*% } %*/
