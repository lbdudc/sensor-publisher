/*%@
  if (!feature.SensorViewer) return [];
  return data.dataWarehouse.sensors
    .map(function(sen) {
      return {
          fileName: normalize(sen.id, true) + 'ServiceImpl.java',
          context: sen
      };
    });
%*/
/*%
  const dimensions = [];
  data.dataWarehouse.sensors
    .filter(sen => sen.id === context.id)
    .forEach(function(sensor) {
      const dims = sensor.dimensions;
      dims
        .filter(dim => dim.type === "CATEGORICAL")
        .forEach(dim => {
          dimensions.push(dim);
        });
  });
  var hasCategoricalDims = dimensions.length > 0;
%*/
package es.udc.lbd.gema.lps.model.service.sensor;

import static java.time.temporal.TemporalAdjusters.lastDayOfMonth;

import es.udc.lbd.gema.lps.model.domain.sensor.CalcAggregation;
import es.udc.lbd.gema.lps.model.domain.sensor.TemporalAggregation;
import es.udc.lbd.gema.lps.model.repository.sensor./*%= normalize(context.id, true) %*/Repository;
/*% if (hasCategoricalDims) { %*/
import es.udc.lbd.gema.lps.model.service.dto.sensor.DataDTO;
/*% } else { %*/
import es.udc.lbd.gema.lps.model.service.dto.DataDTO;
/*% } %*/
import es.udc.lbd.gema.lps.model.service.dto.sensor./*%= normalize(context.id, true) %*/StateRequestDto;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;
import javax.inject.Inject;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true, rollbackFor = Exception.class)
public class /*%= normalize(context.id, true) %*/ServiceImpl implements /*%= normalize(context.id, true) %*/Service {

  @Inject private /*%= normalize(context.id, true) %*/Repository /*%= normalize(context.id, true) %*/Repository;

  @Override
  public List<DataDTO> getData(/*%= normalize(context.id, true) %*/StateRequestDto params) {
    return /*%= normalize(context.id, true) %*/Repository.getData(
        getStart(params),
        getEnd(params),
        params.getTemporalAggregation(),
        params.getCalcAggregation(),
        params.getPropertyAggregation(),
        params.getFilters(),
        params.getSpatialAggregation(),
        params.getSpatialFilter(),
        /*% if (hasCategoricalDims) { %*/
        params.getCategoryAggregation(),
        params.getCategoryFilter(),
        params.getCategoryFrom(),
        params.getCategoryTo(),
        /*% } %*/
        params.getSpatialFilterId());
  }

  @Override
  /*% if (hasCategoricalDims) { %*/
  public List<DataDTO> getData(Long id, /*%= normalize(context.id, true) %*/StateRequestDto params) {
  /*% } else { %*/
  public DataDTO getData(Long id, /*%= normalize(context.id, true) %*/StateRequestDto params) {
  /*% } %*/

    /*% if (hasCategoricalDims) { %*/
    List<CalcAggregation> calcAggregations = Arrays.asList(CalcAggregation.AVERAGE);
    /*% } else { %*/
    List<CalcAggregation> calcAggregations =
        Arrays.asList(CalcAggregation.AVERAGE, CalcAggregation.MAX, CalcAggregation.MIN);
    /*% } %*/

    return /*%= normalize(context.id, true) %*/Repository.getDataBySensorId(
        id,
        getStart(params),
        getEnd(params),
        params.getFilters(),
        params.getSpatialAggregation(),
        params.getTemporalAggregation(),
        calcAggregations,
        /*% if (hasCategoricalDims) { %*/
        params.getCategoryAggregation(),
        params.getCategoryFrom(),
        params.getCategoryTo(),
        params.getProperties());
        /*% } else { %*/
        params.getPropertyAggregation());
        /*% } %*/
  }

  /** PRIVATE METHODS * */
  private LocalDateTime getStart(/*%= normalize(context.id, true) %*/StateRequestDto params) {
    if (params.getTemporalAggregation() == TemporalAggregation.MONTH)
      return LocalDate.now()
          .withYear(params.getStart().getYear())
          .withMonth(params.getStart().getMonthValue())
          .withDayOfMonth(1)
          .atStartOfDay();
    else if (params.getTemporalAggregation() == TemporalAggregation.YEAR)
      return LocalDate.now()
          .withYear(params.getStart().getYear())
          .withMonth(1)
          .withDayOfMonth(1)
          .atStartOfDay();
    else return params.getStart();
  }

  private LocalDateTime getEnd(/*%= normalize(context.id, true) %*/StateRequestDto params) {
    if (params.getTemporalAggregation() == TemporalAggregation.MONTH)
      return LocalDate.now()
          .withYear(params.getEnd().getYear())
          .withMonth(params.getEnd().getMonthValue())
          .with(lastDayOfMonth())
          .atTime(LocalTime.MAX);
    else if (params.getTemporalAggregation() == TemporalAggregation.YEAR)
      return LocalDate.now()
          .withYear(params.getEnd().getYear())
          .withMonth(12)
          .with(lastDayOfMonth())
          .atTime(LocalTime.MAX);
    else return params.getEnd();
  }

  @Override
  /*% if (hasCategoricalDims) { %*/
  public List<DataDTO> getDataHistogram(Long id, /*%= normalize(context.id, true) %*/StateRequestDto params) {
  /*% } else { %*/
  public DataDTO getDataHistogram(Long id, /*%= normalize(context.id, true) %*/StateRequestDto params) {
  /*% } %*/

    /*% if (hasCategoricalDims) { %*/
    List<CalcAggregation> calcAggregations = Arrays.asList(CalcAggregation.AVERAGE);
    /*% } else { %*/
    List<CalcAggregation> calcAggregations =
        Arrays.asList(CalcAggregation.AVERAGE, CalcAggregation.MAX, CalcAggregation.MIN);
    /*% } %*/

    return /*%= normalize(context.id, true) %*/Repository.getHistogramDataBySensorId(
        id,
        getStart(params),
        getEnd(params),
        params.getFilters(),
        params.getSpatialAggregation(),
        params.getTemporalAggregation(),
        calcAggregations,
        /*% if (hasCategoricalDims) { %*/
        params.getCategoryAggregation(),
        /*% } %*/
        params.getProperties());
  }
}
