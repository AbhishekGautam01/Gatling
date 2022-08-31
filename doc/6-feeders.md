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
* We need to generate a Map and pass it to the feeder method , doing so we will be able to generate a basic custom feeder. 
```scala 
package videogamedb.feeders

import io.gatling.core.Predef._
import io.gatling.core.structure.ChainBuilder
import io.gatling.http.Predef._

class BasicCustomFeeder extends Simulation{
  var httpProtocol = http.baseUrl("https://www.videogamedb.uk:443/api")
    .acceptHeader("application/json")

  var idNumbers = (1 to 10).iterator

  val customFeeder = Iterator.continually(Map("gameId" -> idNumbers.next()))

  def getSpecificVideoGame(): ChainBuilder = {
    repeat(10){
      feed(customFeeder)
        .exec(http("Get Video game with id - #{gameId}")
          .get("/videogame/#{gameId}")
          .check(status.is(200)))
        .pause(1)
    }
  }
  val scn = scenario("Basic Custom Feeder")
    .exec(getSpecificVideoGame())

  setUp(
    scn.inject(atOnceUsers(1))
  ).protocols(httpProtocol)
}

```

# Complex Custom Feeder

```scala

var idNumbers = (1 to 10).iterator
  val rnd = new Random()
  val now = LocalDate.now()
  val pattern = DateTimeFormatter.ofPattern("yyyy-MM-dd")

  def randomString(length: Int) = {
    rnd.alphanumeric.filter(_.isLetter).take(length).mkString
  }

  def getRandomDate(startDate: LocalDate, random: Random): String = {
    startDate.minusDays(random.nextInt(30)).format(pattern)
  }

  val customFeeder = Iterator.continually(Map(
    "gameId" -> idNumbers.next(),
    "name" -> ("Game-" + randomString(5)),
    "releaseDate" -> getRandomDate(now, rnd),
    "reviewScore" -> rnd.nextInt(100),
    "category" -> ("Category-" + randomString(6)),
    "rating" -> ("Rating-" + randomString(4))
  ))

  ```