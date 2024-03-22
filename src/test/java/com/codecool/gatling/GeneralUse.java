package com.codecool.gatling;

import io.gatling.javaapi.core.*;
import io.gatling.javaapi.http.HttpProtocolBuilder;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Stream;
import java.util.function.Supplier;

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.*;

public class GeneralUse extends Simulation {
    private static final String QUESTION_TITLE = "Question from ";
    private static final String QUESTION_BODY = "Question for stress testing. What do you think?";
    private static final String ANSWER = "Answer for stress testing. I don't know. Answered by ";
    private static final int MIN_WAIT = 1;
    private static final int MAX_WAIT = 10;
    public static final int LOWER_BOUND_OF_USERNAME_POSTFIX = 1;
    public static final int UPPER_BOUND_OF_USERNAME_POSTFIX = 1000;

    private HttpProtocolBuilder httpProtocol = http
            .baseUrl("http://localhost:3000")
            .acceptHeader("application/json");

    private FeederBuilder.Batchable<String> userFeeder = csv("com/codecool/gatling/users/credentials_1000.csv").random();

    private Iterator<Map<String, Object>> randomSearchQueryFeeder =
            Stream.generate((Supplier<Map<String, Object>>) () -> {
                        String randomSearchQuery = String.valueOf(
                                ThreadLocalRandom.current()
                                        .nextInt(LOWER_BOUND_OF_USERNAME_POSTFIX, UPPER_BOUND_OF_USERNAME_POSTFIX + 1)
                        );
                        return Map.of("query", randomSearchQuery);
                    }
            ).iterator();

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
                                    )
                            )
                            .check(status().is(200))
                            .check(jmesPath("data.userid")
                                    .saveAs("userId")
                            )
                    )
                    .exec(getCookieValue(CookieKey("jwt")
                            .withSecure(true))
                    )
                    .exec(addCookie(Cookie("jwt", "#{jwt}")))
                    .pause(MIN_WAIT, MAX_WAIT);

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
                            )
                    )
                    .check(status().is(200))
            )
            .pause(MIN_WAIT, MAX_WAIT);

    private ChainBuilder getAllQuestions =
            exec(http("Get all questions").get("/api/questions/all")
                    .check(jsonPath("$.data[*].id")
                            .findRandom()
                            .saveAs("RANDOM_QUESTION_ID")
                    )
            )
            .pause(MIN_WAIT, MAX_WAIT);

    private ChainBuilder openQuestionDetails =
            exec(http("Open Question Details").get("/api/questions/#{RANDOM_QUESTION_ID}"))
            .pause(MIN_WAIT, MAX_WAIT);

    private ChainBuilder createAnswer =
            exec(http("Create Answer").post("/api/answers")
                    .headers(sentHeaders)
                    .body(StringBody(
                            "{" +
                                    "\"userId\":\"#{userId}\"," +
                                    "\"questionId\":\"#{RANDOM_QUESTION_ID}\"," +
                                    "\"content\":\"" + ANSWER + " #{USERNAME}\"" +
                                "}"
                            )
                    )
                    .check(status().is(200))
            )
            .pause(MIN_WAIT, MAX_WAIT);

    private ChainBuilder search =
            feed(randomSearchQueryFeeder)
                    .exec(http("Search Questions").get("/api/questions/search/#{query}")
                            .check(jsonPath("$.data[*].id")
                                    .findRandom()
                                    .saveAs("RANDOM_QUESTION_ID")
                            )
                    )
                    .pause(MIN_WAIT, MAX_WAIT)
                    .exec(openQuestionDetails);

    private ChainBuilder browse =
            repeat(3).on(
                    pace(MIN_WAIT, MAX_WAIT)
                            .exec(getAllQuestions)
                            .exec(openQuestionDetails)
                    )
                    .pause(MIN_WAIT, MAX_WAIT)
                    .exec(search)
                    .pause(MIN_WAIT, MAX_WAIT);


    private ScenarioBuilder postingQuestions = scenario("Post new question")
            .exec(login, createQuestion, logout);

    private ScenarioBuilder answeringQuestions = scenario("Answer a question")
            .exec(login, browse, createAnswer, logout);

    private ScenarioBuilder browsingQuestions = scenario("Browse content")
            .exec(browse);

    {
        setUp(postingQuestions.injectOpen(
                    atOnceUsers(2),
                    rampUsersPerSec(1).to(5).during(30).randomized()
                ),
                answeringQuestions.injectOpen(
                    nothingFor(5),
                    atOnceUsers(10),
                    nothingFor(2),
                    rampUsersPerSec(1).to(10).during(40).randomized()
                ),
                browsingQuestions.injectOpen(
                    nothingFor(15),
                    rampUsersPerSec(5).to(10).during(80).randomized()
                )
        ).protocols(httpProtocol);
    }
}
