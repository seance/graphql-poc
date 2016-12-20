package poc.queries

import poc.models._
import poc.database._
import gremlin.scala._
import cats.kernel._

trait PocQueries {
  
  val FavoriteWeapon = "FavoriteWeapon"
  val IsSpecies      = "IsSpecies"
  val HomePlanet     = "HomePlanet"
  
  def querySpecies(g: ScalaGraph) =
    g.V.hasLabel[SpeciesDto].map(_.toCC[SpeciesDto])
    
  def queryCharacters[CC <: Product](g: ScalaGraph) = {
    val charL = StepLabel[Vertex]()
    val weaponL = StepLabel[Option[Vertex]]()
    
    g.V.hasLabel[CC].as(charL)
      .coalesce[Option[Vertex]](
          _.out(FavoriteWeapon).map(Some(_)),
          _.constant(None)).as(weaponL)
      .select((charL, weaponL))
  }
    
  def queryOrganics(g: ScalaGraph) = {
    val organicL = StepLabel[Vertex]()
    val weaponL = StepLabel[Option[Vertex]]()
    
    for {
      (o, w) <- queryCharacters[OrganicDto](g)
      s <- o.out(IsSpecies)
      p <- o.out(HomePlanet)
    }
    yield (
        o.toCC[OrganicDto],
        w.map(_.toCC[WeaponDto]),
        s.toCC[SpeciesDto],
        p.toCC[PlanetDto]
    )
  }
  
  def queryDroids(g: ScalaGraph) = {
    val droidL = StepLabel[Vertex]()
    val weaponL = StepLabel[Option[Vertex]]()
    
    for {
      (d, w) <- queryCharacters[DroidDto](g)
    }
    yield (
        d.toCC[DroidDto],
        w.map(_.toCC[WeaponDto])
    )
  }
  
  def findSpecies(g: ScalaGraph) = querySpecies(g).toList.map { s =>
    Species(s.id, s.name)
  }
  
  def findOrganics(g: ScalaGraph) = queryOrganics(g).toList.map { case (o, w, s, p) =>
    Organic(o.id, o.name, w.map(w => Weapon(w.id, w.name)), Species(s.id, s.name), Planet(p.id, p.name, p.ecology))
  }
  
  def findDroids(g: ScalaGraph) = queryDroids(g).toList.map { case (d, w) =>
    Droid(d.id, d.name, w.map(w => Weapon(w.id, w.name)), DroidFunction(d.primaryFunction))
  }
  
  def findCharacters(g: ScalaGraph) =
    findOrganics(g) ++ findDroids(g)
}
