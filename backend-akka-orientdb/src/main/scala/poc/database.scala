package poc.database

import poc.models._
import gremlin.scala._
import org.apache.tinkerpop.gremlin.orientdb.OrientGraphFactory

trait PocDatabase {
  
  def graphFactory: OrientGraphFactory
  
  def withGraph[A](f: ScalaGraph => A): A = {
    val graph = graphFactory.getNoTx.asScala
    val result = f(graph)
    graph.close()
    result
  }
}