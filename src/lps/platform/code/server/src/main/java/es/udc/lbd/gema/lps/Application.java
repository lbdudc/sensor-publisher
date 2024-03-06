package es.udc.lbd.gema.lps;

import java.util.List;
import net.kaczmarzyk.spring.data.jpa.web.SpecificationArgumentResolver;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
/*% if (feature.T_MailSender) { %*/
import org.springframework.retry.annotation.EnableRetry;
/*% } %*/
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
/*% if (feature.SensorViewer) { %*/
/*% var hasElasticSensor = data?.dataWarehouse?.sensors?.find(function(sensor) { return sensor.datasource === 'elasticsearch'; }) !== undefined; %*/
/*% if (hasElasticSensor) { %*/
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
/*% } %*/
/*% } %*/


@SpringBootApplication
/*% if (feature.T_MailSender) { %*/
@EnableRetry
/*% } %*/
/*% if (feature.SensorViewer && hasElasticSensor) { %*/
@EnableJpaRepositories(
  excludeFilters =
      @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, value = ElasticsearchRepository.class))
@EnableElasticsearchRepositories(
  includeFilters =
      @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, value = ElasticsearchRepository.class),
  excludeFilters =
      @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, value = JpaRepository.class))
/*% } %*/
  public class Application implements WebMvcConfigurer {

  @Override
  public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
    argumentResolvers.add(new SpecificationArgumentResolver());
    argumentResolvers.add(new PageableHandlerMethodArgumentResolver());
  }

  public static void main(String[] args) {
    SpringApplication.run(Application.class, args);
  }
}
