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
    public static final String DOMAIN = "localhost:3000";
    public static final HttpProtocolBuilder HTTP_PROTOCOL = http.baseUrl("http://" + DOMAIN);
    public static final String CSV_FILENAME = "credentials_1000.csv";
    private static final Map<String, String> SENT_HEADERS = new HashMap<>() {{
        put("content-type", "application/json");
    }};
    private static final String QUESTION_TITLE = "Question from ";
    private static final String QUESTION_BODY = "Question for stress testing. What do you think?";
    private static final String ANSWER = "Answer for stress testing. I don't know. Answered by ";
    private static final int MIN_WAIT = 1;
    private static final int MAX_WAIT = 10;
    public static final int LOWER_BOUND_OF_USERNAME_POSTFIX = 1;
    public static final int UPPER_BOUND_OF_USERNAME_POSTFIX = 1000;

    private static final FeederBuilder<String> USER_FEEDER = csv("com/codecool/gatling/users/" + CSV_FILENAME).random();

    private static final Iterator<Map<String, Object>> RANDOM_SEARCH_QUERY_FEEDER =
            Stream.generate((Supplier<Map<String, Object>>) () -> {
                        String randomSearchQuery = String.valueOf(
                                ThreadLocalRandom.current()
                                        .nextInt(LOWER_BOUND_OF_USERNAME_POSTFIX, UPPER_BOUND_OF_USERNAME_POSTFIX + 1));
                        return Map.of("query", randomSearchQuery);
            }).iterator();

    private static final ChainBuilder LOGIN =
            feed(USER_FEEDER)
                    .exec(http("LogIn").post("/api/auth/login")
                            .headers(SENT_HEADERS)
                            .body(StringBody(
                                    "{" +
                                            "\"username\":\"#{USERNAME}\"," +
                                            "\"password\":\"#{PASSWORD}\"" +
                                        "}"))
                            .check(status().is(200))
                            .check(jmesPath("data.userid")
                                    .saveAs("userId")))
                    .exec(getCookieValue(CookieKey("jwt")
                            .withSecure(true)))
                    .exec(addCookie(Cookie("jwt", "#{jwt}")));

    private static final ChainBuilder LOGOUT =
            exec(http("LogOut").post("/api/auth/logout")
                    .check(status().is(200)));

    private static final ChainBuilder CREATE_QUESTION =
            exec(http("Create Question").post("/api/questions")
                    .headers(SENT_HEADERS)
                    .body(StringBody(
                            "{" +
                                    "\"userId\":\"#{userId}\"," +
                                    "\"title\":\"" + QUESTION_TITLE + " #{USERNAME}\"," +
                                    "\"content\":\"" + QUESTION_BODY + "\"" +
                                "}"))
                    .check(status().is(200)));

    private static final ChainBuilder CREATE_ANSWER =
            exec(http("Create Answer").post("/api/answers")
                    .headers(SENT_HEADERS)
                    .body(StringBody(
                            "{" +
                                    "\"userId\":\"#{userId}\"," +
                                    "\"questionId\":\"#{RANDOM_QUESTION_ID}\"," +
                                    "\"content\":\"" + ANSWER + " #{USERNAME}\"" +
                                "}"))
                    .check(status().is(200)));

    private static final ChainBuilder GET_ALL_QUESTIONS =
            exec(http("Get all questions").get("/api/questions/all")
                    .check(jsonPath("$.data[*].id")
                            .findRandom()
                            .saveAs("RANDOM_QUESTION_ID"))
                    .check(status().is(200)));

    private static final ChainBuilder OPEN_A_QUESTIONS_DETAILS =
            exec(http("Open Question Details").get("/api/questions/#{RANDOM_QUESTION_ID}")
                    .check(status().is(200)));

    private static final ChainBuilder SEARCH =
            feed(RANDOM_SEARCH_QUERY_FEEDER)
                    .exec(http("Search Questions").get("/api/questions/search/#{query}")
                            .check(jsonPath("$.data[*].id")
                                    .findRandom()
                                    .saveAs("RANDOM_QUESTION_ID"))
                            .check(status().is(200)));

    private static final ChainBuilder BROWSE_ALL_QUESTIONS =
            repeat(3).on(
                    pace(MIN_WAIT, MAX_WAIT)
                            .exec(GET_ALL_QUESTIONS)
                            .exec(OPEN_A_QUESTIONS_DETAILS));

    private static final ScenarioBuilder POST_NEW_QUESTION = scenario("Post new question")
            .exec(LOGIN)
            .pause(MIN_WAIT, MAX_WAIT)
            .exec(CREATE_QUESTION)
            .pause(MIN_WAIT, MAX_WAIT)
            .exec(LOGOUT);

    private static final ScenarioBuilder ANSWER_A_QUESTION = scenario("Answer a question")
            .exec(LOGIN)
            .pause(MIN_WAIT, MAX_WAIT)
            .exec(BROWSE_ALL_QUESTIONS)
            .pause(MIN_WAIT, MAX_WAIT)
            .exec(SEARCH)
            .pause(MIN_WAIT, MAX_WAIT)
            .exec(OPEN_A_QUESTIONS_DETAILS)
            .pause(MIN_WAIT, MAX_WAIT)
            .exec(CREATE_ANSWER)
            .pause(MIN_WAIT, MAX_WAIT)
            .exec(LOGOUT);

    private static final ScenarioBuilder BROWSE_CONTENT = scenario("Browse content")
            .exec(BROWSE_ALL_QUESTIONS)
            .pause(MIN_WAIT, MAX_WAIT)
            .exec(SEARCH)
            .pause(MIN_WAIT, MAX_WAIT)
            .exec(OPEN_A_QUESTIONS_DETAILS);

    {
        setUp(
                POST_NEW_QUESTION.injectOpen(
                    atOnceUsers(2),
                    rampUsersPerSec(1).to(5).during(30).randomized()),
                ANSWER_A_QUESTION.injectOpen(
                    nothingFor(5),
                    atOnceUsers(10),
                    nothingFor(2),
                    rampUsersPerSec(1).to(10).during(40).randomized()),
                BROWSE_CONTENT.injectOpen(
                    nothingFor(15),
                    rampUsersPerSec(5).to(10).during(80).randomized())
        ).protocols(HTTP_PROTOCOL);
    }
}
