package videogamedb.finalsimulation

import io.gatling.core.Predef._
import io.gatling.http.Predef._

class VideoGameFullTest extends Simulation{
  var httpProtocol = http.baseUrl("https://www.videogamedb.uk:443/api")
    .acceptHeader("application/json")
    .contentTypeHeader("application/json")

  def USERCOUNT = System.getProperty("USERS", "5").toInt
  def RAMPDURATION = System.getProperty("RAMP_DURATION", "10").toInt
  def TESTDURATION: Int = System.getProperty("TEST_DURATION", "30").toInt

  val csvFeeder = csv("data/gameCsvFile.csv").random

  before {
    println(s"Running test with ${USERCOUNT} users")
    println(s"Ramping users over ${RAMPDURATION} seconds")
    println(s"Total test duration: ${TESTDURATION} seconds")
  }

  def getAllVideoGames() = {
    exec(
      http("Get All Video Games")
        .get("/videogame")
        .check(status.is(200))
    )
  }
  def authenticate() = {
    exec(http("Authenticate")
      .post("/authenticate")
      .body(StringBody("{\n  \"password\": \"admin\",\n  \"username\": \"admin\"\n}"))
      .check(jsonPath("$.token").saveAs("jwtToken")))
  }

  def createNewGame() = {
    feed(csvFeeder)
      .exec(http("Create New Game - #{name}")
        .post("/videogame")
        .header("authorization", "Bearer #{jwtToken}")
        .body(ElFileBody("bodies/newGameTemplate.json")).asJson)
  }

  def getSingleGame() = {
    exec(http("Get single game - #{name}")
      .get("/videogame/#{gameId}")
      .check(jsonPath("$.name").is("#{name}")))
  }

  def deleteGame() = {
    exec(http("Delete game - #{name}")
      .delete("/videogame/#{gameId}")
      .header("authorization", "Bearer #{jwtToken}")
      .check(bodyString.is("Video game deleted")))
  }

  val scn = scenario("Video Game DB Final Script")
    .forever {
      exec(getAllVideoGames())
        .pause(2)
        .exec(authenticate())
        .pause(2)
        .exec(createNewGame())
        .pause(2)
        .exec(getSingleGame())
        .pause(2)
        .exec(deleteGame())
    }

  setUp(
    scn.inject(
      nothingFor(5),
      rampUsers(USERCOUNT).during(RAMPDURATION)
    ).protocols(httpProtocol)
  ).maxDuration(TESTDURATION)

  after {
    println("Stress test completed")
  }

}
