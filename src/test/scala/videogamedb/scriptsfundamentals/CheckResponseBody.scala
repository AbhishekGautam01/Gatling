package videogamedb.scriptsfundamentals

import io.gatling.core.Predef._
import io.gatling.http.Predef._

class CheckResponseBodyAndExtract extends Simulation{
  val httpProtocol = http.baseUrl("https://www.videogamedb.uk/api")
    .acceptHeader("application/json")

  val scn = scenario("Video Game DB - 3 Calls")

    .exec(http("Get Specific game")
      .get("/videogame/1")
      .check(status.in(200 to 210), jsonPath("$.name").is("Resident Evil 4")))

  setUp(
    scn.inject(atOnceUsers(1))
  ).protocols(httpProtocol)
}
