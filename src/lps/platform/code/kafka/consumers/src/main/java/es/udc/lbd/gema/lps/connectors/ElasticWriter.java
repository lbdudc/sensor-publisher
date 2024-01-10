/*% if (feature.SV_D_DataInsertion && feature.SV_D_DI_Consumers) { %*/
package es.udc.lbd.gema.lps.connectors;

import java.io.IOException;
import javax.annotation.PostConstruct;
import org.elasticsearch.client.RestHighLevelClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.elasticsearch.client.ClientConfiguration;
import org.springframework.data.elasticsearch.client.RestClients;
import org.springframework.stereotype.Component;

@Component
public class ElasticWriter {

  private static final Logger logger = LoggerFactory.getLogger(ElasticWriter.class);

  @Value("${elastic.ipPort}")
  private String ipPort;

  @Value("${elastic.user}")
  private String user;

  @Value("${elastic.pass}")
  private String pass;

  private RestHighLevelClient restClient;

  public ElasticWriter() {}

  @PostConstruct
  private void connect() {
    restClient =
        RestClients.create(
                ClientConfiguration.builder().connectedTo(ipPort).withBasicAuth(user, pass).build())
            .rest();

    System.out.println("Connected to ElasticSearch");
  }

  public void disconnect() {
    try {
      restClient.close();
    } catch (IOException e) {
      logger.error("Error closing ElasticSearch connection");
    }
  }

  public RestHighLevelClient getRestClient() {
    return restClient;
  }
}
/*% } %*/
