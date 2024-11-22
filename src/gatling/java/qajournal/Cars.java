package qajournal;

import io.gatling.javaapi.core.ChainBuilder;
import io.gatling.javaapi.core.FeederBuilder;
import io.gatling.javaapi.core.ScenarioBuilder;
import io.gatling.javaapi.core.Simulation;
import io.gatling.javaapi.http.HttpProtocolBuilder;
import java.time.Duration;
import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.http;
import static io.gatling.javaapi.http.HttpDsl.status;

public class Cars extends Simulation {

    FeederBuilder<String> feeder = csv("testdata.csv").circular();

    HttpProtocolBuilder httpProtocol = http
            .baseUrl("http://localhost:3000")
            .acceptHeader("text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
            .userAgentHeader("Mozilla/5.0 (Linux; Android 9.0; Galaxy S9 Build/OPR1.170623.011) AppleWebKit/537.36 " +
                    "(KHTML, like Gecko) Chrome/111.0.0.0 Mobile Safari/537.36");

    ChainBuilder createCar = exec(http("Create car")
            .post("/cars")
            .body(StringBody("{\"brand\":\"#{BRAND}\", \"name\":\"#{NAME}\", \"variant\":\"#{VARIANT}\", " +
                    "\"type\":\"1\", \"id\":\"#{randomInt(5,100)}\", \"commentId\":1}"))
            .check(status().is(201)).check(jmesPath("id").saveAs("carId")));

    ChainBuilder updateCar = exec(http("Update car")
            .put("/cars/#{carId}")
            .body(StringBody("{\"brand\":\"#{BRAND}\", \"name\":\"#{NAME}\", \"variant\":\"#{VARIANT}\", " +
                    "\"type\":\"1\", \"id\":\"#{carId}\", \"commentId\":2}"))
            .check(status().is(200)));

    ChainBuilder getCar = exec(http("Get car")
            .get("/cars/#{carId}")
            .check(status().is(200)));

    ChainBuilder getCarByBrand = exec(http("Get car by brand")
            .get("/cars")
            .queryParam("brand", "tata")
            .check(status().is(200)));

    ChainBuilder deleteCar = exec(http("Delete car")
            .delete("/cars/#{carId}")
            .check(status().is(200)));

    ScenarioBuilder scn = scenario("Get user")
            .feed(feeder)
            .exec(createCar)
            .exec(updateCar)
            .exec(getCar)
            .exec(getCarByBrand)
            .exec(deleteCar);


    {
        setUp(
                scn.injectOpen(constantUsersPerSec(1).during(Duration.ofSeconds(9))
                ).protocols(httpProtocol)
        );
    }

}