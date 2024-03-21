package com.codecool.gatling;

import io.gatling.javaapi.core.ChainBuilder;
import io.gatling.javaapi.core.FeederBuilder;
import io.gatling.javaapi.core.ScenarioBuilder;
import io.gatling.javaapi.core.Simulation;
import io.gatling.javaapi.http.HttpProtocolBuilder;

import java.util.HashMap;
import java.util.Map;

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.*;

public class GeneralUse extends Simulation {
    private static final String QUESTION_TITLE = "Question from ";
    private static final String QUESTION_BODY = "Question for stress testing. What do you think?";
    private static final String ANSWER = "Answer for stress testing. I don't know. Answered by ";

    private static final int MIN_WAIT = 2;
    private static final int MAX_WAIT = 5;

    private HttpProtocolBuilder httpProtocol = http
            .baseUrl("http://localhost:3000")
            .acceptHeader("application/json");

    private FeederBuilder.Batchable<String> userFeeder = csv("com/codecool/gatling/users/credentials_1000.csv").random();

    private Map<String, String> sentHeaders = new HashMap<>() {{
        put("content-type", "application/json");
    }};

    private ChainBuilder login =
            feed(userFeeder)
                    .exec(http("LogIn").post("/api/auth/login")
                            .headers(sentHeaders)
                            .body(StringBody(
                                    "{" +
                                            "\"username\":\"#{USERNAME}\"," +
                                            "\"password\":\"#{PASSWORD}\"" +
                                            "}"
                            ))
                            .check(status().is(200))
                            .check(jmesPath("data.userid")
                                    .saveAs("userId")))
                    .pause(MIN_WAIT, MAX_WAIT)
                    .exec(getCookieValue(CookieKey("jwt")
                            .withSecure(true)))
                    .exec(addCookie(Cookie("jwt", "#{jwt}")));

    private ChainBuilder logout =
            exec(http("LogOut").post("/api/auth/logout"));

    private ChainBuilder createQuestion =
                exec(http("Create Question").post("/api/questions")
                        .headers(sentHeaders)
                        .body(StringBody(
                                "{" +
                                        "\"userId\":\"#{userId}\"," +
                                        "\"title\":\"" + QUESTION_TITLE + " #{USERNAME}\"," +
                                        "\"content\":\"" + QUESTION_BODY + "\"" +
                                    "}"
                        ))
                )
                .pause(MIN_WAIT, MAX_WAIT);

    private ChainBuilder createAnswer =
                exec(http("Get all questions").get("/api/questions/all")
                        .check(jsonPath("$.data[*].id")
                                .findRandom()
                                .saveAs("RANDOM_QUESTION_ID")))
                .exec(http("Create Answer").post("/api/answers")
                        .headers(sentHeaders)
                        .body(StringBody(
                                "{" +
                                        "\"userId\":\"#{userId}\"," +
                                        "\"questionId\":\"#{RANDOM_QUESTION_ID}\"," +
                                        "\"content\":\"" + ANSWER + " #{USERNAME}\"" +
                                    "}"
                                )
                        )
                )
                .pause(MIN_WAIT, MAX_WAIT);

    private ScenarioBuilder newQuestion = scenario("Create new question")
            .exec(login, createQuestion, logout);

    private ScenarioBuilder newAnswer = scenario("Create new answer")
            .exec(login, createAnswer, logout);

    {
        setUp(newQuestion.injectOpen(rampUsersPerSec(2).to(5).during(30))
                .andThen(newAnswer.injectOpen(rampUsersPerSec(5).to(10).during(60)))
        ).protocols(httpProtocol);
    }
}
