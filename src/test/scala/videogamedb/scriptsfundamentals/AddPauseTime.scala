package videogamedb.scriptsfundamentals

import io.gatling.core.Predef._
import io.gatling.http.Predef._

import scala.concurrent.duration.DurationInt

class AddPauseTime extends  Simulation {
  var httpProtocol = http.baseUrl("https://www.videogamedb.uk:443/api")
    .acceptHeader("application/json")

  var scn = scenario("Vide Game DB - 3  calls")
    .exec(http("Get All Video Games")
            .get("/videogame"))
    .pause(5)
    .exec(http("Get Specific Game")
            .get("/videogame/1"))
    .pause(1, 10) // randomly pause between 1 to 10 seconds
    .exec(http("Get All Games - 2nd call")
      .get("/videogame"))
    .pause(3000.milliseconds)

  setUp(
    scn.inject(atOnceUsers(1))
  ).protocols(httpProtocol)


}
