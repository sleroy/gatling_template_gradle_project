/**
 *
 */

package org.example

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import scala.concurrent.duration._
import io.gatling.core.structure.ChainBuilder
import io.gatling.http.request.builder.HttpRequestBuilder

object Authentication {

  var csrf_headers = Map(
    "Accept" -> "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8",
    "Upgrade-Insecure-Requests" -> "1"
  );

  def basicAuthentication(url: String, user: String, password: String): ChainBuilder = {
    return exec(http("Auth").get(url).basicAuth(user, password));
  }

  def digestAuthentication(url: String, user: String, password: String): ChainBuilder = {
    return exec(http("Digest Auth").get(url).digestAuth(user, password));
  }

  def springFormAuthentication(loginUrl: String, user: String, password: String): ChainBuilder = {
    val params = Map(
      "j_username" -> user,
      "j_password" -> password
    );

    var headers_http_authenticated = Map(
      "Accept" -> """application/json""",
      "X-CSRF-TOKEN" -> "${csrf_token}"
    );

    return exec(http("FormSpring Auth").post(loginUrl).headers(headers_http_authenticated).formParamMap(params))
  }

  def csrfToken(formURL: String): ChainBuilder = {
    return exec(
      http("Get CSRF")
        .get(formURL)
        .headers(csrf_headers)
        .check(status.is(200))
        .check(
          regex("""<input type="hidden" name="_csrf" value="([^"]*)"/>""").saveAs("csrf_token")
    )
        )
  }

}
