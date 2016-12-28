package poc.schema

import poc.models._
import poc.database._
import poc.queries._
import sangria.schema._
import sangria.execution.deferred._
import scala.concurrent._

trait PocSchema { self: PocQueries with PocDatabase =>
  
  val assocsFetcher = Fetcher.caching[Unit, Association, Int](
      (g, ids) => Future.successful(withGraph(findAssociations(_)(ids))))(HasId(_.assocId))
      
  val charsFetcher = Fetcher.caching[Unit, Character, Int](
      (g, ids) => Future.successful(withGraph(findCharacters(_)(ids))))(HasId(_.id))
      
  val resolver = DeferredResolver.fetchers(assocsFetcher, charsFetcher)
  
  val FactionType = EnumType(
      "Faction",
      Some("The Rebel Alliance or the Galactic Empire"),
      List(
          EnumValue("RebelAlliance", value = Faction.RebelAlliance),
          EnumValue("GalacticEmpire", value = Faction.GalacticEmpire)))
  
  val DroidFunctionType = EnumType(
      "DroidFunction",
      Some("Primary function of a droid"),
      List(
          EnumValue("Astromech", value = DroidFunction.Astromech),
          EnumValue("Protocol", value = DroidFunction.Protocol),
          EnumValue("Assassin", value = DroidFunction.Assassin)))
  
  val WeaponType = ObjectType("Weapon", () => fields[Unit, Weapon](
      Field("id", IDType, resolve = _.value.id.toString),
      Field("name", StringType, resolve = _.value.name)
  ))
  
  val SpeciesType: ObjectType[Unit, Species] = ObjectType("Species", () => fields[Unit, Species](
      Field("id", IDType, resolve = _.value.id.toString),
      Field("name", StringType, resolve = _.value.name),
      Field("foundOn", ListType(PlanetType), resolve = c => withGraph(findPlanetsBySpecies(_)(c.value.id))),
      Field("notableMembers", ListType(OrganicType), resolve = c => withGraph(findCharactersBySpecies(_)(c.value.id)))
  ))
  
  val PlanetType: ObjectType[Unit, Planet] = ObjectType("Planet", () => fields[Unit, Planet](
      Field("id", IDType, resolve = _.value.id.toString),
      Field("name", StringType, resolve = _.value.name),
      Field("ecology", StringType, resolve = _.value.ecology),
      Field("species", ListType(SpeciesType), resolve = c => withGraph(findSpeciesByPlanet(_)(c.value.id))),
      Field("natives", ListType(OrganicType), resolve = c => withGraph(findNativesByPlanet(_)(c.value.id)))
  ))
  
  val AssociationType = ObjectType("Association", () => fields[Unit, Association](
      Field("character", CharacterType, resolve = c => charsFetcher.defer(c.value.sourceId)),
      Field("relation", StringType, resolve = _.value.relation)))
  
  def characterCommonFields[C <: Character] = fields[Unit, C](
      Field("id", IDType, resolve = _.value.id.toString),
      Field("kind", StringType, resolve = _.value.kind),
      Field("name", StringType, resolve = _.value.name),
      Field("faction", OptionType(FactionType), resolve = _.value.faction),
      Field("favoriteWeapon", OptionType(WeaponType), resolve = _.value.favoriteWeapon),
      Field("associates", ListType(AssociationType), resolve = c => assocsFetcher.deferSeqOpt(c.value.assocIds)))
      
  val CharacterType: InterfaceType[Unit, Character] = InterfaceType(
      "Character",
      "A character in the Star Wars universe",
      () => characterCommonFields[Character])
          
  val OrganicType: ObjectType[Unit, Organic] = ObjectType(
      "Organic",
      "A non-mechanical character, e.g. human, wookiee...",
      interfaces[Unit, Organic](CharacterType),
      () => characterCommonFields[Organic] ++ fields[Unit, Organic](
          Field("species", SpeciesType, resolve = _.value.species),
          Field("homePlanet", PlanetType, resolve = _.value.homePlanet)))
      
  val DroidType: ObjectType[Unit, Droid] = ObjectType(
      "Droid",
      "A mechanical character",
      interfaces[Unit, Droid](CharacterType),
      () => characterCommonFields[Droid] ++ fields[Unit, Droid](
          Field("primaryFunction", DroidFunctionType, resolve = _.value.primaryFunction)))
          
  val CharIdArg = Argument("characterId", IDType, "Character id")
  val PlanetIdArg = Argument("planetId", IDType, "Planet id")
  val SpeciesIdArg = Argument("speciesId", IDType, "Species id")
  
  val QueryType = ObjectType("Query", fields[Unit, Unit](
      Field("characters", ListType(CharacterType), resolve = c => withGraph(findAllCharacters)),
      Field("planets", ListType(PlanetType), resolve = c => withGraph(findAllPlanets)),
      Field("species", ListType(SpeciesType), resolve = c => withGraph(findAllSpecies)),
      Field("character", OptionType(CharacterType),
          arguments = CharIdArg :: Nil,
          resolve = c => withGraph(findCharacter(_)((c arg CharIdArg).toInt))),
      Field("planet", OptionType(PlanetType),
          arguments = PlanetIdArg :: Nil,
          resolve = c => withGraph(findPlanet(_)((c arg PlanetIdArg).toInt))),
      Field("species1", OptionType(SpeciesType),
          arguments = SpeciesIdArg :: Nil,
          resolve = c => withGraph(findSpecies(_)((c arg SpeciesIdArg).toInt)))
  ))
      
  val PocSchema = Schema(QueryType, additionalTypes = List(OrganicType, DroidType))
}
