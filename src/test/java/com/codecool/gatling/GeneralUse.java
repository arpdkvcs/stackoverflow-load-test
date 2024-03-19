package com.codecool.gatling;

import io.gatling.javaapi.core.FeederBuilder;
import io.gatling.javaapi.http.HttpProtocolBuilder;

import static io.gatling.javaapi.core.CoreDsl.csv;
import static io.gatling.javaapi.http.HttpDsl.http;

public class GeneralUse {
    private final String questionTitle = "Question from ";
    private final String questionBody = "Question for stress testing. What do you think?";
    private final String answerBody = "Answer for stress testing. I don't know. Answered by ";

    private HttpProtocolBuilder httpProtocol = http
            .baseUrl("http://localhost:3000")
            .acceptHeader("application/json");

    private FeederBuilder.Batchable<String> questionTakerFeeder = csv("com/codecool/gatling/users/QuestionTakers_200.csv").random();

    private FeederBuilder.Batchable<String> answererFeeder = csv("com/codecool/gatling/users/Answerers_800.csv").random();


}
