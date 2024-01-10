/*% if (feature.MWM_TA_TrajectoryAnnotator) { %*/
package es.udc.lbd.gema.lps.component.sensor_data_collector.model.service.util;

import es.udc.lbd.gema.annotator.domain.AnnotatorActivity;
import es.udc.lbd.gema.annotator.domain.AnnotatorActivityCategory;
import es.udc.lbd.gema.annotator.domain.AnnotatorEmployee;
import es.udc.lbd.gema.annotator.domain.AnnotatorLocation;
import es.udc.lbd.gema.annotator.domain.AnnotatorSegment;
import es.udc.lbd.gema.annotator.domain.AnnotatorTag;
import es.udc.lbd.gema.annotator.domain.activitytaxonomy.AnnotatorTaxonomyEdge;
import es.udc.lbd.gema.annotator.domain.activitytaxonomy.AnnotatorTaxonomyNode;
import es.udc.lbd.gema.annotator.domain.activitytaxonomy.processingnodes.AnnotatorDecisionNode;
import es.udc.lbd.gema.annotator.domain.activitytaxonomy.processingnodes.AnnotatorGroupNode;
import es.udc.lbd.gema.annotator.domain.activitytaxonomy.processingnodes.AnnotatorTagNode;
import es.udc.lbd.gema.annotator.domain.contextinformation.AnnotatorSpatialFeature;
import es.udc.lbd.gema.annotator.domain.contextinformation.AnnotatorSpatialFeatureCollection;
import es.udc.lbd.gema.annotator.domain.taxonomypredicates.AnnotatorPredicate;
import es.udc.lbd.gema.lps.component.gema.model.domain.Activity;
import es.udc.lbd.gema.lps.component.gema.model.domain.ActivityCategory;
import es.udc.lbd.gema.lps.component.gema.model.domain.Employee;
import es.udc.lbd.gema.lps.component.sensor_data_collector.model.domain.Location;
import es.udc.lbd.gema.lps.component.sensor_data_collector.model.domain.Tag;
import es.udc.lbd.gema.lps.component.sensor_data_collector.model.domain.activitytaxonomy.TaxonomyEdge;
import es.udc.lbd.gema.lps.component.sensor_data_collector.model.domain.activitytaxonomy.TaxonomyNode;
import es.udc.lbd.gema.lps.component.sensor_data_collector.model.domain.activitytaxonomy.TaxonomySegment;
import es.udc.lbd.gema.lps.component.sensor_data_collector.model.domain.activitytaxonomy.processingnodes.DecisionNode;
import es.udc.lbd.gema.lps.component.sensor_data_collector.model.domain.activitytaxonomy.processingnodes.GroupNode;
import es.udc.lbd.gema.lps.component.sensor_data_collector.model.domain.activitytaxonomy.processingnodes.TagNode;
import es.udc.lbd.gema.lps.component.sensor_data_collector.model.domain.contextinformation.SpatialFeature;
import es.udc.lbd.gema.lps.component.sensor_data_collector.model.domain.contextinformation.SpatialFeatureCollection;
import es.udc.lbd.gema.lps.component.sensor_data_collector.model.domain.taxonomypredicates.Predicate;
import es.udc.lbd.gema.lps.component.sensor_data_collector.model.service.util.exception.ConversionException;
import es.udc.lbd.gema.lps.component.sensor_data_collector.model.service.util.predicate.PredicateHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

/** Util class which converts GEMA LPS entities to annotator entities */
@Service
public class AnnotatorLPSConversor {

  public AnnotatorSegment lpsSegmentToAnnotatorSegment(TaxonomySegment taxonomySegment) {
    AnnotatorSegment annotatorSegment = new AnnotatorSegment();

    annotatorSegment.setId(taxonomySegment.getId());
    if (taxonomySegment.getEmployee() != null) {
      annotatorSegment.setEmployee(lpsEmployeeToAnnotatorEmployee(taxonomySegment.getEmployee()));
    }
    annotatorSegment.setEndTime(taxonomySegment.getEndTime());
    annotatorSegment.setLocations(
        taxonomySegment.getLocations().stream()
            .map(l -> lpsLocationToAnnotatorLocation(l))
            .collect(Collectors.toList()));
    annotatorSegment.setStartTime(taxonomySegment.getStartTime());
    if (taxonomySegment.getTag() != null) {
      annotatorSegment.setTag(lpsTagToAnnotatorTag(taxonomySegment.getTag()));
    }

    return annotatorSegment;
  }

  public AnnotatorEmployee lpsEmployeeToAnnotatorEmployee(Employee employee) {
    AnnotatorEmployee annotatorEmployee = new AnnotatorEmployee();

    annotatorEmployee.setId(employee.getId());
    annotatorEmployee.setActive(employee.getActive());
    annotatorEmployee.setEmail(employee.getEmail());
    annotatorEmployee.setFullName(employee.getFullName());
    annotatorEmployee.setLabel(employee.getLabel());
    annotatorEmployee.setLastProcessed(employee.getLastProcessed());
    annotatorEmployee.setLocation(employee.getLocation());
    annotatorEmployee.setPhone(employee.getPhone());

    return annotatorEmployee;
  }

  public AnnotatorTaxonomyNode lpsTaxonomyNodeToAnnotatorTaxonomyNode(TaxonomyNode taxonomyNode)
      throws ConversionException {
    AnnotatorTaxonomyNode annotatorTaxonomyNode = null;
    if (taxonomyNode instanceof DecisionNode) {
      annotatorTaxonomyNode = new AnnotatorDecisionNode();
    } else if (taxonomyNode instanceof GroupNode) {
      annotatorTaxonomyNode = new AnnotatorGroupNode(((GroupNode) taxonomyNode).getDuration());
    } else if (taxonomyNode instanceof TagNode) {
      annotatorTaxonomyNode = new AnnotatorTagNode();
      ((AnnotatorTagNode) annotatorTaxonomyNode).setTag(((TagNode) taxonomyNode).getTag());
    }

    if (annotatorTaxonomyNode != null) {
      annotatorTaxonomyNode.setChildren(
          taxonomyNode.getChildren().stream()
              .map(
                  e -> {
                    try {
                      return lpsTaxonomyEdgeToAnnotatorTaxonomyEdge(e);
                    } catch (ConversionException e1) {
                      return null;
                    }
                  })
              .collect(Collectors.toList()));

      if (annotatorTaxonomyNode.getChildren() == null) {
        throw new ConversionException();
      }
    }

    return annotatorTaxonomyNode;
  }

  public AnnotatorTaxonomyEdge lpsTaxonomyEdgeToAnnotatorTaxonomyEdge(TaxonomyEdge taxonomyEdge)
      throws ConversionException {
    AnnotatorTaxonomyEdge annotatorTaxonomyEdge = new AnnotatorTaxonomyEdge();

    annotatorTaxonomyEdge.setChild(lpsTaxonomyNodeToAnnotatorTaxonomyNode(taxonomyEdge.getChild()));
    annotatorTaxonomyEdge.setPredicate(
        lpsPredicateToAnnotatorPredicate(taxonomyEdge.getPredicate()));

    return annotatorTaxonomyEdge;
  }

  public AnnotatorPredicate lpsPredicateToAnnotatorPredicate(Predicate predicate)
      throws ConversionException {
    AnnotatorPredicate annotatorPredicate = null;
    try {
      String classSimpleName = predicate.getClass().getSimpleName() + "Handler";
      String handlerPackageName = PredicateHandler.class.getPackage().getName();
      Class<?> handlerClass = Class.forName(handlerPackageName + "." + classSimpleName);
      Object handlerObject = handlerClass.getConstructor().newInstance();
      Method method = handlerClass.getMethod("handlePredicate", Predicate.class);

      annotatorPredicate = (AnnotatorPredicate) method.invoke(handlerObject, predicate);
    } catch (ClassNotFoundException
        | InstantiationException
        | NoSuchMethodException
        | SecurityException
        | IllegalAccessException
        | IllegalArgumentException
        | InvocationTargetException e) {
      throw new ConversionException();
    }

    if (annotatorPredicate != null) {
    }

    return annotatorPredicate;
  }

  public AnnotatorActivityCategory lpsActivityCategoryToAnnotatorActivityCategory(
      ActivityCategory activityCategory) {
    AnnotatorActivityCategory annotatorActivityCategory = new AnnotatorActivityCategory();

    annotatorActivityCategory.setId(activityCategory.getId());
    annotatorActivityCategory.setLabel(activityCategory.getLabel());
    annotatorActivityCategory.setColor(activityCategory.getColor());

    return annotatorActivityCategory;
  }

  public AnnotatorLocation lpsLocationToAnnotatorLocation(Location location) {
    return new AnnotatorLocation(
        location.getId(),
        location.getProvider(),
        location.getTime(),
        location.getPosition(),
        location.getCreationDate(),
        location.getAltitude(),
        location.getSpeed(),
        location.getBearing(),
        location.getAccuracy(),
        lpsEmployeeToAnnotatorEmployee(location.getEmployee()));
  }

  public AnnotatorTag lpsTagToAnnotatorTag(Tag tag) {
    AnnotatorTag annotatorTag = new AnnotatorTag();

    annotatorTag.setId(tag.getId());
    annotatorTag.setName(tag.getName());

    return annotatorTag;
  }

  public AnnotatorSpatialFeatureCollection
      lpsSpatialFeatureCollectionToAnnotatorSpatialFeatureCollection(
          SpatialFeatureCollection spatialFeatureCollection) {
    AnnotatorSpatialFeatureCollection annotatorSpatialFeatureCollection =
        new AnnotatorSpatialFeatureCollection();

    annotatorSpatialFeatureCollection.setName(spatialFeatureCollection.getName());
    annotatorSpatialFeatureCollection.setFeatures(
        spatialFeatureCollection.getFeatures().stream()
            .map(f -> lpsSpatialFeatureToAnnotatorSpatialFeature(f))
            .collect(Collectors.toSet()));

    // Setting parent collection
    annotatorSpatialFeatureCollection
        .getFeatures()
        .forEach(f -> f.setCollection(annotatorSpatialFeatureCollection));

    return annotatorSpatialFeatureCollection;
  }

  public AnnotatorSpatialFeature lpsSpatialFeatureToAnnotatorSpatialFeature(
      SpatialFeature spatialFeature) {
    AnnotatorSpatialFeature annotatorSpatialFeature = new AnnotatorSpatialFeature();

    annotatorSpatialFeature.setDescription(spatialFeature.getDescription());
    annotatorSpatialFeature.setType(spatialFeature.getType());
    annotatorSpatialFeature.setPosition(spatialFeature.getPosition());

    return annotatorSpatialFeature;
  }

  public Activity annotatorActivityToLpsActivity(AnnotatorActivity annotatorActivity) {
    Activity activity = new Activity();

    activity.setId(annotatorActivity.getId());
    activity.setEmployee(annotatorEmployeeToLpsEmployee(annotatorActivity.getEmployee()));
    activity.setCategory(
        annotatorActivityCategoryToLpsActivityCategory(annotatorActivity.getCategory()));
    activity.setGeom(annotatorActivity.getGeom());
    activity.setTs_init(annotatorActivity.getTs_init());
    activity.setTs_end(annotatorActivity.getTs_end());

    return activity;
  }

  public ActivityCategory annotatorActivityCategoryToLpsActivityCategory(
      AnnotatorActivityCategory annotatorActivityCategory) {
    ActivityCategory activityCategory = new ActivityCategory();

    activityCategory.setId(annotatorActivityCategory.getId());
    activityCategory.setColor(annotatorActivityCategory.getColor());
    activityCategory.setLabel(annotatorActivityCategory.getLabel());

    return activityCategory;
  }

  public Employee annotatorEmployeeToLpsEmployee(AnnotatorEmployee annotatorEmployee) {
    Employee employee = new Employee();

    employee.setActive(annotatorEmployee.getActive());
    employee.setEmail(annotatorEmployee.getEmail());
    employee.setFullName(annotatorEmployee.getFullName());
    employee.setId(annotatorEmployee.getId());
    employee.setLabel(annotatorEmployee.getLabel());
    employee.setLastProcessed(annotatorEmployee.getLastProcessed());
    employee.setLocation(annotatorEmployee.getLocation());
    employee.setPhone(annotatorEmployee.getPhone());

    return employee;
  }
}

/*% } %*/
