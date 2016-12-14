package poc.schema

import poc.models._
import poc.queries._
import poc.database._
import sangria.schema._
import sangria.marshalling.circe._
import io.circe._
import io.circe.generic.semiauto._
import scalaz.syntax.id._
import scala.concurrent._

trait PocSchema { self: PocDatabase with PocQueries =>
  
  import dbConfig.driver.api._
      
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
  
  val PlanetType: ObjectType[Database, Planet] = ObjectType("Planet", () => fields[Database, Planet](
      Field("id", IDType, resolve = _.value.id.toString),
      Field("name", StringType, resolve = _.value.name),
      Field("ecology", StringType, resolve = _.value.ecology),
      Field("species", ListType(SpeciesType), resolve = c => findSpeciesByPlanet(c.ctx)(c.value.id)),
      Field("natives", ListType(OrganicType), resolve = c => findNativesByPlanet(c.ctx)(c.value.id))))
      
  val SpeciesType = ObjectType("Species", () => fields[Database, poc.models.Species](
      Field("id", IDType, resolve = _.value.id.toString),
      Field("name", StringType, resolve = _.value.name),
      Field("planets", ListType(PlanetType), resolve = c => findPlanetsBySpecies(c.ctx)(c.value.id))))
      
  val AssociationType = ObjectType("Association", () => fields[Database, Association](
      Field("character", CharacterType, resolve = c => findCharacter(c.ctx)(c.value.characterId)),
      Field("relation", StringType, resolve = _.value.relation)))
      
  val CharacterType: InterfaceType[Database, Character] = InterfaceType(
      "Character",
      "A character in the Star Wars universe",
      () => fields[Database, Character](
          Field("id", IDType, resolve = _.value.id.toString),
          Field("kind", StringType, resolve = _.value.kind),
          Field("name", StringType, resolve = _.value.name),
          Field("associates", ListType(AssociationType), resolve = c => findAssociations(c.ctx)(c.value.assocIds)),
          Field("appearsIn", ListType(EpisodeType), resolve = c => findEpisodes(c.ctx)(c.value.id)),
          Field("favoriteWeapon", OptionType(WeaponType), resolve = _.value.favoriteWeapon)))
          
  val OrganicType: ObjectType[Database, Organic] = ObjectType(
      "Organic",
      "A non-mechanical character, e.g. human, wookiee...",
      interfaces[Database, Organic](CharacterType),
      () => fields[Database, Organic](
          Field("id", IDType, resolve = _.value.id.toString),
          Field("kind", StringType, resolve = _.value.kind),
          Field("name", StringType, resolve = _.value.name),
          Field("associates", ListType(AssociationType), resolve = c => findAssociations(c.ctx)(c.value.assocIds)),
          Field("appearsIn", ListType(EpisodeType), resolve = c => findEpisodes(c.ctx)(c.value.id)),
          Field("favoriteWeapon", OptionType(WeaponType), resolve = _.value.favoriteWeapon),
          Field("species", SpeciesType, resolve = _.value.species),
          Field("homePlanet", PlanetType, resolve = _.value.homePlanet)))
      
  val DroidType: ObjectType[Database, Droid] = ObjectType(
      "Droid",
      "A mechanical character",
      interfaces[Database, Droid](CharacterType),
      fields[Database, Droid](
          Field("id", IDType, resolve = _.value.id.toString),
          Field("kind", StringType, resolve = _.value.kind),
          Field("name", StringType, resolve = _.value.name),
          Field("associates", ListType(AssociationType), resolve = c => findAssociations(c.ctx)(c.value.assocIds)),
          Field("appearsIn", ListType(EpisodeType), resolve = c => findEpisodes(c.ctx)(c.value.id)),
          Field("favoriteWeapon", OptionType(WeaponType), resolve = _.value.favoriteWeapon),
          Field("primaryFunction", DroidFunctionType, resolve = _.value.primaryFunction)))
          
  val CommentType = ObjectType(
      "Comment",
      "A comment from one character to another",
      fields[Database, Comment](
          Field("id", IDType, resolve = _.value.id.toString),
          Field("commenter", CharacterType, resolve = c => findCharacter(c.ctx)(c.value.commenterId)),
          Field("commentee", CharacterType, resolve = c => findCharacter(c.ctx)(c.value.commenteeId)),
          Field("replyToId", OptionType(IDType), resolve = _.value.replyToId.map(_.toString)),
          Field("comment", StringType, resolve = _.value.comment)))

  case class CommentInput(commenterId: String, commenteeId: String, replyToId: Option[String], comment: String)
  
  // sangria.macros.derive.deriveInputObjectType[CommentInput]
  val CommentInputType = InputObjectType[CommentInput](
      "CommentInput",
      List(
          InputField("commenterId", IDType, "Commenter character id"),
          InputField("commenteeId", IDType, "Commentee character id"),
          InputField("replyToId", OptionInputType(IDType), "Optional parent comment id"),
          InputField("comment", StringType, "Comment content")))
          
  implicit val commentInputDecoder = deriveDecoder[CommentInput]
  
  val EpisodeArg = Argument("episode", OptionInputType(EpisodeType), "Optionally limit query to an episode")
  val CommentInputArg = Argument("commentInput", CommentInputType, "Comment input")

  val MutationType = ObjectType("Mutation", fields[Database, Unit](
      Field("addComment",
          CommentType,
          arguments = CommentInputArg :: Nil,
          resolve = c => (c arg CommentInputArg) |> { ci =>
            addComment(c.ctx)(
                ci.commenterId.toInt,
                ci.commenteeId.toInt,
                ci.replyToId.map(_.toInt),
                ci.comment)
          })))
          
  val QueryType = ObjectType("Query", fields[Database, Unit](
      Field("characters",
          ListType(CharacterType),
          Some("List characters from the Star Wars universe"),
          arguments = EpisodeArg :: Nil,
          resolve = c => findCharacters(c.ctx)(c.arg(EpisodeArg))),
      Field("organics",
          ListType(OrganicType),
          Some("List non-mechanical characters"),
          arguments = EpisodeArg :: Nil,
          resolve = c => findOrganics(c.ctx)(c.arg(EpisodeArg))),
      Field("droids",
          ListType(DroidType),
          Some("List mechanical characters"),
          arguments = EpisodeArg :: Nil,
          resolve = c => findDroids(c.ctx)(c.arg(EpisodeArg))),
      Field("planets",
          ListType(PlanetType),
          Some("List known planets"),
          resolve = c => findPlanets(c.ctx)),
      Field("species",
          ListType(SpeciesType),
          Some("List known species"),
          resolve = c => findSpecies(c.ctx)),
      Field("comments",
          ListType(CommentType),
          Some("Comments between characters"),
          resolve = c => findComments(c.ctx))))
      
  val PocSchema = Schema(QueryType, Some(MutationType))
}
