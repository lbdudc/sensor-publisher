/*% if (feature.MWM_TA_SensorDataCollector) { %*/
package es.udc.lbd.gema.lps.component.sensor_data_collector;

import es.udc.lbd.gema.lps.component.sensor_data_collector.model.service.TagService;
import es.udc.lbd.gema.lps.component.sensor_data_collector.model.service.dto.TagDTO;
import java.util.List;
import java.util.NoSuchElementException;
import javax.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(TagResource.TAG_RESOURCE_URL)
public class TagResource {

  public static final String TAG_RESOURCE_URL = "/api/sensor-data-collector/tag";

  private static final Logger log = LoggerFactory.getLogger(TagResource.class);

  @Inject private TagService tagService;

  @PostMapping
  public ResponseEntity<TagDTO> createTag(@RequestBody TagDTO tagDto) {
    TagDTO bdTag = tagService.createTag(tagDto);
    return new ResponseEntity<TagDTO>(bdTag, HttpStatus.OK);
  }

  @PutMapping("/{id}")
  public ResponseEntity<TagDTO> updateTag(@RequestBody TagDTO tagDto, @PathVariable Long id) {
    if (tagDto.getId() != id) {
      return new ResponseEntity<TagDTO>(HttpStatus.BAD_REQUEST);
    }

    try {
      TagDTO bdTag = tagService.updateTag(tagDto);
      return new ResponseEntity<TagDTO>(bdTag, HttpStatus.OK);
    } catch (NoSuchElementException e) {
      return new ResponseEntity<TagDTO>(HttpStatus.NOT_FOUND);
    }
  }

  @GetMapping
  public ResponseEntity<List<TagDTO>> findAll() {
    List<TagDTO> tagDtoList = tagService.findAll();
    return new ResponseEntity<List<TagDTO>>(tagDtoList, HttpStatus.OK);
  }

  @GetMapping("/{id}")
  public ResponseEntity<TagDTO> findById(@PathVariable Long id) {
    try {
      TagDTO tagDto = tagService.findById(id);
      return new ResponseEntity<TagDTO>(tagDto, HttpStatus.OK);
    } catch (NoSuchElementException e) {
      return new ResponseEntity<TagDTO>(HttpStatus.NOT_FOUND);
    }
  }

  @DeleteMapping("/{id}")
  public ResponseEntity deleteTag(@PathVariable Long id) {
    try {
      tagService.deleteTag(id);
      return new ResponseEntity(HttpStatus.OK);
    } catch (NoSuchElementException e) {
      return new ResponseEntity(HttpStatus.NOT_FOUND);
    }
  }
}

/*% } %*/
