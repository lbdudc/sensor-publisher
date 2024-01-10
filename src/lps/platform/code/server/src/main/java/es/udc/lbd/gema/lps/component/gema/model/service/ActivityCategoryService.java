/*% if (feature.MWM_M_Activity) { %*/
package es.udc.lbd.gema.lps.component.gema.model.service;
/*% if (feature.MWM_TE_Statistics || feature.MWM_TE_ActivitiesRecord) { %*/
import es.udc.lbd.gema.lps.component.gema.model.domain.ActivityCategory;
import es.udc.lbd.gema.lps.component.gema.model.service.dto.ActivityCategoryDTO;
import java.util.List;
import java.util.stream.Collectors;
/*% } %*/
import javax.inject.Inject;
import es.udc.lbd.gema.lps.component.gema.model.repository.ActivityCategoryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ActivityCategoryService {
  @Inject private ActivityCategoryRepository activityCategoryRepository;

  /*% if (feature.MWM_TE_Statistics || feature.MWM_TE_ActivitiesRecord) { %*/
  public List<ActivityCategoryDTO> findAll() {

    List<ActivityCategory> activityCategories = activityCategoryRepository.findAll();

    List<ActivityCategoryDTO> activityCategoriesDTO =
      activityCategories.stream()
        .map(activityCategory -> new ActivityCategoryDTO(activityCategory))
        .collect(Collectors.toList());
    return activityCategoriesDTO;
  }
  /*% } %*/
}
/*% } %*/
