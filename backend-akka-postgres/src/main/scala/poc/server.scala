package poc

import poc.schema._
import poc.database._
import sangria.parser._
import sangria.execution._
import sangria.marshalling.circe._
import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.http.scaladsl.model.ContentTypes._
import akka.http.scaladsl.model.StatusCodes.{Success => _, _}
import akka.http.scaladsl.server.Directives
import akka.stream.ActorMaterializer
import slick.driver.H2Driver.api._
import scala.concurrent._
import scala.util._
import scala.io._

object WebServer extends App with Directives with PocSchema with PocDatabase {

  implicit val system = ActorSystem("graphql-poc")
  implicit val materializer = ActorMaterializer()
  implicit val executionContext = system.dispatcher
  
  val db = Database.forURL(
      url = "jdbc:h2:mem:pocdb;MODE=PostgreSQL;DB_CLOSE_DELAY=-1",
      driver = "org.h2.Driver") 
  
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
      parameter("query") { query =>
        complete(executeGraphQL(query))
      }
    }
  }
  
  val bindingFuture = bootstrapDb.flatMap { _ =>
    Http().bindAndHandle(graphql, "localhost", 8080)
  }
  
  bindingFuture.onComplete {
    case Success(b) => println(s"Server listening at ${b.localAddress}")
    case Failure(t) => println(s"Server startup failed: $t")
  }
  
  StdIn.readLine()
  
  bindingFuture.flatMap(_.unbind()).onComplete { _ =>
    db.shutdown.flatMap(_ => system.terminate())
  }
}
