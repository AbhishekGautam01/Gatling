package videogamedb.scriptsfundamentals

import io.gatling.core.Predef._
import io.gatling.core.structure.ChainBuilder
import io.gatling.http.Predef._

class LoopHttpCalls extends Simulation{
  val httpProtocol  = http.baseUrl("https://www.videogamedb.uk/api")
    .acceptHeader("application/json")

  def getAllVideoGames(): ChainBuilder = {
    repeat(3 ){
      exec(http("Get All Video Games")
        .get("/videogame")
        .check(status.is(200)))
    }
  }

  def getSpecificGames(): ChainBuilder = {
    repeat(5, "counter"){
      exec(http("Get Specific Game with id: #{counter}")
        .get("/videogame/#{counter}")
        .check(status.in(200 to 210)))
    }
  }

  val scn = scenario("Code Repeat")
    .exec(getAllVideoGames())
    .pause(5)
    .exec(getSpecificGames())
    .pause(5)
    .repeat(2){ // Repeated 2 X 3 times
      getAllVideoGames()
    }

  setUp(
    scn.inject(atOnceUsers(1))
  ).protocols(httpProtocol)
}
