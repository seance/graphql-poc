package poc.queries

import poc.models._
import poc.database._
import scala.concurrent._

trait PocQueries { self: PocDatabase =>
  
  implicit val executionContext: ExecutionContext
  
  import dbConfig.driver.api._
  import Episode._
  
  def mapCharacterPolymorphic(
      c: CharacterDto,
      w: Option[Weapon],
      o: Option[OrganicDto],
      s: Option[poc.models.Species],
      p: Option[Planet],
      d: Option[DroidDto]): Character =
  {
    o.toRight(d.get).fold(
        d => Droid(c.id, c.name, w, DroidFunction(d.primaryFunction)),
        o => Organic(c.id, c.name, w, s.get, p.get))
  }
  
  def queryCharacters() =
    Characters.joinLeft(Weapons).on(_.favoriteWeaponId === _.id)
    
  def queryCharactersPolymorphic() = for {
    (c, w) <- queryCharacters
    (((co, o), s), p) <- Characters
      .joinLeft(Organics).on(_.id === _.id)
      .joinLeft(Species).on(_._2.map(_.speciesId) === _.id)
      .joinLeft(Planets).on(_._1._2.map(_.homePlanetId) === _.id)
      if c.id === co.id
    (cd, d) <- Characters
      .joinLeft(Droids).on(_.id === _.id)
      if c.id === cd.id
  }
  yield (c, w, o, s, p, d)
    
  def queryCharacterPolymorphic(characterId: Int) =
    queryCharactersPolymorphic.filter(_._1.id === characterId)
  
  def queryAssociates(characterId: Int) = for {
    a <- CharacterAssociations if a.targetId === characterId
    c <- queryCharactersPolymorphic if a.sourceId === c._1.id
  }
  yield (c, a.relation)
  
  def queryEpisodes(characterId: Int) = for {
    e <- CharacterEpisodes if e.characterId === characterId
  }
  yield e
  
  def queryOrganics() = for {
    (c, w) <- queryCharacters
    o <- Organics if c.id === o.id
    s <- Species if o.speciesId === s.id
    p <- Planets if o.homePlanetId === p.id
  }
  yield (c, w, s, p)
  
  def queryOrganicsByEpisode(ep: Episode) = for {
    e <- CharacterEpisodes if e.episode === ep.id
    (c, w, s, p) <- queryOrganics if c.id === e.characterId
  }
  yield (c, w, s, p)
  
  def queryDroids() = for {
    (c, w) <- queryCharacters
    d <- Droids if c.id === d.id
  }
  yield (c, w, d)
  
  def queryDroidsByEpisode(ep: Episode) = for {
    e <- CharacterEpisodes if e.episode === ep.id
    (c, w, d) <- queryDroids if c.id === e.characterId
  }
  yield (c, w, d)
  
  def querySpeciesByPlanet(planetId: Int) = for {
    o <- Organics if o.homePlanetId === planetId
    s <- Species if s.id === o.speciesId
  }
  yield s
  
  def queryNativesByPlanet(planetId: Int) = for {
    (c, w) <- queryCharacters
    o <- Organics if c.id === o.id && o.homePlanetId === planetId
    s <- Species if s.id === o.speciesId
    p <- Planets if p.id === o.homePlanetId
  }
  yield (c, w, s, p)
  
  def queryPlanetsBySpecies(speciesId: Int) = for {
    o <- Organics if o.speciesId === speciesId
    p <- Planets if o.homePlanetId === p.id
  }
  yield p 
  
  def queryPlanets() = for {
    p <- Planets
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
  
  def findAssociates(db: Database)(characterId: Int) = {
    db.run(queryAssociates(characterId).result).map(_.map { case (c, rel) =>
      Association(mapCharacterPolymorphic _ tupled c, rel)
    })
  }
  
  def findEpisodes(db: Database)(characterId: Int) = {
    db.run(queryEpisodes(characterId).result).map(_.map(e => Episode(e.episode)))
  }
  
  def findOrganics(db: Database)(ep: Option[Episode]): Future[Seq[Organic]] = {
    db.run(ep.map(queryOrganicsByEpisode).getOrElse(queryOrganics).result).map(_.map { case (c, w, s, p) =>
      Organic(c.id, c.name, w, s, p)
    })
  }
  
  def findDroids(db: Database)(ep: Option[Episode]): Future[Seq[Droid]] = {
    db.run(ep.map(queryDroidsByEpisode).getOrElse(queryDroids).result).map(_.map { case (c, w, d) =>
      Droid(c.id, c.name, w, DroidFunction(d.primaryFunction))
    })
  }
  
  def findCharacters(db: Database)(ep: Option[Episode]): Future[Seq[Character]] =
    Future.reduce(findOrganics(db)(ep) :: findDroids(db)(ep) :: Nil)(_ ++ _)
    
  def findCharacter(db: Database)(characterId: Int) =
    db.run(queryCharacterPolymorphic(characterId).result.head.map(mapCharacterPolymorphic _ tupled))
  
  def findSpeciesByPlanet(db: Database)(planetId: Int) = {
    db.run(querySpeciesByPlanet(planetId).distinct.result)
  }
  
  def findNativesByPlanet(db: Database)(planetId: Int) = {
    db.run(queryNativesByPlanet(planetId).result).map(_.map { case (c, w, s, p) =>
      Organic(c.id, c.name, w, s, p)
    })
  }
  
  def findPlanetsBySpecies(db: Database)(speciesId: Int) = {
    db.run(queryPlanetsBySpecies(speciesId).distinct.result)
  }
  
  def findPlanets(db: Database) = {
    db.run(queryPlanets.result)
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
