/*% if (feature.DM_A_Connectivity) { %*/
package es.udc.lbd.gema.lps.component.connectivity.repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import javax.inject.Inject;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.PrecisionModel;
import org.locationtech.jts.io.ParseException;
import org.locationtech.jts.io.WKTReader;

import es.udc.lbd.gema.lps.config.Properties;

/*% if (feature.DM_A_C_NetworkTracing) { %*/
import es.udc.lbd.gema.lps.component.connectivity.model.NetworkCoverage;
import es.udc.lbd.gema.lps.component.connectivity.model.OriginDestinyPoint;
/*% } %*/

/*% if (feature.DM_A_C_RC_pgRouting) { %*/

import java.sql.PreparedStatement;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import es.udc.lbd.gema.lps.component.connectivity.model.RouteSegment;
/*% } %*/

@Repository
public class ConnectivityRepository {

    @Inject
    private JdbcTemplate jdbcTemplate;

    @Inject
    private Properties properties;


    /*% if (feature.DM_A_C_RC_pgRouting) { %*/

    public List<RouteSegment> obtainListSections(Double fromLat, Double fromLng, Double toLat, Double toLng) {
    	Integer srid = properties.getGis().getDefaultSrid();
        GeometryFactory factory = new GeometryFactory(new PrecisionModel(), srid);
        WKTReader geometryReader = new WKTReader();
        String originPoint = "ST_SetSRID(ST_Point("+ fromLng + ", " + fromLat + "), " + srid + ")";
        String destinyPoint = "ST_SetSRID(ST_Point("+ toLng + ", " + toLat + "), " + srid + ")";

        String insertValues = "(SELECT -1000, source, -1," + 
        		" n.cost*ST_LineLocatePoint(geom_way, " + originPoint + ") as cost," + 
        		" n.reverse_cost*ST_LineLocatePoint(geom_way, " + originPoint + ") as reverse_cost," + 
        		" ST_asText(ST_LineSubstring(geom_way, 0, ST_LineLocatePoint(geom_way, " + originPoint + "))) as geom_way " + 
        		" FROM network.link n" + 
        		" WHERE st_distance(" + originPoint + ", n.geom_way) < 0.01 " + 
        		" AND ST_GeometryType(ST_LineSubstring(geom_way, 0, ST_LineLocatePoint(geom_way, " + originPoint + "))) <> 'ST_Point' " + 
        		" ORDER BY st_distance(" + originPoint + ", n.geom_way) LIMIT 1)" + 
        		" UNION" + 
        		" (SELECT -1001, -1, target, n.cost, n.reverse_cost," + 
        		" ST_asText(ST_LineSubstring(geom_way, ST_LineLocatePoint(geom_way, " + originPoint + "), 1)) as geom_way " + 
        		" FROM network.link n" + 
        		" WHERE st_distance(" + originPoint + ", n.geom_way) < 0.01" + 
        		" AND ST_GeometryType(ST_LineSubstring(geom_way, ST_LineLocatePoint(geom_way, " + originPoint + "), 1)) <> 'ST_Point' " + 
        		" ORDER BY st_distance(" + originPoint + ", n.geom_way) LIMIT 1)" + 
        		" UNION" + 
        		" (SELECT -2000, source, -2," + 
        		" n.cost*ST_LineLocatePoint(geom_way, " + destinyPoint + ") as cost," + 
        		" n.reverse_cost*ST_LineLocatePoint(geom_way, " + destinyPoint + ") as reverse_cost," + 
        		" ST_asText(ST_LineSubstring(geom_way, 0, ST_LineLocatePoint(geom_way, " + destinyPoint + "))) as geom_way " + 
        		" FROM network.link n" + 
        		" WHERE st_distance(" + destinyPoint + ", n.geom_way) < 0.01" + 
        		" AND ST_GeometryType(ST_LineSubstring(geom_way, 0, ST_LineLocatePoint(geom_way, " + destinyPoint + "))) <> 'ST_Point' " + 
        		" ORDER BY st_distance(" + destinyPoint + ", n.geom_way) LIMIT 1)" + 
        		" UNION" + 
        		" (SELECT -2001, target, -2, n.cost, n.reverse_cost," + 
        		" ST_asText(ST_LineSubstring(geom_way, ST_LineLocatePoint(geom_way, " + destinyPoint + "), 1)) as geom_way " + 
        		" FROM network.link n" + 
        		" WHERE st_distance(" + destinyPoint + ", n.geom_way) < 0.01" + 
        		" AND ST_GeometryType(ST_LineSubstring(geom_way, ST_LineLocatePoint(geom_way, " + destinyPoint + "), 1)) <> 'ST_Point' " +
        		" ORDER BY st_distance(" + destinyPoint + ", n.geom_way) LIMIT 1);";
                
        List<RouteSegment> insertedRows = jdbcTemplate.query(insertValues, new RowMapper<RouteSegment>() {
            @Override
            public RouteSegment mapRow(ResultSet rs, int rowNum) throws SQLException {
                RouteSegment c = new RouteSegment();
                Geometry geom = null;
                
                c.setLinkId(rs.getLong(1));
                c.setCost(rs.getDouble(4));
                c.setReverse_cost(rs.getDouble(5));
                c.setSource(rs.getLong(2));
                c.setTarget(rs.getLong(3));
                
                try {
                    geom = geometryReader.read(rs.getCharacterStream(6));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                
                Coordinate[] coord = geom.getCoordinates();
                
                c.setGeom_way(factory.createLineString(coord));
                return c;
            }
        });

        String insertString = "INSERT INTO network.link (id, source, target, cost, reverse_cost, geom_way, x1, y1, x2, y2) VALUES (?, ?, ?, ?, ?, ST_GeometryFromText(?, " + srid + "), ?, ?, ?, ?)";

        jdbcTemplate.batchUpdate(insertString, new BatchPreparedStatementSetter() {
        	@Override
        	public void setValues(PreparedStatement ps, int i) throws SQLException {
                RouteSegment rs = insertedRows.get(i);
                ps.setLong(1, rs.getLinkId());
                ps.setLong(2, rs.getSource());
                ps.setLong(3, rs.getTarget());
                ps.setDouble(4, rs.getCost());
                ps.setDouble(5, rs.getReverse_cost());
                ps.setString(6, rs.getGeom_way().toString());
                ps.setDouble(7, rs.getGeom_way().getStartPoint().getX());
                ps.setDouble(8, rs.getGeom_way().getStartPoint().getY());
                ps.setDouble(9, rs.getGeom_way().getEndPoint().getX());
                ps.setDouble(10, rs.getGeom_way().getEndPoint().getY());
        	}

			@Override
			public int getBatchSize() {
				return insertedRows.size();
			}
        }); 

        String sql =
                "	(SELECT osm_id as linkId, kmh as maxSpeed, osm_name as linkName, case when clazz = 1 then 'highway' when clazz = 2 then 'highway_link' when clazz = 3 then 'trunk' when clazz = 4 then 'trunk_link' when clazz = 5 then 'primary' when clazz = 6 then 'primary_link' when clazz = 7 then 'secondary' when clazz = 8 then 'secondary_link' when clazz = 9 then 'tertiary' when clazz = 10 then 'tertiary_link' when clazz = 11 then 'residential' when clazz = 12 then 'road' when clazz = 13 then 'unclassified' when clazz = 14 then 'service' when clazz = 15 then 'living_street' when clazz = 16 then 'pedestrian' when clazz = 17 then 'track' when clazz = 18 then 'path' when clazz = 19 then 'cicleway' when clazz = 20 then 'footway' when clazz = 21 then 'steps' end as linkType, km as length, link.cost as cost, link.reverse_cost as reverse_cost, source, target, st_asText(geom_way) as geom_way " +
                "	FROM network.link, " + 
                "	(SELECT seq, node, edge, cost " + 
                "	FROM pgr_astar('SELECT id, source, target, cost, reverse_cost, ST_X(ST_StartPoint(geom_way)) as x1, ST_Y(ST_StartPoint(geom_way)) as y1,  ST_X(ST_EndPoint(geom_way)) as x2, ST_Y(ST_EndPoint(geom_way)) as y2 FROM network.link WHERE geom_way && (SELECT st_expand(st_envelope(st_union(geom_way)),1) FROM network.link WHERE source IN (-1, -2))', -1, -2)) " + 
                "	as path where link.id = path.edge order by path.seq);";
        
        List<RouteSegment> ret = jdbcTemplate.query(sql, new RowMapper<RouteSegment>() {

            @Override
            public RouteSegment mapRow(ResultSet rs, int rowNum) throws SQLException {
                RouteSegment c = new RouteSegment();
                Geometry geom = null;

                c.setLinkId(rs.getLong(1));
                c.setMaxSpeed(rs.getInt(2));
                c.setLinkName(rs.getString(3));
                c.setLinkType(rs.getString(4));
                c.setLength(rs.getDouble(5));
                c.setCost(rs.getDouble(6));
                c.setReverse_cost(rs.getDouble(7));
                c.setSource(rs.getLong(8));
                c.setTarget(rs.getLong(9));
                try {
                    geom = geometryReader.read(rs.getCharacterStream(10));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                Coordinate[] coord = geom.getCoordinates();
                c.setGeom_way(factory.createLineString(coord));
                return c;
            }
        });

        String deleteTempEdges = "DELETE FROM network.link WHERE id in (?, ?, ?, ?)";
        
        jdbcTemplate.update(deleteTempEdges, new Object[] {-1000, -1001, -2000, -2001});

        return ret;
    }
    /*% } %*/
    /*% if (feature.DM_A_C_NetworkTracing) { %*/

    public OriginDestinyPoint obtainOriginDestinyPoint(Double lat, Double lng) {
        String sql = "select source, x1, y1 from network.link where geom_way && st_expand(st_geometryfromtext('POINT("
                + lng + " " + lat + ")', " + properties.getGis().getDefaultSrid() + "), 0.1) order by st_distance(geom_way, st_geometryfromtext('POINT(" + lng
                + " " + lat + ")', " + properties.getGis().getDefaultSrid() + ")) limit 1;";
        List<OriginDestinyPoint> ret = jdbcTemplate.query(sql, new RowMapper<OriginDestinyPoint>() {

            @Override
            public OriginDestinyPoint mapRow(ResultSet rs, int rowNum) throws SQLException {
                OriginDestinyPoint c = new OriginDestinyPoint();
                c.setSource(rs.getInt(1));
                c.setX1(rs.getDouble(2));
                c.setY1(rs.getDouble(3));
                return c;
            }
        });
        return ret.get(0);
    }

    public NetworkCoverage networkTracing(Integer origin, Double distance) {
        GeometryFactory factory = new GeometryFactory(new PrecisionModel(), properties.getGis().getDefaultSrid());
        WKTReader geometryReader = new WKTReader();
        Double concavity = 1.0;
        if (distance <= 1) {
            concavity = 0.99;
        }
        if (distance <= 0.2) {
            concavity = 0.98;
        }

        String sql = "SELECT st_astext(st_concaveHull(st_collect(geom_way), " + concavity + ")) FROM network.link, (SELECT seq, node, edge, cost, agg_cost FROM pgr_drivingDistance( 'SELECT id, source, target, cost, reverse_cost FROM network.link WHERE geom_way && (SELECT st_expand(st_envelope(st_union(geom_way)), " + distance + ") FROM network.link WHERE source IN (" + origin + "))', " + origin + ", " + distance + ", true)) as path where link.id = path.edge";
        List<NetworkCoverage> ret = jdbcTemplate.query(sql, new RowMapper<NetworkCoverage>() {

            @Override
            public NetworkCoverage mapRow(ResultSet rs, int rowNum) throws SQLException {
                Geometry geom = null;
                NetworkCoverage nc = new NetworkCoverage();
                try {
                    geom = geometryReader.read(rs.getCharacterStream(1));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                Coordinate[] coord = geom.getCoordinates();
                nc.setGeom(factory.createPolygon(coord));
                return nc;
            }

        });

        return ret.get(0);
    }
    /*% } %*/
}
/*% } %*/
