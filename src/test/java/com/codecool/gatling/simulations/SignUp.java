package com.codecool.gatling.simulations;

import io.gatling.javaapi.core.ChainBuilder;
import io.gatling.javaapi.core.ScenarioBuilder;
import io.gatling.javaapi.core.Simulation;
import io.gatling.javaapi.http.HttpProtocolBuilder;

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.http;

public class SignUp extends Simulation {

    private HttpProtocolBuilder httpProtocol = http
            .baseUrl("http://localhost:5000")
            .acceptHeader("application/json");

    private ChainBuilder signUp =
            exec(http("Main page").get("/"))
                    .pause(2, 5)
                    .exec(http("Sign up").post("/api/auth/register")
                            .header("content-type", "application/json")
                            .body(StringBody("{\"username\":\"adi\",\"password\":\"adi_password\"}"))
                    )
                    .pause(2, 5);

    private ChainBuilder login =
            exec(http("Login").post("/api/auth/login")
                    .header("content-type", "application/json")
                    .body(StringBody("{\"username\":\"adi\",\"password\":\"adi_password\"}"))
            )
                    .pause(2, 5);

    private ChainBuilder logout =
            exec(http("Logout").post("/api/auth/logout"))
                    .pause(2, 5);

    private ScenarioBuilder newUser = scenario("Register new user")
            .exec(signUp);
    {
        setUp(newUser.injectOpen(atOnceUsers(1))
        ).protocols(httpProtocol);
    }
}
