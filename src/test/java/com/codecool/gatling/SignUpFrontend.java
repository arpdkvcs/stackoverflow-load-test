package com.codecool.gatling;

import io.gatling.javaapi.core.ChainBuilder;
import io.gatling.javaapi.core.FeederBuilder;
import io.gatling.javaapi.core.ScenarioBuilder;
import io.gatling.javaapi.core.Simulation;
import io.gatling.javaapi.http.HttpProtocolBuilder;

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.http;

public class SignUpFrontend extends Simulation {

    private HttpProtocolBuilder httpProtocol = http
            .baseUrl("localhost:5000")
            .acceptHeader("application/json");

    private FeederBuilder.Batchable<String> feeder = csv("com/codecool/gatling/signup/credentials_10000.csv").circular();

    private final int numberOfRecords = feeder.recordsCount();

    private ChainBuilder signUp =
            exec(http("Main Page").get("/"))
                    .pause(2, 5)
                    .feed(feeder)
                    .exec(http("Sign up").post("/api/auth/register")
                            .header("content-type", "application/json")
                            .body(StringBody("{\"username\":\"#{USERNAME}\",\"password\":\"#{PASSWORD}\"}"))
                    );


    private ScenarioBuilder newUser = scenario("Register new user")
            .exec(signUp);
    {
        setUp(newUser.injectOpen(rampUsers(numberOfRecords).during(400))
        ).protocols(httpProtocol);
    }
}
