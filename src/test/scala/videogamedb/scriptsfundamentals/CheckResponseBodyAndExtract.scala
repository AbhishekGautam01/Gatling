package videogamedb.scriptsfundamentals

import io.gatling.core.Predef._
import io.gatling.http.Predef._

import scala.concurrent.duration.DurationInt

class CheckResponseCode extends Simulation {

  val httpProtocol = http.baseUrl("https://www.videogamedb.uk/api")
    .acceptHeader("application/json")

  val scn = scenario("Video Game DB - 3 Calls")
    .exec(http("Get all video games - 1st call")
      .get("/videogame")
      .check(status.is(200)))
    .pause(5)

    .exec(http("Get Specific game")
      .get("/videogame/1")
      .check(status.in(200 to 210)))
    .pause(1, 10)

    .exec(http("Get all video games - 2nd call")
      .get("/videogame")
      .check(status.not(404), status.not(500)))
    .pause(3000.milliseconds)

    setUp(
      scn.inject(atOnceUsers(1))
    ).protocols(httpProtocol)
}
