package stackoverflow;

import io.gatling.javaapi.core.FeederBuilder;
import io.gatling.javaapi.core.ScenarioBuilder;
import io.gatling.javaapi.core.Simulation;
import io.gatling.javaapi.http.HttpProtocolBuilder;

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.http;

public class SignUpFrontend extends Simulation {

    private HttpProtocolBuilder httpProtocol = http
            .baseUrl("http://localhost:5000")
            .acceptHeader("application/json");

    private FeederBuilder.Batchable<String> feeder = csv("users/credentials_1000.csv").circular();

    private final int numberOfRecords = feeder.recordsCount();
    private static final int MIN_WAIT = 2;
    private static final int MAX_WAIT = 5;

    private ScenarioBuilder newUser = scenario("Register new user")
            .exec(http("Main Page").get("/"))
            .pause(MIN_WAIT, MAX_WAIT)
            .feed(feeder)
            .exec(http("Sign up").post("/api/auth/register")
                    .header("content-type", "application/json")
                    .body(StringBody(
                            "{" +
                                    "\"username\":\"#{USERNAME}\"," +
                                    "\"password\":\"#{PASSWORD}\"" +
                                    "}"
                    ))
            );
    {
        setUp(newUser.injectOpen(rampUsers(numberOfRecords).during(40))
        ).protocols(httpProtocol);
    }
}
