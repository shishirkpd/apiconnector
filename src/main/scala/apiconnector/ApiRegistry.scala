package apiconnector

import akka.actor.ActorSystem
import akka.actor.typed.{ActorRef, Behavior}
import akka.actor.typed.scaladsl.Behaviors
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.concurrent.duration.DurationInt

object ApiRegistry {
  sealed trait Command
  final case class FetchApiResult(replyTo: ActorRef[ApiResponseData]) extends Command
  final case class ApiResponseData(data: String)

  case class RequestData(pat: String, hashId: String)

  final case class ActionPerformed(description: String)

  final case class ApiResponse(maybeResponse: Option[ApiResponseData])

  def apply(): Behavior[Command] = registry

  private def registry: Behavior[Command] =
    Behaviors.receiveMessage {
      case FetchApiResult(replyTo) =>
        fetchResponse.map( data =>
          replyTo ! data.maybeResponse.getOrElse(ApiResponseData(""))
        )
        Behaviors.same
    }

  private def fetchResponse: Future[ApiResponse] = {
   val data = RequestData("pid", "hashId")
    val userId = "rock"
    implicit val system = ActorSystem()
    val request = HttpRequest(
      method = HttpMethods.GET,
      //uri = s"https://${System.getenv().get("env")}.abc.com/",
      uri = s"https://api.instagram.com/v1/users/${userId}",
      entity = HttpEntity(ContentTypes.`application/json`, data.toString)
    )

    Http.apply.singleRequest(request)
      .flatMap(_.entity.toStrict(2.seconds))
      .map(x => ApiResponse(Option(ApiResponseData(x.data.utf8String))))
  }
}
