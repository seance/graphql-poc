package poc

import poc.schema._
import poc.database._
import io.circe.Json
import io.circe.syntax._
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
import de.heikoseeberger.akkahttpcirce.CirceSupport._
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
  
  def executeGraphQL(query: String): Future[(StatusCode, Json)] = {
    QueryParser.parse(query).map { queryDoc =>
      Executor.execute(PocSchema, queryDoc).map(OK -> _) recover {
        case e: QueryAnalysisError => UnprocessableEntity -> e.resolveError
        case e: ErrorWithResolver => InternalServerError -> e.resolveError
      }
    } getOrElse Future.successful {
      BadRequest -> Map(
        "errors" -> Seq(Map("message" -> s"Unparseable query $query"))
      ).asJson
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
