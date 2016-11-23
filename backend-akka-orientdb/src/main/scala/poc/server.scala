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
import com.typesafe.config.ConfigFactory
import org.apache.tinkerpop.gremlin.orientdb.OrientGraphFactory
import gremlin.scala._
import scala.concurrent.Future
import scala.util._
import scala.io._

object WebServer extends App with Directives with PocSchema with PocDatabase {

  val config = ConfigFactory.load()
  
  implicit val system = ActorSystem("graphql-poc", config.getConfig("actorsystem"))
  implicit val materializer = ActorMaterializer()
  implicit val executionContext = system.dispatcher
  
  val graphFactory = new OrientGraphFactory(
      config.getString("database.url"),
      config.getString("database.user"),
      config.getString("database.password"))
    .setupPool(
      config.getInt("database.pool.maxPartitionSize"),
      config.getInt("database.pool.maxSize"))
      
  def badQuery = Future.successful {
    BadRequest -> Map(
      "errors" -> Seq(Map("message" -> s"Unparseable query"))
    ).asJson
  }
  
  def executeGraphQL(query: String): Future[(StatusCode, Json)] = {
    QueryParser.parse(query).map { queryDoc =>
      withGraph { graph =>
        Executor.execute(PocSchema, queryDoc, graph).map(OK -> _) recover {
          case e: QueryAnalysisError => UnprocessableEntity -> e.resolveError
          case e: ErrorWithResolver => InternalServerError -> e.resolveError
        }
      }
    } getOrElse badQuery
  }
  
  val root = pathEndOrSingleSlash {
    redirect("graphiql", Found)
  }

  val graphql = path("graphql") {
    get {
      parameter("query") { query =>
        complete(executeGraphQL(query))
      }
    } ~
    post {
      entity(as[Json]) { b =>
        complete(b.cursor.get[String]("query").fold(
            _ => badQuery,
            executeGraphQL))
      }
    }
  }
  
  val graphiql = (path("graphiql") & get) {
    getFromResource("assets/graphiql.html")
  }
  
  val assets = (pathPrefix("assets") & get) {
    getFromResourceDirectory("assets")
  }
  
  val routes = root ~ graphql ~ graphiql ~ assets
  
  val bindingFuture = Http().bindAndHandle(routes,
      config.getString("server.interface"),
      config.getInt("server.port"))
  
  bindingFuture.onComplete {
    case Success(b) => println(s"Server listening at ${b.localAddress}")
    case Failure(t) => println(s"Server startup failed: $t")
  }
  
//  StdIn.readLine()
//  
//  bindingFuture.flatMap(_.unbind()).onComplete { _ =>
//    graphFactory.close()
//    system.terminate()
//  }
}
