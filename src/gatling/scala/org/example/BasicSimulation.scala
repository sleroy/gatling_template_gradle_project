package org.example

import io.gatling.core.Predef._ // 2
import io.gatling.http.Predef._
import scala.concurrent.duration._
import io.gatling.core.structure.ChainBuilder
import io.gatling.http.request.builder.HttpRequestBuilder


class BasicSimulation extends Simulation { // 3

  val httpProtocol = http
    .baseURL("http://httpbin.org")
    .inferHtmlResources()
    .acceptHeader("text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8") // 6
    .doNotTrackHeader("1")
    .acceptLanguageHeader("en-US,en;q=0.8")
    .acceptEncodingHeader("gzip, deflate, sdch")
    .userAgentHeader("Mozilla/5.0 (Windows NT 5.1; rv:31.0) Gecko/20100101 Firefox/31.0")

  val scn = scenario("BasicSimulation").
    exec(Authentication.basicAuthentication("/basic-auth/user/password", "user", "password")).pause(5)
    .exec(Authentication.digestAuthentication("/digest-auth/auth/user/password", "user", "password")).pause(5)
    // Project to test : https://github.com/bkielczewski/example-spring-boot-security
    .exec(Authentication.csrfToken("http://localhost:8080/login")).pause(1)
    .exec(Authentication.springFormAuthentication("http://localhost:8080/login", "user", "password")).pause(5);

  setUp(
    scn.inject(atOnceUsers(1))
  ).protocols(httpProtocol)
}