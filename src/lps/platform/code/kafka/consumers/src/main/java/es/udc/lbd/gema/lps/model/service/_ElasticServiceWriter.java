/*%@
  if (!feature.SV_D_DataInsertion || !feature.SV_D_DI_Consumers) return [];
  return data.dataWarehouse.sensors
    .filter(function(sen) {
      return sen.datasource === 'elasticsearch';
    })
    .map(function(sen) {
      return {
          fileName: normalize(sen.id, false) + '/' + normalize(sen.id, true) + 'Writer.java',
          context: sen
      };
    });
%*/
package es.udc.lbd.gema.lps.model.service./*%= normalize(context.id, false) %*/;

import es.udc.lbd.gema.lps.model.domain./*%= normalize(context.id, true) %*/;
import es.udc.lbd.gema.lps.model.service.conditional.ConditionalOnKafkaProperty;
import es.udc.lbd.gema.lps.connectors.ElasticWriter;

import java.io.IOException;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;

@Service
@ConditionalOnKafkaProperty(topic = "/*%= normalize(context.id).toLowerCase() %*/", datasource = "/*%= context.datasource ? context.datasource : 'elasticsearch' %*/")
public class /*%= normalize(context.id, true) %*/Writer {


  private static final Logger logger =
    LoggerFactory.getLogger(/*%= normalize(context.id, true) %*/Writer.class);

  @Autowired private ElasticWriter elasticWriter;

  public void insertMedidas(String indexName, List</*%= normalize(context.id, true) %*/> medidas) {

    RestHighLevelClient restClient = elasticWriter.getRestClient();

    for (/*%= normalize(context.id, true) %*/ medida : medidas) {
      IndexRequest indexRequest = new IndexRequest(getRegexIndex(indexName, medida));
      // Generate random uuid string
      String uuid = java.util.UUID.randomUUID().toString();
      indexRequest.id(String.valueOf(uuid));
      indexRequest.source(medida.toJSON(), XContentType.JSON);

      try {
        restClient.index(indexRequest, RequestOptions.DEFAULT);
      } catch (IOException e) {
        // Known error, not a problem
        if (!e.getMessage().contains("Unable to parse response body for Response"))
          logger.error("Error inserting data to ElasticSearch: " + e.getMessage());
      }
    }
  }


  private String getRegexIndex(String indexName, /*%= normalize(context.id, true) %*/ medida) {
      DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
      LocalDateTime dateTime = LocalDateTime.parse(medida.getDate(), formatter);
      String year = String.valueOf(dateTime.getYear());
      String month =
          dateTime.getMonthValue() < 10
              ? "0" + dateTime.getMonthValue()
              : dateTime.getMonthValue() + "";
      String day =
          dateTime.getDayOfMonth() < 10
              ? "0" + dateTime.getDayOfMonth()
              : dateTime.getDayOfMonth() + "";
      return indexName + year + month + day;
    }
}
