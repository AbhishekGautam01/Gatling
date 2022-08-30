# Gatling Script Fundamentals

## Adding Pause time 
* This is important to simulate the user is waiting or typing something meanwhile before making the next call. 
* The result will be stored in target > gatling folder 
```scala
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

```

## Check Response Code 

* It is done using check api and checks can be chained. 


```scala
package videogamedb.scriptsfundamentals

import io.gatling.core.Predef._
import io.gatling.http.Predef._

import scala.concurrent.duration.DurationInt

class CheckResponseCode extends Simulation{

  val httpProtocol = http.baseUrl("https://www.videogamedb.uk:443/api")
    .acceptHeader("application/json")

  val scn = scenario("Video Game DB - 3 Calls")
    .exec(http("Get all video games - 1st call")
      .get("/videogame")
      .check(status.is(404)))
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
    ).protocols(httpProtocols)
}
```

# Checking Response Body

* For this we will use the `json path` to get this you can use any json path evaluator tool online.
* Use jsonpath.com
* Learn json path in depth: https://goessner.net/articles/JsonPath/

```scala 
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
```

## Saving Response body and using it

```scala
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

    .exec(http("Get specific game")
      .get("/videogame/#{gameId}") // using the saved paramter
    .check(jsonPath("$.name").is("Gran Turismo 3")))

  setUp(
    scn.inject(atOnceUsers(1))
  ).protocols(httpProtocol)
}

```

## Debug Session Variables in Gatling
* using session we can print the variables which are stored inside the session 
* also we can use a special variable bodyString to get access to the entire body of the session. 

```scala
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
```

* Another thing we can do is to enable logging by editing `logback-test.xml` file. 
