/*% if (feature.DM_A_Connectivity) { %*/
package es.udc.lbd.gema.lps.component.connectivity;

import java.util.List;

import javax.inject.Inject;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/*% if (feature.DM_A_C_NetworkTracing) { %*/
import es.udc.lbd.gema.lps.component.connectivity.model.NetworkCoverage;
/*% } %*/
/*% if (feature.DM_A_C_RC_pgRouting) { %*/
import es.udc.lbd.gema.lps.component.connectivity.model.RouteSegment;
/*% } %*/

@RestController
@RequestMapping(ConnectivityResource.CONNECTIVITY_RESOURCE_URL)
public class ConnectivityResource {
    public final static String CONNECTIVITY_RESOURCE_URL = "/api/component/connectivity";

    @Inject
    private NetworkService networkService;
    /*% if (feature.DM_A_C_RC_pgRouting) { %*/

    @GetMapping("/routeCalculation")
    public ResponseEntity<?> getRoute(@RequestParam(value = "fromLat", required = true) Double fromLat,
            @RequestParam(value = "fromLng", required = true) Double fromLng,
            @RequestParam(value = "toLat", required = true) Double toLat,
            @RequestParam(value = "toLng", required = true) Double toLng) {

        List<RouteSegment> ret = networkService.getComputeRoute(fromLat, fromLng, toLat, toLng);
        return new ResponseEntity<List<RouteSegment>>(ret, HttpStatus.OK);
    }
    /*% } %*/
    /*% if (feature.DM_A_C_NetworkTracing) { %*/

    @GetMapping("/networkTracing")
    public ResponseEntity<?> getRoute(@RequestParam(value = "fromLat", required = true) Double fromLat,
            @RequestParam(value = "fromLng", required = true) Double fromLng,
            @RequestParam(value = "minutes", required = true) Integer minutes) {

        NetworkCoverage ret = networkService.getNetworkTracing(fromLat, fromLng, minutes);
        return new ResponseEntity<NetworkCoverage>(ret, HttpStatus.OK);
    }
    /*% } %*/
}
/*% } %*/
