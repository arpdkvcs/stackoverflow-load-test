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

    public static final String DOMAIN = "localhost:5000";
    public static final HttpProtocolBuilder HTTP_PROTOCOL = http.baseUrl("http://" + DOMAIN);

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
        setUp(scn.injectOpen(atOnceUsers(1))).protocols(HTTP_PROTOCOL);
    }
}
