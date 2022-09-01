package videogamedb.commandline


import io.gatling.core.Predef._
import io.gatling.core.structure.ChainBuilder
import io.gatling.http.Predef._

class RunTimeParameters extends Simulation{
  var httpProtocol = http.baseUrl("https://www.videogamedb.uk:443/api")
    .acceptHeader("application/json")

  def getAllVideoGames(): ChainBuilder = {
    exec(
      http("GetAllVideo Games")
        .get("/videogame")
    ).pause(1)
  }

  val scn = scenario("Run from command line")
    .forever{
      exec(getAllVideoGames())
    }

  setUp(
    scn.inject(
      nothingFor(5),
      rampUsersPerSec(1).to(10).during(20)
    )
  ).protocols(httpProtocol)
    .maxDuration(60)
}
