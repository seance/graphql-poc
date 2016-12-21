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
  
  def mapFaction(f: Option[Int]) = f.flatMap(f => if (f == 0) None else Some(Faction(f)))
  
  def mapWeapon(w: Option[WeaponDto]) = w.map(w => Weapon(w.id, w.name))
  
  def mapSpecies(s: SpeciesDto) = Species(s.id, s.name)
  
  def mapPlanet(p: PlanetDto) = Planet(p.id, p.name, p.ecology)
  
  def mapOrganic: PartialFunction[(OrganicDto, Option[WeaponDto], Seq[Int], SpeciesDto, PlanetDto), Organic] = {
    case (o, w, a, s, p) => Organic(o.id, o.name, mapFaction(o.faction), mapWeapon(w), a, mapSpecies(s), mapPlanet(p))
  }
  
  def mapDroid: PartialFunction[(DroidDto, Option[WeaponDto], Seq[Int]), Droid] = {
    case (d, w, a) => Droid(d.id, d.name, mapFaction(d.faction), mapWeapon(w), a, DroidFunction(d.primaryFunction))
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
  
  def queryAllSpecies(g: ScalaGraph) =
    g.V.hasLabel[SpeciesDto].map(_.toCC[SpeciesDto])
    
  def queryAllPlanets(g: ScalaGraph) =
    g.V.hasLabel[PlanetDto].map(_.toCC[PlanetDto])
    
  def querySpecies(g: ScalaGraph)(speciesId: Int) =
    g.V.hasLabel[SpeciesDto].has(Id, speciesId).map(_.toCC[SpeciesDto])
    
  def queryPlanet(g: ScalaGraph)(planetId: Int) =
    g.V.hasLabel[PlanetDto].has(Id, planetId).map(_.toCC[PlanetDto])
    
  def querySpeciesByPlanet(g: ScalaGraph)(planetId: Int) =
    g.V.hasLabel[PlanetDto].has(Id, planetId).in(HomePlanet).out(IsSpecies).dedup().toCC[SpeciesDto]
  
  def queryNativesByPlanet(g: ScalaGraph)(planetId: Int) = {
    import scala.collection.JavaConversions._
    
    val organicL = StepLabel[Vertex]()
    val weaponL = StepLabel[Option[Vertex]]()
  
    for {
      (o, w) <- g.V.hasLabel[PlanetDto].has(Id, planetId).in(HomePlanet).as(organicL)
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
  
  def queryPlanetsBySpecies(g: ScalaGraph)(speciesId: Int) =
    g.V.hasLabel[SpeciesDto].has(Id, speciesId).in(IsSpecies).out(HomePlanet).toCC[PlanetDto]
  
  def queryOrganicsBySpecies(g: ScalaGraph)(speciesId: Int) = {
    import scala.collection.JavaConversions._
    
    val organicL = StepLabel[Vertex]()
    val weaponL = StepLabel[Option[Vertex]]()
  
    for {
      (o, w) <- g.V.hasLabel[SpeciesDto].has(Id, speciesId).in(IsSpecies).as(organicL)
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
  
  def findAllOrganics(g: ScalaGraph) = queryOrganics(g).toList.map(mapOrganic)
  
  def findAllDroids(g: ScalaGraph) = queryDroids(g).toList.map(mapDroid)
  
  def findOrganics(g: ScalaGraph)(characterIds: Seq[Int]) = {
    queryOrganics(g).filter({ case (o, w, a, s, p) =>
      characterIds.contains(o.id)
    }).toList.map(mapOrganic)
  }
  
  def findDroids(g: ScalaGraph)(characterIds: Seq[Int]) = {
    queryDroids(g).filter({ case (d, w, a) =>
      characterIds.contains(d.id)
    }).toList.map(mapDroid)
  }
  
  def findOrganic(g: ScalaGraph)(characterId: Int) = {
    queryOrganics(g).filter({ case (o, w, a, s, p) =>
      o.id == characterId
    }).headOption.map(mapOrganic)
  }
  
  def findDroid(g: ScalaGraph)(characterId: Int) = {
    queryDroids(g).filter({ case (d, w, a) =>
      d.id == characterId
    }).headOption.map(mapDroid)
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
  
  def findAllPlanets(g: ScalaGraph) = queryAllPlanets(g).toList.map { p =>
    Planet(p.id, p.name, p.ecology)
  }
  
  def findPlanet(g: ScalaGraph)(planetId: Int) = {
    queryPlanet(g)(planetId).headOption.map { p =>
      Planet(p.id, p.name, p.ecology)
    }
  }
  
  def findAllSpecies(g: ScalaGraph) = queryAllSpecies(g).toList.map { s =>
    Species(s.id, s.name)
  }
  
  def findSpecies(g: ScalaGraph)(speciesId: Int) = {
    querySpecies(g)(speciesId).headOption.map { s =>
      Species(s.id, s.name)
    }
  }
  
  def findSpeciesByPlanet(g: ScalaGraph)(planetId: Int) = {
    querySpeciesByPlanet(g)(planetId).toList.map { s =>
      Species(s.id, s.name)
    }
  }
  
  def findNativesByPlanet(g: ScalaGraph)(planetId: Int) = {
    queryNativesByPlanet(g)(planetId).toList.map(mapOrganic)
  }
  
  def findPlanetsBySpecies(g: ScalaGraph)(speciesId: Int) = {
    queryPlanetsBySpecies(g)(speciesId).toList.map { p =>
      Planet(p.id, p.name, p.ecology)
    }
  }
  
  def findCharactersBySpecies(g: ScalaGraph)(speciesId: Int) = {
    queryOrganicsBySpecies(g)(speciesId).toList.map(mapOrganic)
  }
}
