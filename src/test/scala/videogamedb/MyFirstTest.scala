package videogamedb

import io.gatling.core.Predef._
import io.gatling.http.Predef._

class MyFirstTest extends Simulation {

  // 1. Http Configuration

  var httpProtocol = http.baseUrl("https://www.videogamedb.uk:443/api")
    .acceptHeader("application/json")

  // 2. Scenario Definitions

  var scn = scenario("My First Test")
    .exec(http("Get All Games")
    .get("/videogame"))

  // 3. Load Scenarios

  setUp(
    scn.inject(atOnceUsers(1))
  ).protocols(httpProtocol)
}
