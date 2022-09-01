# Load Simulation Design 

## Basic Load Simulation
* Gatling Documentation
* Here we do nothing for 5 seconds then start with 5 users and ramp up to 10 users during 10 seconds
```scala
package videogamedb.simulations

import io.gatling.core.Predef._
import io.gatling.core.structure.ChainBuilder
import io.gatling.http.Predef._

class BasicLoadSimulation extends Simulation{
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
      atOnceUsers(5),
      rampUsers(10).during(10) //Here the time is in seconds
    ).protocols(httpProtocol)
  )
}

```

## Ramp User Load Simulation
* ConstantUserPerSec
* RampUserPerSec

```scala
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
```

## Fixed User per second
* We can use forever block while building up the scenario. 
```scala
  val scn = scenario("Basic Load Simulation")
    .forever{ // to make it run forever
      exec(getAllVideoGames())
        .pause(2)
        .exec(getSpecificVideoGames())
        .pause(5)
        .exec(getAllVideoGames())
    }

```

* Then we can add max duration to our scenario
```scala
  setUp(
    scn.inject(
      nothingFor(5),
      atOnceUsers(10),
      rampUsers(20).during(30)
    ).protocols(httpProtocol)
  ).maxDuration(60) // after 60 seconds the test should stop
  ```