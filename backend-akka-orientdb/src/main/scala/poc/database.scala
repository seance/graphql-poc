package poc.database

import poc.models._
import gremlin.scala._
import scala.concurrent.Future

trait PocDatabase {
  
  def graph: ScalaGraph
}