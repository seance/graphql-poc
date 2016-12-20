package poc.schema

import poc.models._
import poc.database._
import poc.queries._
import sangria.schema._
import gremlin.scala._

trait PocSchema { self: PocQueries =>
  
  val DroidFunctionType = EnumType(
      "DroidFunction",
      Some("Primary function of a droid"),
      List(
          EnumValue("Astromech", value = DroidFunction.Astromech),
          EnumValue("Protocol", value = DroidFunction.Protocol),
          EnumValue("Assassin", value = DroidFunction.Assassin)))
  
  val WeaponType = ObjectType("Weapon", () => fields[ScalaGraph, Weapon](
      Field("id", IDType, resolve = _.value.id.toString),
      Field("name", StringType, resolve = _.value.name)
  ))
  
  val SpeciesType = ObjectType("Species", () => fields[ScalaGraph, Species](
      Field("id", IDType, resolve = _.value.id.toString),
      Field("name", StringType, resolve = _.value.name)
  ))
  
  val PlanetType = ObjectType("Planet", () => fields[ScalaGraph, Planet](
      Field("id", IDType, resolve = _.value.id.toString),
      Field("name", StringType, resolve = _.value.name),
      Field("ecology", StringType, resolve = _.value.ecology)
  ))
  
  def characterCommonFields[C <: Character] = fields[ScalaGraph, C](
      Field("id", IDType, resolve = _.value.id.toString),
      Field("kind", StringType, resolve = _.value.kind),
      Field("name", StringType, resolve = _.value.name),
      Field("favoriteWeapon", OptionType(WeaponType), resolve = _.value.favoriteWeapon))
      
  val CharacterType: InterfaceType[ScalaGraph, Character] = InterfaceType(
      "Character",
      "A character in the Star Wars universe",
      () => characterCommonFields[Character])
          
  val OrganicType: ObjectType[ScalaGraph, Organic] = ObjectType(
      "Organic",
      "A non-mechanical character, e.g. human, wookiee...",
      interfaces[ScalaGraph, Organic](CharacterType),
      () => characterCommonFields[Organic] ++ fields[ScalaGraph, Organic](
          Field("species", SpeciesType, resolve = _.value.species),
          Field("homePlanet", PlanetType, resolve = _.value.homePlanet)))
      
  val DroidType: ObjectType[ScalaGraph, Droid] = ObjectType(
      "Droid",
      "A mechanical character",
      interfaces[ScalaGraph, Droid](CharacterType),
      () => characterCommonFields[Droid] ++ fields[ScalaGraph, Droid](
          Field("primaryFunction", DroidFunctionType, resolve = _.value.primaryFunction)))
  
  val QueryType = ObjectType("Query", fields[ScalaGraph, Unit](
      Field("species", ListType(SpeciesType), resolve = c => findSpecies(c.ctx)),
      Field("characters", ListType(CharacterType), resolve = c => findCharacters(c.ctx))
  ))
      
  val PocSchema = Schema(QueryType, additionalTypes = List(OrganicType, DroidType))
}
