package soa.eip;

import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

@Component
public class Router extends RouteBuilder {

  public static final String DIRECT_URI = "direct:twitter";

  @Override
  public void configure() {

    Processor maxProcessor = exchange -> {
      String body = exchange.getIn().getBody(String.class),
          newBody = "",
          max = "";
      for (String token : body.split(" ")) {
        if (token.matches("max:[0-9]+")) {
          max = token.split(":")[1];
        } else {
          newBody += token + " ";
        }
        exchange.getIn().setBody(newBody);
        exchange.getIn().setHeader("count", max);
      }
    };

    from(DIRECT_URI)
      .log("Body contains \"${body}\"")
      .log("Searching twitter for \"${body}\"!")
      .choice()
        .when(body().regex(".*max:[0-9]+.*"))
        .process(maxProcessor)
        .toD("twitter-search:${body}?count=${header.count}")
        .endChoice()
      .otherwise()
        .toD("twitter-search:${body}")
        .endChoice()
        .end()
      .log("Body now contains the response from twitter:\n${body}");
  }
}
