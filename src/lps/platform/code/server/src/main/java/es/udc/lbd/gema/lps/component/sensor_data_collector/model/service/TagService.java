/*% if (feature.MWM_TA_SensorDataCollector) { %*/
package es.udc.lbd.gema.lps.component.sensor_data_collector.model.service;

import es.udc.lbd.gema.lps.component.sensor_data_collector.model.domain.Tag;
import es.udc.lbd.gema.lps.component.sensor_data_collector.model.repository.TagRepository;
import es.udc.lbd.gema.lps.component.sensor_data_collector.model.service.dto.TagDTO;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;
import javax.inject.Inject;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class TagService {

  @Inject private TagRepository tagRepository;

  public TagDTO createTag(TagDTO tagDto) {
    Tag tag = new Tag();

    tag.setName(tagDto.getName());

    tag = tagRepository.save(tag);
    return new TagDTO(tag);
  }

  public TagDTO updateTag(TagDTO tagDto) throws NoSuchElementException {
    Tag bdTag = tagRepository.findById(tagDto.getId()).get(); // Check tag exists

    bdTag.setName(tagDto.getName());

    bdTag = tagRepository.save(bdTag);

    return new TagDTO(bdTag);
  }

  public TagDTO findById(Long id) throws NoSuchElementException {
    Tag bdTag = tagRepository.findById(id).get();
    TagDTO bdTagDto = new TagDTO(bdTag);
    return bdTagDto;
  }

  public List<TagDTO> findAll() {
    List<Tag> tags = tagRepository.findAll();
    List<TagDTO> tagsDTO = tags.stream().map(tag -> new TagDTO(tag)).collect(Collectors.toList());
    return tagsDTO;
  }

  public void deleteTag(Long id) throws NoSuchElementException {
    TagDTO tagDto = findById(id);
    Tag tag = new Tag();

    tag.setId(id);
    tag.setName(tagDto.getName());

    tagRepository.delete(tag);
  }
}

/*% } %*/
