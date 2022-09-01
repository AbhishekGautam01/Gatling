package videogamedb.simulations

import io.gatling.core.Predef._
import io.gatling.core.structure.ChainBuilder
import io.gatling.http.Predef._

class FixedUserLoadSimulation extends Simulation {
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
    .forever{ // to make it run forever
      exec(getAllVideoGames())
        .pause(2)
        .exec(getSpecificVideoGames())
        .pause(5)
        .exec(getAllVideoGames())
    }


  setUp(
    scn.inject(
      nothingFor(5),
      atOnceUsers(10),
      rampUsers(20).during(30)
    ).protocols(httpProtocol)
  ).maxDuration(60) // after 60 seconds the test should stop
}
