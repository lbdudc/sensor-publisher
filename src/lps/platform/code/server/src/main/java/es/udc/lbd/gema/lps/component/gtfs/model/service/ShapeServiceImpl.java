/*% if (feature.DM_DS_GTFS) { %*/
package es.udc.lbd.gema.lps.component.gtfs.model.service;

import es.udc.lbd.gema.lps.component.gtfs.model.domain.Shape;
import es.udc.lbd.gema.lps.component.gtfs.model.repository.ShapeRepository;
import es.udc.lbd.gema.lps.component.gtfs.model.service.dto.ShapeDTO;
import es.udc.lbd.gema.lps.component.gtfs.model.service.dto.ShapeFullDTO;
import es.udc.lbd.gema.lps.component.gtfs.specifications.ShapeSpecification;
import es.udc.lbd.gema.lps.model.service.exceptions.NotFoundException;
import es.udc.lbd.gema.lps.model.service.exceptions.OperationNotAllowedException;
import es.udc.lbd.gema.lps.web.rest.custom.FeatureCollectionJSON;
import es.udc.lbd.gema.lps.web.rest.custom.FeatureJSON;
import es.udc.lbd.gema.lps.web.rest.util.specification_utils.*;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import javax.inject.Inject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true, rollbackFor = Exception.class)
public class ShapeServiceImpl implements ShapeService {

  @Inject private ShapeRepository shapeRepository;

  public Page<ShapeDTO> getAll(Pageable pageable, List<String> filters, String search) {
    Page<Shape> page;
    if (search != null && !search.isEmpty()) {
      page = shapeRepository.findAll(ShapeSpecification.searchAll(search), pageable);
    } else {
      page =
          shapeRepository.findAll(
              SpecificationUtil.getSpecificationFromFilters(filters, false), pageable);
    }
    return page.map(ShapeDTO::new);
  }

  public FeatureCollectionJSON getShapePtLoc(Boolean properties, List<String> filters) {
    List<Shape> list =
        shapeRepository.findAll(SpecificationUtil.getSpecificationFromFilters(filters, false));

    List<FeatureJSON> ret =
        list.stream()
            .map(
                e -> {
                  FeatureJSON geoJSON = new FeatureJSON();
                  if (properties) {
                    geoJSON = new FeatureJSON(Shape.class, e);
                  } else {
                    geoJSON.setProperties(new HashMap());
                  }
                  geoJSON.setId(e.getId());
                  geoJSON.getProperties().put("displayString", "" + e.getId() + "");
                  geoJSON.setGeometry(e.getShapePtLoc());
                  return geoJSON;
                })
            .filter(e -> e.getGeometry() != null)
            .collect(Collectors.toList());
    return new FeatureCollectionJSON(ret);
  }

  public ShapeFullDTO get(Long id) throws NotFoundException {
    Shape shape = findById(id);
    return new ShapeFullDTO(shape);
  }

  @Transactional(readOnly = false, rollbackFor = Exception.class)
  public ShapeFullDTO create(ShapeFullDTO shapeDto) throws OperationNotAllowedException {
    if (shapeDto.getId() != null) {
      throw new OperationNotAllowedException("shape.error.id-exists");
    }
    Shape shapeEntity = shapeDto.toShape();
    Shape shapeSaved = shapeRepository.save(shapeEntity);
    return new ShapeFullDTO(shapeSaved);
  }

  @Transactional(readOnly = false, rollbackFor = Exception.class)
  public ShapeFullDTO update(Long id, ShapeFullDTO shapeDto) throws OperationNotAllowedException {
    if (shapeDto.getId() == null) {
      throw new OperationNotAllowedException("shape.error.id-not-exists");
    }
    if (!id.equals(shapeDto.getId())) {
      throw new OperationNotAllowedException("shape.error.id-dont-match");
    }
    Shape shape =
        shapeRepository
            .findById(id)
            .orElseThrow(() -> new OperationNotAllowedException("shape.error.id-not-exists"));
    Shape shapeToUpdate = shapeDto.toShape();
    Shape shapeUpdated = shapeRepository.save(shapeToUpdate);
    return new ShapeFullDTO(shapeUpdated);
  }

  @Transactional(readOnly = false, rollbackFor = Exception.class)
  public void delete(Long id) {
    shapeRepository.deleteById(id);
  }

  /** PRIVATE METHODS * */
  private Shape findById(Long id) throws NotFoundException {
    return shapeRepository
        .findById(id)
        .orElseThrow(() -> new NotFoundException("Cannot find Shape with id " + id));
  }
}
/*% } %*/
