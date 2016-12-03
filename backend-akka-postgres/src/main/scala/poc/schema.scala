package poc.schema

import poc.models._
import poc.database._
import sangria.schema._
import scala.concurrent._

trait PocSchema { self: PocDatabase =>
  
  implicit val executionContext: ExecutionContext
  
  import dbConfig.driver.api._
  
  def queryAssociates(characterId: Int) = for {
    a <- CharacterAssociations if a.targetId === characterId
    (c, w) <- Characters
      .joinLeft(Weapons).on(_.favoriteWeaponId === _.id)
      if a.sourceId === c.id
    (((co, o), s), p) <- Characters
      .joinLeft(Organics).on(_.id === _.id)
      .joinLeft(Species).on(_._2.map(_.speciesId) === _.id)
      .joinLeft(Planets).on(_._1._2.map(_.homePlanetId) === _.id)
      if c.id === co.id
    (cd, d) <- Characters
      .joinLeft(Droids).on(_.id === _.id)
      if c.id === cd.id
  }
  yield (c, w, o, s, p, d, a.relation)
  
  def queryEpisodes(characterId: Int) = for {
    e <- CharacterEpisodes if e.characterId === characterId
  }
  yield e
  
  def queryOrganics() = for {
    (c, w) <- Characters.joinLeft(Weapons).on(_.favoriteWeaponId === _.id)
    o <- Organics if c.id === o.id
    s <- Species if o.speciesId === s.id
    p <- Planets if o.homePlanetId === p.id
  }
  yield (c, w, s, p)
  
  def queryDroids() = for {
    (c, w) <- Characters.joinLeft(Weapons).on(_.favoriteWeaponId === _.id)
    d <- Droids if c.id === d.id
  }
  yield (c, w, d)
  
  def findAssociates(db: Database)(characterId: Int) = {
    db.run(queryAssociates(characterId).result).map(_.map { case (c, w, o, s, p, d, rel) =>
      Association(o.toRight(d.get).fold(
          d => Droid(c.id, c.name, w, DroidFunction(d.primaryFunction)),
          o => Organic(c.id, c.name, w, s.get, p.get)),
          rel)
    })
  }
  
  def findEpisodes(db: Database)(characterId: Int) = {
    db.run(queryEpisodes(characterId).result).map(_.map(e => Episode(e.episode)))
  }
  
  def findOrganics(db: Database): Future[Seq[Organic]] = {
    db.run(queryOrganics.result).map(_.map { case (c, w, s, p) =>
      Organic(c.id, c.name, w, s, p)
    })
  }
  
  def findDroids(db: Database): Future[Seq[Droid]] = {
    db.run(queryDroids.result).map(_.map { case (c, w, d) =>
      Droid(c.id, c.name, w, DroidFunction(d.primaryFunction))
    })
  }
  
  def findCharacters(db: Database): Future[Seq[Character]] = for {
    os <- findOrganics(db)
    ds <- findDroids(db)
  }
  yield os ++ ds
      
  val EpisodeType = EnumType(
      "Episode",
      Some("Episode of the original trilogy"),
      List(
          EnumValue("ANewHope", value = Episode.ANewHope),
          EnumValue("TheEmpireStrikesBack", value = Episode.TheEmpireStrikesBack),
          EnumValue("ReturnOfTheJedi", value = Episode.ReturnOfTheJedi)))
  
  val DroidFunctionType = EnumType(
      "DroidFunction",
      Some("Primary function of a droid"),
      List(
          EnumValue("Astromech", value = DroidFunction.Astromech),
          EnumValue("Protocol", value = DroidFunction.Protocol),
          EnumValue("Assassin", value = DroidFunction.Assassin)))
  
  val WeaponType = ObjectType("Weapon", fields[Database, Weapon](
      Field("id", IDType, resolve = _.value.id.toString),
      Field("name", StringType, resolve = _.value.name)))
  
  val PlanetType = ObjectType("Planet", fields[Database, Planet](
      Field("id", IDType, resolve = _.value.id.toString),
      Field("name", StringType, resolve = _.value.name)))
      
  val SpeciesType = ObjectType("Species", fields[Database, poc.models.Species](
      Field("id", IDType, resolve = _.value.id.toString),
      Field("name", StringType, resolve = _.value.name)))
      
  val AssociationType = ObjectType("Association", () => fields[Database, Association](
      Field("character", CharacterType, resolve = _.value.character),
      Field("relation", StringType, resolve = _.value.relation)))
      
  val CharacterType: InterfaceType[Database, Character] = InterfaceType(
      "Character",
      "A character in the Star Wars universe",
      () => fields[Database, Character](
          Field("id", IDType, resolve = _.value.id.toString),
          Field("name", StringType, resolve = _.value.name),
          Field("associates", ListType(AssociationType), resolve = c => findAssociates(c.ctx)(c.value.id)),
          Field("appearsIn", ListType(EpisodeType), resolve = c => findEpisodes(c.ctx)(c.value.id)),
          Field("favoriteWeapon", OptionType(WeaponType), resolve = _.value.favoriteWeapon)))
          
  val OrganicType = ObjectType(
      "Organic",
      "A non-mechanical character, e.g. human, wookiee...",
      interfaces[Database, Organic](CharacterType),
      fields[Database, Organic](
          Field("id", IDType, resolve = _.value.id.toString),
          Field("name", StringType, resolve = _.value.name),
          Field("associates", ListType(AssociationType), resolve = c => findAssociates(c.ctx)(c.value.id)),
          Field("appearsIn", ListType(EpisodeType), resolve = c => findEpisodes(c.ctx)(c.value.id)),
          Field("favoriteWeapon", OptionType(WeaponType), resolve = _.value.favoriteWeapon),
          Field("species", SpeciesType, resolve = _.value.species),
          Field("homePlanet", PlanetType, resolve = _.value.homePlanet)))
      
  val DroidType = ObjectType(
      "Droid",
      "A mechanical character",
      interfaces[Database, Droid](CharacterType),
      fields[Database, Droid](
          Field("id", IDType, resolve = _.value.id.toString),
          Field("name", StringType, resolve = _.value.name),
          Field("associates", ListType(AssociationType), resolve = c => findAssociates(c.ctx)(c.value.id)),
          Field("appearsIn", ListType(EpisodeType), resolve = c => findEpisodes(c.ctx)(c.value.id)),
          Field("favoriteWeapon", OptionType(WeaponType), resolve = _.value.favoriteWeapon),
          Field("primaryFunction", DroidFunctionType, resolve = _.value.primaryFunction)))

  val QueryType = ObjectType("Query", fields[Database, Unit](
      Field("characters", ListType(CharacterType), resolve = c => findCharacters(c.ctx)),
      Field("organics", ListType(OrganicType), resolve = c => findOrganics(c.ctx)),
      Field("droids", ListType(DroidType), resolve = c => findDroids(c.ctx))))
      
  val PocSchema = Schema(QueryType)
}
