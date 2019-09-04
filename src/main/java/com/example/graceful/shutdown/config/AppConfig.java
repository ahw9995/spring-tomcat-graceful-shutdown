package com.example.graceful.shutdown.config;

import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.servlet.server.ConfigurableServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class AppConfig {

  @Bean
  public GracefulShutdown gracefulShutdown() {
    return new GracefulShutdown();
  }

  @Bean
  public ConfigurableServletWebServerFactory webServerFactory(final GracefulShutdown gracefulShutdown) {
    TomcatServletWebServerFactory f = new TomcatServletWebServerFactory();
    f.addConnectorCustomizers(gracefulShutdown);
    return f;
  }
}
