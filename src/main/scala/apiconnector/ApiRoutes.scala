package apiconnector

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Route

import scala.concurrent.Future
import apiconnector.ApiRegistry._
import akka.actor.typed.ActorRef
import akka.actor.typed.ActorSystem
import akka.actor.typed.scaladsl.AskPattern._
import akka.util.Timeout

class ApiRoutes(apiRegistry: ActorRef[ApiRegistry.Command])(implicit val system: ActorSystem[_]) {

  import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
  import JsonFormats._

  private implicit val timeout = Timeout.create(system.settings.config.getDuration("my-app.routes.ask-timeout"))

  def getApiResponse()(implicit system: ActorSystem[_]): Future[ApiResponseData] =
    apiRegistry.ask(FetchApiResult)

  val apiRoutes: Route =
    pathPrefix("api") {
      concat(
        pathEnd {
          concat(
            get {
              complete(getApiResponse())
            })
        })
    }
}
