package videogamedb.scriptsfundamentals

import io.gatling.core.Predef._
import io.gatling.http.Predef._

class CheckResponseBodyAndExtract extends Simulation{
  val httpProtocol = http.baseUrl("https://www.videogamedb.uk/api")
    .acceptHeader("application/json")

  val scn = scenario("Check with json path and extract")

    .exec(http("Get Specific game")
      .get("/videogame/1")
      .check(status.in(200 to 210), jsonPath("$.name").is("Resident Evil 4")))

    .exec(http("Get All Video Games")
    .get("/videogame")
    .check(jsonPath("$[1].id").saveAs("gameId")))
    .exec{
      session => println(session); session //returning the session
    }
    .exec(http("Get specific game")
      .get("/videogame/#{gameId}") // using the saved paramter
    .check(jsonPath("$.name").is("Gran Turismo 3"))
    .check(bodyString.saveAs("responseBody")))
    .exec{session => println(session("responseBody").as[String]); session}// bodyString is special gatling variable

  setUp(
    scn.inject(atOnceUsers(1))
  ).protocols(httpProtocol)
}
