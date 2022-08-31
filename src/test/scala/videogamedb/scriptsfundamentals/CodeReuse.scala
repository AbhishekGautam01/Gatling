package videogamedb.scriptsfundamentals

import io.gatling.core.Predef._
import io.gatling.core.structure.ChainBuilder
import io.gatling.http.Predef._

class CodeReuse extends Simulation {
  val httpProtocol  = http.baseUrl("https://www.videogamedb.uk/api")
    .acceptHeader("application/json")

  def getAllVideoGames(): ChainBuilder = {
    exec(http("Get All Video Games")
    .get("/videogame")
    .check(status.is(200)))
  }

  def getSpecificGames(): ChainBuilder = {
    exec(http("Get Specific Game")
      .get("/videogame/1")
      .check(status.in(200 to 210)))
  }

  val scn = scenario("Code Reuse")
    .exec(getAllVideoGames())
    .pause(5)
    .exec(getSpecificGames())
    .pause(5)
    .exec(getAllVideoGames())


  setUp(
    scn.inject(atOnceUsers(1))
  ).protocols(httpProtocol)
}
