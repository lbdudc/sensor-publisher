/*%@
  if (!feature.SensorViewer) return [];
  return data.dataWarehouse.sensors
    .map(function(sen) {
      return {
          fileName: normalize(sen.id, true) + 'Repository.java',
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
package es.udc.lbd.gema.lps.model.repository.sensor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import es.udc.lbd.gema.lps.model.domain.sensor.CalcAggregation;
import es.udc.lbd.gema.lps.model.domain.sensor.TemporalAggregation;
/*% if (hasCategoricalDims) { %*/
import es.udc.lbd.gema.lps.model.service.dto.sensor.DataDTO;
/*% } else { %*/
import es.udc.lbd.gema.lps.model.service.dto.DataDTO;
/*% } %*/
import es.udc.lbd.gema.lps.model.domain.sensor./*%= normalize(context.id, true) %*/SpatialAggregation;
import es.udc.lbd.gema.lps.model.domain.sensor./*%= normalize(context.id, true) %*/SpatialFilter;

public interface /*%= normalize(context.id, true) %*/Repository {
  List<DataDTO> getData(
      LocalDateTime start,
      LocalDateTime end,
      TemporalAggregation temporalAggregation,
      CalcAggregation calc,
      String field,
      Map<String, Object> filters,
      /*%= normalize(context.id, true) %*/SpatialAggregation spatialAggregation,
      /*%= normalize(context.id, true) %*/SpatialFilter spatialFilterType,
      /*% if (hasCategoricalDims) { %*/
      String categoryAggregation,
      String categoryFilter,
      String categoryFrom,
      String categoryTo,
      /*% } %*/
      Integer spatialFilter);

  /*% if (hasCategoricalDims) { %*/
    List<DataDTO> getDataBySensorId(
      Long sensorId,
      LocalDateTime start,
      LocalDateTime end,
      Map<String, Object> filters,
      /*%= normalize(context.id, true) %*/SpatialAggregation spatialAggregation,
      TemporalAggregation temporalAggregation,
      List<CalcAggregation> calcAggregation,
      String categoryAggregation,
      String categoryFrom,
      String categoryTo,
      List<String> properties);
  /*% } else { %*/
  DataDTO getDataBySensorId(
      Long sensorId,
      LocalDateTime start,
      LocalDateTime end,
      Map<String, Object> filters,
      /*%= normalize(context.id, true) %*/SpatialAggregation spatialAggregation,
      TemporalAggregation temporalAggregation,
      List<CalcAggregation> calcAggregation,
      String propertyAggregation
      );
  /*% } %*/

  /*% if (hasCategoricalDims) { %*/
    List<DataDTO> getHistogramDataBySensorId(
      Long sensorId,
      LocalDateTime start,
      LocalDateTime end,
      Map<String, Object> filters,
      /*%= normalize(context.id, true) %*/SpatialAggregation spatialAggregation,
      TemporalAggregation temporalAggregation,
      List<CalcAggregation> calcAggregation,
      String categoryAggregation,
      List<String> propertyAggregation);
  /*% } else { %*/
  DataDTO getHistogramDataBySensorId(
      Long sensorId,
      LocalDateTime start,
      LocalDateTime end,
      Map<String, Object> filters,
      /*%= normalize(context.id, true) %*/SpatialAggregation spatialAggregation,
      TemporalAggregation temporalAggregation,
      List<CalcAggregation> calcAggregation,
      List<String> propertyAggregation
      );
  /*% } %*/
}
