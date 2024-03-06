/*%@
  if (!feature.SensorViewer) return [];
  return data.dataWarehouse.sensors
    .map(function(sen) {
      return {
          fileName: normalize(sen.id, true) + 'Service.java',
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

/*% if (hasCategoricalDims) { %*/
import es.udc.lbd.gema.lps.model.service.dto.sensor.DataDTO;
/*% } else { %*/
import es.udc.lbd.gema.lps.model.service.dto.DataDTO;
/*% } %*/
import es.udc.lbd.gema.lps.model.service.dto.sensor./*%= normalize(context.id, true) %*/StateRequestDto;
import java.util.List;

public interface /*%= normalize(context.id, true) %*/Service {

  List<DataDTO> getData(/*%= normalize(context.id, true) %*/StateRequestDto params);

  /*% if (hasCategoricalDims) { %*/
  List<DataDTO> getData(Long id, /*%= normalize(context.id, true) %*/StateRequestDto params);
  /*% } else { %*/
  DataDTO getData(Long id, /*%= normalize(context.id, true) %*/StateRequestDto params);
  /*% } %*/

  /*% if (hasCategoricalDims) { %*/
  List<DataDTO> getDataHistogram(Long id, /*%= normalize(context.id, true) %*/StateRequestDto params);
  /*% } else { %*/
  DataDTO getDataHistogram(Long id, /*%= normalize(context.id, true) %*/StateRequestDto params);
  /*% } %*/
}
