package com.codecool.gatling;

import java.time.Duration;
import java.util.*;

import io.gatling.javaapi.core.*;
import io.gatling.javaapi.http.*;
import io.gatling.javaapi.jdbc.*;

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.*;
import static io.gatling.javaapi.jdbc.JdbcDsl.*;

public class RecordedSimulation extends Simulation {

  private HttpProtocolBuilder httpProtocol = http
    .baseUrl("http://localhost:5000")
    .inferHtmlResources(AllowList(), DenyList(".*\\.js", ".*\\.css", ".*\\.gif", ".*\\.jpeg", ".*\\.jpg", ".*\\.ico", ".*\\.woff", ".*\\.woff2", ".*\\.(t|o)tf", ".*\\.png", ".*\\.svg", ".*detectportal\\.firefox\\.com.*"))
    .acceptHeader("*/*")
    .acceptEncodingHeader("gzip, deflate, br")
    .acceptLanguageHeader("en-GB,en-US;q=0.9,en;q=0.8")
    .userAgentHeader("Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/122.0.0.0 Safari/537.36");

  private ScenarioBuilder scn = scenario("RecordedSimulation")
    .exec(
      http("Main Page")
        .get("/")
        .resources(
          http("All questions")
            .get("/api/questions/all")
        ),
      pause(5),
      http("Sign up")
        .post("/api/auth/register")
        .body(RawFileBody("com/codecool/gatling/recordedsimulation/0003_request.json"))
              .header("content-type", "application/json"),
      pause(5),
      http("Log in")
        .post("/api/auth/login")
        .body(RawFileBody("com/codecool/gatling/recordedsimulation/0004_request.json"))
              .header("content-type", "application/json")
        .resources(
          http("All questions")
            .get("/api/questions/all")
        ),
      pause(5),
      http("Log out")
        .post("/api/auth/logout")
        .resources(
          http("All questions")
            .get("/api/questions/all")
        )
    );

  {
	  setUp(scn.injectOpen(atOnceUsers(1))).protocols(httpProtocol);
  }
}
