package com.bhuvancom.ecom.exception;

import org.springframework.boot.web.server.ConfigurableWebServerFactory;
import org.springframework.boot.web.server.ErrorPage;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;

/**
 * Created Using IntelliJ Idea
 *
 * @author Bhuvaneshvar
 * Date    7/25/2021
 * Time    4:52 AM
 * Project ecomNew
 */
@Configuration
public class ErrorPageConfig {
    @Bean
    public WebServerFactoryCustomizer<ConfigurableWebServerFactory> webServerFactoryCustomizer() {
        return new WebServerFactoryCustomizer<ConfigurableWebServerFactory>() {
            @Override
            public void customize(ConfigurableWebServerFactory factory) {
                ErrorPage page404 = new ErrorPage(HttpStatus.NOT_FOUND, "/error/404");
                ErrorPage page500 = new ErrorPage(HttpStatus.INTERNAL_SERVER_ERROR, "/error/500");
                factory.addErrorPages(page404, page500);
            }
        };
    }
}
