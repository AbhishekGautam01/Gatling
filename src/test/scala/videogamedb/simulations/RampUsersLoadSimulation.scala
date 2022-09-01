package videogamedb.simulations

import io.gatling.core.Predef._
import io.gatling.core.structure.ChainBuilder
import io.gatling.http.Predef._

class RampUsersLoadSimulation extends Simulation {
  var httpProtocol = http.baseUrl("https://www.videogamedb.uk:443/api")
    .acceptHeader("application/json")

  def getAllVideoGames(): ChainBuilder = {
    exec(
      http("GetAllVideo Games")
        .get("/videogame")
    )
  }
  def getSpecificVideoGames(): ChainBuilder = {
    exec(
      http("GetAllVideo Games")
        .get("/videogame/2")
    )
  }

  val scn = scenario("Basic Load Simulation")
    .exec(getAllVideoGames())
    .pause(2)
    .exec(getSpecificVideoGames())
    .pause(5)
    .exec(getAllVideoGames())

  setUp(
    scn.inject(
      nothingFor(5),
      constantUsersPerSec(10).during(10), //Here the time is in seconds
      rampUsersPerSec(1).to(5).during(5)
    ).protocols(httpProtocol)
  )
}
