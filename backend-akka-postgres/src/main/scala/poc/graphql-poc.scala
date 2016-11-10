package poc

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Directives
import akka.stream.ActorMaterializer

object WebServer extends App with Directives {

  implicit val system = ActorSystem("graphql-poc")
  implicit val materializer = ActorMaterializer()
  implicit val executionContext = system.dispatcher

  val route = get {
    complete("Hello world")
  }

  Http().bindAndHandle(route, "localhost", 8080).onSuccess { case s =>
    println(s"Server listening at ${s.localAddress}")
  }
}
