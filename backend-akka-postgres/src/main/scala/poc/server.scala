package poc

import poc.schema._
import poc.queries._
import poc.database._
import io.circe._
import io.circe.parser._
import io.circe.syntax._
import io.circe.optics._
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
import slick.backend.DatabaseConfig
import slick.driver.JdbcProfile
import scala.concurrent.Future
import scala.util._
import scala.io._

object WebServer extends App with Directives with PocSchema with PocQueries with PocDatabase {

  val config = ConfigFactory.load()
  
  implicit val system = ActorSystem("graphql-poc", config.getConfig("actorsystem"))
  implicit val materializer = ActorMaterializer()
  implicit val executionContext = system.dispatcher
  
  val dbConfig = DatabaseConfig.forConfig[JdbcProfile]("database")
  
  val queryT = JsonPath.root.query.string
  val variablesT = JsonPath.root.variables.obj
  val operationNameT = JsonPath.root.operationName.string
  
  def badQuery = Future.successful {
    BadRequest -> Map(
      "errors" -> Seq(Map("message" -> s"Unparseable query"))
    ).asJson
  }
  
  def executeGraphQL(query: String, opName: Option[String], vars: Json): Future[(StatusCode, Json)] = {
    QueryParser.parse(query).map { queryDoc =>
      Executor.execute(PocSchema, queryDoc, dbConfig.db, (), opName, vars).map(OK -> _) recover {
        case e: QueryAnalysisError => UnprocessableEntity -> e.resolveError
        case e: ErrorWithResolver => InternalServerError -> e.resolveError
      }
    } getOrElse badQuery
  }
  
  val root = pathEndOrSingleSlash {
    redirect("graphiql", Found)
  }

  val graphql = path("graphql") {
    get {
      parameters("query", "operationName"?, "variables"?) { (query, opNameOpt, varsOpt) =>
        val result = (for {
        	vars <- varsOpt.flatMap(parse(_).right.toOption) orElse Some(Json.obj())
        }
        yield executeGraphQL(query, opNameOpt, vars)) getOrElse badQuery
        complete(result)
      }
    } ~
    post {
      entity(as[Json]) { b =>
        val result = (for {
          query  <- queryT.getOption(b)
          opName <- Some(operationNameT.getOption(b))
          vars   <- variablesT.getOption(b) orElse Some(JsonObject.empty)
        }
        yield executeGraphQL(query, opName, vars.asJson)) getOrElse badQuery
        complete(result)
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
//    dbConfig.db.shutdown.flatMap(_ => system.terminate())
//  }
}
