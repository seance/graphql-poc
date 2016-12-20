package poc.queries

import poc.models._
import poc.database._
import scala.concurrent._

trait PocQueries { self: PocDatabase =>
  
  implicit val executionContext: ExecutionContext
  
  import dbConfig.driver.api._
  import Episode._
  import Faction._
  
  def mapOrganics(os: Seq[(
      CharacterDto,
      Option[Weapon],
      Option[CharAssociationDto],
      poc.models.Species,
      Planet)]): Seq[Organic] =
  {
    os.groupBy(_._1.id).values.map(_.toList).collect { case os @ (c, w, a, s, p) :: _ =>
      Organic(c.id, c.name, c.faction.map(Faction(_)), w, os.map(_._3.map(_.id)).flatten, s, p)
    }.toSeq.sortBy(_.id)
  }
    
  def mapDroids(ds: Seq[(
      CharacterDto,
      Option[Weapon],
      Option[CharAssociationDto],
      DroidDto)]): Seq[Droid] =
  {
    ds.groupBy(_._1.id).values.map(_.toList).collect { case ds @ (c, w, a, d) :: _ =>
      Droid(c.id, c.name, c.faction.map(Faction(_)), w, ds.map(_._3.map(_.id)).flatten, DroidFunction(d.primaryFunction))
    }.toSeq.sortBy(_.id)
  }
  
  def mapCharacters(cs: Seq[(
      CharacterDto,
      Option[Weapon],
      Option[CharAssociationDto],
      Option[OrganicDto],
      Option[poc.models.Species],
      Option[Planet],
      Option[DroidDto])]): Seq[Character] =
  {
    cs.groupBy(_._1.id).values.map(_.toList).collect { case cs @ (c, w, a, o, s, p, d) :: _ =>
      o.toRight(d.get).fold(
          d => Droid(c.id, c.name, c.faction.map(Faction(_)), w, cs.map(_._3.map(_.id)).flatten, DroidFunction(d.primaryFunction)),
          o => Organic(c.id, c.name, c.faction.map(Faction(_)), w, cs.map(_._3.map(_.id)).flatten, s.get, p.get))
    }.toSeq.sortBy(_.id)
  }
  
  def queryAllCharacters() = for {
    ((c, w), a) <- Characters
      .joinLeft(Weapons).on(_.favoriteWeaponId === _.id)
      .joinLeft(CharacterAssociations).on(_._1.id === _.targetId)
  }
  yield (c, w, a)
  
  def queryCharacters(characterIds: Seq[Int]) = {
    queryCharactersPolymorphic.filter(_._1.id inSet characterIds)
  }
  
  def queryCharactersPolymorphic() = for {
    (c, w, a) <- queryAllCharacters
    (((co, o), s), p) <- Characters
      .joinLeft(Organics).on(_.id === _.id)
      .joinLeft(Species).on(_._2.map(_.speciesId) === _.id)
      .joinLeft(Planets).on(_._1._2.map(_.homePlanetId) === _.id)
      if c.id === co.id
    (cd, d) <- Characters
      .joinLeft(Droids).on(_.id === _.id)
      if c.id === cd.id
  }
  yield (c, w, a, o, s, p, d)
    
  def queryCharacterPolymorphic(characterId: Int) =
    queryCharactersPolymorphic.filter(_._1.id === characterId)
  
  def queryAssociations(associationIds: Seq[Int]) = for {
    a <- CharacterAssociations if a.id.inSet(associationIds)
  }
  yield a
  
  def queryAssociationsByCharacter(characterIds: Seq[Int]) = for {
    (c, w, a) <- queryAllCharacters if c.id inSet characterIds
  }
  yield a
  
  def queryEpisodes(characterId: Int) = for {
    e <- CharacterEpisodes if e.characterId === characterId
  }
  yield e
  
  def queryOrganics() = for {
    (c, w, a) <- queryAllCharacters
    o <- Organics if c.id === o.id
    s <- Species if o.speciesId === s.id
    p <- Planets if o.homePlanetId === p.id
  }
  yield (c, w, a, s, p)
  
  def queryOrganicsByEpisode(ep: Episode) = for {
    e <- CharacterEpisodes if e.episode === ep.id
    (c, w, a, s, p) <- queryOrganics if c.id === e.characterId
  }
  yield (c, w, a, s, p)
  
  def queryDroids() = for {
    (c, w, a) <- queryAllCharacters
    d <- Droids if c.id === d.id
  }
  yield (c, w, a, d)
  
  def queryDroidsByEpisode(ep: Episode) = for {
    e <- CharacterEpisodes if e.episode === ep.id
    (c, w, a, d) <- queryDroids if c.id === e.characterId
  }
  yield (c, w, a, d)
  
  def querySpeciesByPlanet(planetId: Int) = for {
    o <- Organics if o.homePlanetId === planetId
    s <- Species if s.id === o.speciesId
  }
  yield s
  
  def queryNativesByPlanet(planetId: Int) = for {
    (c, w, a) <- queryAllCharacters
    o <- Organics if c.id === o.id && o.homePlanetId === planetId
    s <- Species if s.id === o.speciesId
    p <- Planets if p.id === o.homePlanetId
  }
  yield (c, w, a, s, p)
  
  def queryPlanetsBySpecies(speciesId: Int) = for {
    o <- Organics if o.speciesId === speciesId
    p <- Planets if o.homePlanetId === p.id
  }
  yield p 
  
  def queryPlanets() = for {
    p <- Planets
  }
  yield p
  
  def queryPlanet(planetId: Int) = for {
    p <- Planets if p.id === planetId
  }
  yield p
  
  def querySpecies() = for {
    s <- Species
  }
  yield s
  
  def queryComments() = for {
    c <- CharacterComments
  }
  yield c
  
  def findAssociations(db: Database)(associationIds: Seq[Int]) = {
    db.run(queryAssociations(associationIds).result).map(_.map { a =>
      Association(a.id, a.targetId, a.sourceId, a.relation)
    })
  }
  
  def findAssociationsByCharacter(db: Database)(characterIds: Seq[Int]) = {
    db.run(queryAssociationsByCharacter(characterIds).result).map(_.collect { case Some(a) =>
      Association(a.id, a.targetId, a.sourceId, a.relation)
    })
  }
  
  def findEpisodes(db: Database)(characterId: Int) = {
    db.run(queryEpisodes(characterId).result).map(_.map(e => Episode(e.episode)))
  }
  
  def findOrganics(db: Database)(ep: Option[Episode]): Future[Seq[Organic]] = {
    db.run(ep.map(queryOrganicsByEpisode).getOrElse(queryOrganics).result).map(mapOrganics)
  }
  
  def findDroids(db: Database)(ep: Option[Episode]): Future[Seq[Droid]] = {
    db.run(ep.map(queryDroidsByEpisode).getOrElse(queryDroids).result).map(mapDroids)
  }
  
  def findAllCharacters(db: Database)(ep: Option[Episode]): Future[Seq[Character]] =
    Future.reduce(findOrganics(db)(ep) :: findDroids(db)(ep) :: Nil)(_ ++ _)
    
  def findCharacters(db: Database)(characterIds: Seq[Int]) = {
    db.run(queryCharacters(characterIds).result).map(mapCharacters)
  }
    
  def findCharacter(db: Database)(characterId: Int) = {
    db.run(queryCharacterPolymorphic(characterId).result).map(mapCharacters(_).headOption)
  }
  
  def findSpeciesByPlanet(db: Database)(planetId: Int) = {
    db.run(querySpeciesByPlanet(planetId).distinct.result)
  }
  
  def findNativesByPlanet(db: Database)(planetId: Int) = {
    db.run(queryNativesByPlanet(planetId).result).map(mapOrganics)
  }
  
  def findPlanetsBySpecies(db: Database)(speciesId: Int) = {
    db.run(queryPlanetsBySpecies(speciesId).distinct.result)
  }
  
  def findPlanets(db: Database) = {
    db.run(queryPlanets.result)
  }
  
  def findPlanet(db: Database)(planetId: Int) = {
    db.run(queryPlanet(planetId).result.headOption)
  } 
  
  def findSpecies(db: Database) = {
    db.run(querySpecies.result)
  }
  
  def findComments(db: Database) = {
    db.run(queryComments.result)
  }
  
  def addComment(db: Database)(commenterId: Int, commenteeId: Int, replyToId: Option[Int], comment: String) = {
    val c = Comment(0, commenterId, commenteeId, replyToId, comment)
    db.run(CharacterComments.returning(CharacterComments) += c)
  }
}
