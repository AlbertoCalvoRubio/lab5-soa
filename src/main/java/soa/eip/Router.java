package soa.eip;

import org.apache.camel.CamelContext;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultCamelContext;
import org.springframework.stereotype.Component;

@Component
public class Router extends RouteBuilder {

  public static final String DIRECT_URI = "direct:twitter";

  @Override
  public void configure() {
    from("rest:get:search")
        .setBody(exchange -> exchange.getIn().getHeader("q"))
        .removeHeader("q")
        .log("Body contains \"${body}\"")
        .log("Searching twitter for \"${body}\"!")
        .toD("twitter-search:${body}")
        // Transform Twitter's POJO to Json
        .marshal()
        .json()
        .log("Body now contains the response from twitter:\n${body}");
  }
}
