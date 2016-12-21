package poc.queries

import poc.models._
import poc.database._
import gremlin.scala._
import cats.kernel._
import shapeless._
import org.apache.tinkerpop.gremlin.process.traversal.P

trait PocQueries {
  
  val FavoriteWeapon = "FavoriteWeapon"
  val IsSpecies      = "IsSpecies"
  val HomePlanet     = "HomePlanet"
  val Association    = "Association"
  
  val Id       = Key[Int]("id")
  val AssocId  = Key[Int]("assocId")
  val Relation = Key[String]("relation")
  
  /**
   * Clear warning about `withFilter` missing.
   */
  implicit class GremlinScalaWithFilter[E, L <: HList](val g: GremlinScala[E, L]) {
    def withFilter(p: E => Boolean) = g.filter(p)
  }
  
  def queryOrganics(g: ScalaGraph) = {
    import scala.collection.JavaConversions._
    
    val organicL = StepLabel[Vertex]()
    val weaponL = StepLabel[Option[Vertex]]()
    
    for {
      (o, w) <- g.V.hasLabel[OrganicDto].as(organicL)
        .coalesce[Option[Vertex]](
            _.out(FavoriteWeapon).map(Some(_)),
            _.constant(None)).as(weaponL)
        .select((organicL, weaponL))
      a <- o.inE(Association).fold
      s <- o.out(IsSpecies)
      p <- o.out(HomePlanet)
    }
    yield (
        o.toCC[OrganicDto],
        w.map(_.toCC[WeaponDto]),
        a.map(_.property(AssocId).value).toList,
        s.toCC[SpeciesDto],
        p.toCC[PlanetDto]
    )
  }
  
  def queryDroids(g: ScalaGraph) = {
    import scala.collection.JavaConversions._
    
    val droidL = StepLabel[Vertex]()
    val weaponL = StepLabel[Option[Vertex]]()
    
    for {
      (d, w) <- g.V.hasLabel[DroidDto].as(droidL)
        .coalesce[Option[Vertex]](
            _.out(FavoriteWeapon).map(Some(_)),
            _.constant(None)).as(weaponL)
        .select((droidL, weaponL))
      a <- d.inE(Association).fold
    }
    yield (
        d.toCC[DroidDto],
        w.map(_.toCC[WeaponDto]),
        a.map(_.property(AssocId).value).toList
    )
  }
  
  def queryAssociations(g: ScalaGraph)(associationIds: Seq[Int]) = {
    g.E.has(Association, AssocId, P.within(associationIds: _*)).map { a => (
      a.property(AssocId).value,
      a.inVertex.property(Id).value,
      a.outVertex.property(Id).value,
      a.property(Relation).value
    )}
  }
  
  def querySpecies(g: ScalaGraph) =
    g.V.hasLabel[SpeciesDto].map(_.toCC[SpeciesDto])
    
  def queryPlanets(g: ScalaGraph) =
    g.V.hasLabel[PlanetDto].map(_.toCC[PlanetDto])
  
  def findAllOrganics(g: ScalaGraph) = queryOrganics(g).toList.map { case (o, w, a, s, p) =>
    Organic(o.id, o.name, w.map(w => Weapon(w.id, w.name)), a, Species(s.id, s.name), Planet(p.id, p.name, p.ecology))
  }
  
  def findAllDroids(g: ScalaGraph) = queryDroids(g).toList.map { case (d, w, a) =>
    Droid(d.id, d.name, w.map(w => Weapon(w.id, w.name)), a, DroidFunction(d.primaryFunction))
  }
  
  def findOrganics(g: ScalaGraph)(characterIds: Seq[Int]) = {
    queryOrganics(g).filter({ case (o, w, a, s, p) =>
      characterIds.contains(o.id)
    }).toList.map { case (o, w, a, s, p) =>
      Organic(o.id, o.name, w.map(w => Weapon(w.id, w.name)), a, Species(s.id, s.name), Planet(p.id, p.name, p.ecology))
    }
  }
  
  def findDroids(g: ScalaGraph)(characterIds: Seq[Int]) = {
    queryDroids(g).filter({ case (d, w, a) =>
      characterIds.contains(d.id)
    }).toList.map { case (d, w, a) =>
      Droid(d.id, d.name, w.map(w => Weapon(w.id, w.name)), a, DroidFunction(d.primaryFunction))
    }
  }
  
  def findOrganic(g: ScalaGraph)(characterId: Int) = {
    queryOrganics(g).filter({ case (o, w, a, s, p) =>
      o.id == characterId
    }).headOption.map { case (o, w, a, s, p) =>
      Organic(o.id, o.name, w.map(w => Weapon(w.id, w.name)), a, Species(s.id, s.name), Planet(p.id, p.name, p.ecology))
    }
  }
  
  def findDroid(g: ScalaGraph)(characterId: Int) = {
    queryDroids(g).filter({ case (d, w, a) =>
      d.id == characterId
    }).headOption.map { case (d, w, a) =>
      Droid(d.id, d.name, w.map(w => Weapon(w.id, w.name)), a, DroidFunction(d.primaryFunction))
    }
  }
  
  def findAllCharacters(g: ScalaGraph) =
    findAllOrganics(g) ++ findAllDroids(g)
    
  def findCharacters(g: ScalaGraph)(characterIds: Seq[Int]) = {
    findOrganics(g)(characterIds) ++ findDroids(g)(characterIds)
  }
    
  def findCharacter(g: ScalaGraph)(characterId: Int) = {
    findOrganic(g)(characterId) orElse findDroid(g)(characterId)
  }
    
  def findAssociations(g: ScalaGraph)(associationIds: Seq[Int]) = {
    queryAssociations(g)(associationIds).toList.map { case (id, t, s, r) =>
      poc.models.Association(id, t, s, r)
    }
  }
  
  def findPlanets(g: ScalaGraph) = queryPlanets(g).toList.map { p =>
    Planet(p.id, p.name, p.ecology)
  }
  
  def findSpecies(g: ScalaGraph) = querySpecies(g).toList.map { s =>
    Species(s.id, s.name)
  }
}
