package com.example.graceful.shutdown.config;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import org.apache.catalina.connector.Connector;
import org.apache.coyote.ProtocolHandler;
import org.springframework.boot.web.embedded.tomcat.TomcatConnectorCustomizer;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;

public class GracefulShutdown implements TomcatConnectorCustomizer, ApplicationListener<ContextClosedEvent> {

  private volatile  Connector connector;
  private static final String protocolHandlerClassName = "org.apache.coyote.http11.Http11NioProtocol";

  @Override
  public void customize(Connector connector) {
    this.connector = connector;
  }

  @Override
  public void onApplicationEvent(ContextClosedEvent contextClosedEvent) {

    this.protocolHandlerClose();

    Executor executor = this.connector.getProtocolHandler().getExecutor();

    if (executor instanceof ThreadPoolExecutor) {
      ThreadPoolExecutor threadPoolExecutor = (ThreadPoolExecutor) executor;

      threadPoolExecutor.shutdown();

      try {
        if (!threadPoolExecutor.awaitTermination(60, TimeUnit.SECONDS)) {
          threadPoolExecutor.shutdownNow();
        }
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
  }

  private void protocolHandlerClose() {
    try {
      Class<?> clazz = Class.forName(protocolHandlerClassName);
      ProtocolHandler p = (ProtocolHandler) clazz.getConstructor().newInstance();
      p.pause();

    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
