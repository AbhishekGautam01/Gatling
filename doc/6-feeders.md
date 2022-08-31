# Feeders
1. CSV Feeder
2. Basic Custom Feeder
3. Complex Custom Feeder
4. Templating Feeders

## CSV Feeder
* We can use the csv method from gatling dsl and provide it a path to csv file which we should place in test > data directory. 
* There are multiple strategy to read a csv file like circular, earger, batch and random which can be used based on right use case. 
* The csv file values gets injected in the sessions variables and can be referenced using the column names. 

```scala 
package videogamedb.feeders

import io.gatling.core.Predef._
import io.gatling.core.structure.ChainBuilder
import io.gatling.http.Predef._

class CsvFeeder extends Simulation{
  var httpProtocol = http.baseUrl("https://www.videogamedb.uk:443/api")
    .acceptHeader("application/json")

  val csvFeeder = csv("data/gameCsvFile.csv").circular

  def getSpecificVideoGame() : ChainBuilder = {
    repeat(10){
      feed(csvFeeder)
        .exec(http("Get video game with name - #{gameName}")
          .get("/videogame/#{gameId}")
          .check(jsonPath("$.name").is("#{gameName}"))
          .check(status.is(200)))
        .pause(1)

    }
  }
  val scn = scenario("Csv Feeder")
    .exec(getSpecificVideoGame())

  setUp(
    scn.inject(atOnceUsers(1))
  ).protocols(httpProtocol)
}
```

## Basic Custom Feeder
* 