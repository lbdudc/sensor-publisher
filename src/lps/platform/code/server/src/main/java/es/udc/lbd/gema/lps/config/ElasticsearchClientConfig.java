/*% if (feature.SensorViewer) { %*/
/*% var hasElasticSensor = data?.dataWarehouse?.sensors?.find(function(sensor) { return sensor.datasource === 'elasticsearch'; }) !== undefined; %*/
/*% if (hasElasticSensor) { %*/
package es.udc.lbd.gema.lps.config;

import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.client.ClientConfiguration;
import org.springframework.data.elasticsearch.client.RestClients;
import org.springframework.data.elasticsearch.config.AbstractElasticsearchConfiguration;

@Configuration
public class ElasticsearchClientConfig extends AbstractElasticsearchConfiguration {

  @Value("${spring.elasticsearch.rest.uris}")
  private String host;

  @Value("${spring.elasticsearch.rest.connection-timeout}")
  private Integer connectionTimeout;

  @Value("${spring.elasticsearch.rest.username}")
  private String username;

  @Value("${spring.elasticsearch.rest.password}")
  private String password;

  @Override
  @Bean
  public RestHighLevelClient elasticsearchClient() {
    final ClientConfiguration clientConfiguration =
        ClientConfiguration.builder()
            .connectedTo(host)
            .withSocketTimeout(connectionTimeout)
            .withBasicAuth(username, password)
            .build();

    return RestClients.create(clientConfiguration).rest();
  }
}
/*% } %*/
/*% } %*/
