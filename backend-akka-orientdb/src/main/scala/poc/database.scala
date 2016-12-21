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

@label("Weapon")
case class WeaponDto(
    id: Int,
    name: String)

@label("Species")
case class SpeciesDto(
    id: Int,
    name: String)
    
@label("Planet")
case class PlanetDto(
    id: Int,
    name: String,
    ecology: String)

@label("Organic")
case class OrganicDto(
    id: Int,
    name: String,
    faction: Option[Int])

@label("Droid")
case class DroidDto(
    id: Int,
    name: String,
    faction: Option[Int],
    primaryFunction: Int)
    