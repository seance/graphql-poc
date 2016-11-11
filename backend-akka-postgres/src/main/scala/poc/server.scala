package poc

import poc.schema._
import sangria.parser._
import sangria.execution._
import sangria.marshalling.circe._
import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.http.scaladsl.model.ContentTypes._
import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.server.Directives
import akka.stream.ActorMaterializer
import scala.concurrent._

object WebServer extends App with Directives with PocSchema {

  implicit val system = ActorSystem("graphql-poc")
  implicit val materializer = ActorMaterializer()
  implicit val executionContext = system.dispatcher
  
  def executeGraphQL(query: String) = {
    QueryParser.parse(query).map { queryDoc =>
      Executor.execute(PocSchema, queryDoc).map { result =>
        HttpResponse(entity = HttpEntity(`application/json`, result.spaces2))
      } recover { case t: Throwable =>
        HttpResponse(UnprocessableEntity, entity = t.getMessage)
      }
    } getOrElse Future.successful {
      HttpResponse(BadRequest, entity = s"Unparseable query $query")
    }
  }

  val graphql = path("graphql") {
    get {
      parameter("query") { queryStr =>
        complete(executeGraphQL(queryStr))
      }
    }
  }
  
  val bindingFuture = Http().bindAndHandle(graphql, "localhost", 8080)
  
  bindingFuture.onSuccess { case b =>
    println(s"Server listening at ${b.localAddress}")
  }
  
  scala.io.StdIn.readLine()
  
  bindingFuture.flatMap(_.unbind()).onComplete { _ =>
    system.terminate()
  }
}
