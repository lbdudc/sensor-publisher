/*% if (feature.T_Swagger) { %*/
package es.udc.lbd.gema.lps.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import com.google.common.base.Predicates;

@Configuration
@EnableSwagger2
public class SwaggerConfig {

	@Bean
	public Docket swaggerSpringMvcPlugin() {
		return new Docket(DocumentationType.SWAGGER_2)
            .host("/*%= getExtraConfigFromSpec(data, 'server_deploy_url', 'http://localhost:8080') %*/")
            .useDefaultResponseMessages(false)
            .apiInfo(apiInfo())
            .select()
            .apis(Predicates.not(RequestHandlerSelectors.basePackage("org.springframework.boot")))
            .build() ;
	}

	private ApiInfo apiInfo() {
		return new ApiInfoBuilder()
            .title("/*%= data.basicData.name %*/")
            .description("API description")
			.termsOfServiceUrl("http://springfox.io")
            .contact(new Contact("LBD", "lbd.udc.es", "alejandro.cortinas@udc.es"))
            .license("Apache License Version 2.0")
			.licenseUrl("https://github.com/springfox/springfox/blob/master/LICENSE")
            .version("1.0-SNAPSHOT").build();
	}
}
/*% } %*/
