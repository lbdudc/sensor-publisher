/*% if (feature.DM_A_Connectivity) { %*/
package es.udc.lbd.gema.lps.component.connectivity;

import java.util.List;

import javax.inject.Inject;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/*% if (feature.DM_A_C_NetworkTracing) { %*/
import es.udc.lbd.gema.lps.component.connectivity.model.NetworkCoverage;

import es.udc.lbd.gema.lps.component.connectivity.model.OriginDestinyPoint;
/*% } %*/

/*% if (feature.DM_A_C_RC_pgRouting) { %*/
import es.udc.lbd.gema.lps.component.connectivity.model.RouteSegment;
/*% } %*/
import es.udc.lbd.gema.lps.component.connectivity.repository.ConnectivityRepository;

@Service
@Transactional
public class NetworkService {

    @Inject
    private ConnectivityRepository connectivityRepository;
    /*% if (feature.DM_A_C_RC_pgRouting) { %*/

    public List<RouteSegment> getComputeRoute(Double fromLat, Double fromLng, Double toLat, Double toLng) {

        List<RouteSegment> segments = connectivityRepository.obtainListSections(fromLat, fromLng, toLat, toLng);
        return segments;
    }
    /*% } %*/
    /*% if (feature.DM_A_C_NetworkTracing) { %*/

    public NetworkCoverage getNetworkTracing(Double lat, Double lng, Integer minutes) {
        OriginDestinyPoint point = connectivityRepository.obtainOriginDestinyPoint(lat, lng);
        Double distance = minutes / 60.0;

        return connectivityRepository.networkTracing(point.getSource(), distance);
    }
    /*% } %*/
}
/*% } %*/
