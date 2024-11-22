package qajournal;

import io.gatling.javaapi.core.ChainBuilder;
import io.gatling.javaapi.core.FeederBuilder;
import io.gatling.javaapi.core.ScenarioBuilder;
import io.gatling.javaapi.core.Simulation;
import io.gatling.javaapi.http.HttpProtocolBuilder;

import java.time.Duration;

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.core.CoreDsl.constantUsersPerSec;
import static io.gatling.javaapi.http.HttpDsl.http;
import static io.gatling.javaapi.http.HttpDsl.status;

public class NewCars extends Simulation {

    HttpProtocolBuilder httpProtocol = http
            .baseUrl("http://localhost:3000")
            .acceptHeader("text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
            .userAgentHeader("Mozilla/5.0 (Linux; Android 9.0; Galaxy S9 Build/OPR1.170623.011) AppleWebKit/537.36 " +
                    "(KHTML, like Gecko) Chrome/111.0.0.0 Mobile Safari/537.36");


    ChainBuilder getCarByBrand = exec(http("Get car by brand")
            .get("/cars")
            .queryParam("brand", "tata")
            .check(status().is(200)));

    ScenarioBuilder scn = scenario("Get user")
            .exec(getCarByBrand);


    {
        setUp(
                scn.injectOpen(constantUsersPerSec(1).during(Duration.ofSeconds(9))
                ).protocols(httpProtocol)
        );
    }
}
