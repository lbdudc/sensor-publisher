/*% if (feature.DM_DS_GTFS) { %*/
package es.udc.lbd.gema.lps.component.gtfs.model.service;

import es.udc.lbd.gema.lps.component.gtfs.model.domain.Route;
import es.udc.lbd.gema.lps.component.gtfs.model.domain.Trip;
import es.udc.lbd.gema.lps.component.gtfs.model.repository.TripRepository;
import es.udc.lbd.gema.lps.component.gtfs.model.service.dto.TripDTO;
import es.udc.lbd.gema.lps.component.gtfs.model.service.dto.TripFullDTO;
import es.udc.lbd.gema.lps.component.gtfs.specifications.TripSpecification;
import es.udc.lbd.gema.lps.model.service.exceptions.NotFoundException;
import es.udc.lbd.gema.lps.model.service.exceptions.OperationNotAllowedException;
import es.udc.lbd.gema.lps.web.rest.custom.FeatureCollectionJSON;
import es.udc.lbd.gema.lps.web.rest.custom.FeatureJSON;
import es.udc.lbd.gema.lps.web.rest.util.specification_utils.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.locationtech.jts.geom.Geometry;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true, rollbackFor = Exception.class)
public class TripServiceImpl implements TripService {

  @Inject private TripRepository tripRepository;

  public Page<TripDTO> getAll(Pageable pageable, List<String> filters, String search) {
    Page<Trip> page;
    if (search != null && !search.isEmpty()) {
      page = tripRepository.findAll(TripSpecification.searchAll(search), pageable);
    } else {
      page =
          tripRepository.findAll(
              SpecificationUtil.getSpecificationFromFilters(filters, false), pageable);
    }
    return page.map(TripDTO::new);
  }

  public FeatureCollectionJSON getShape(Boolean properties, List<String> filters) {
	    List<Trip> list =
	    		tripRepository.findAll(SpecificationUtil.getSpecificationFromFilters(filters, false));

	    Map<Geometry, Trip> geometryTrips =
	    		tripRepository.findTripsShapes(
	            list.stream().map(t -> t.getTripId()).collect(Collectors.toList()));
	    List<FeatureJSON> ret = new ArrayList<FeatureJSON>();
	    for (Map.Entry<Geometry, Trip> entry : geometryTrips.entrySet()) {
	      FeatureJSON geoJSON = new FeatureJSON();
	      Trip trip = entry.getValue();
	      if (properties) {
	        geoJSON = new FeatureJSON(Route.class, trip);
	      } else {
	        geoJSON.setProperties(new HashMap());
	      }
	      geoJSON.getProperties().put("displayString", "" + trip.getTripId() + "");
	      geoJSON.setGeometry(entry.getKey());
	      ret.add(geoJSON);
	    }
	    return new FeatureCollectionJSON(ret);
	  }

  public TripFullDTO get(String id) throws NotFoundException {
    Trip trip = findById(id);
    return new TripFullDTO(trip);
  }

  @Transactional(readOnly = false, rollbackFor = Exception.class)
  public TripFullDTO create(TripFullDTO tripDto) throws OperationNotAllowedException {
    if (tripDto.getId() != null) {
      throw new OperationNotAllowedException("trip.error.id-exists");
    }
    Trip tripEntity = tripDto.toTrip();
    Trip tripSaved = tripRepository.save(tripEntity);
    return new TripFullDTO(tripSaved);
  }

  @Transactional(readOnly = false, rollbackFor = Exception.class)
  public TripFullDTO update(String id, TripFullDTO tripDto) throws OperationNotAllowedException {
    if (tripDto.getId() == null) {
      throw new OperationNotAllowedException("trip.error.id-not-exists");
    }
    if (!id.equals(tripDto.getId())) {
      throw new OperationNotAllowedException("trip.error.id-dont-match");
    }
    Trip trip =
        tripRepository
            .findById(id)
            .orElseThrow(() -> new OperationNotAllowedException("trip.error.id-not-exists"));
    Trip tripToUpdate = tripDto.toTrip();
    Trip tripUpdated = tripRepository.save(tripToUpdate);
    return new TripFullDTO(tripUpdated);
  }

  @Transactional(readOnly = false, rollbackFor = Exception.class)
  public void delete(String id) {
    tripRepository.deleteById(id);
  }

  /** PRIVATE METHODS * */
  private Trip findById(String id) throws NotFoundException {
    return tripRepository
        .findById(id)
        .orElseThrow(() -> new NotFoundException("Cannot find Trip with id " + id));
  }
}
/*% } %*/
