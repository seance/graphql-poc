package poc.database

import poc.models._
import slick.backend.DatabaseConfig
import slick.driver.JdbcProfile

trait PocDatabase {
  
  val dbConfig: DatabaseConfig[JdbcProfile]
  
  import dbConfig.driver.api._
  
  lazy val Weapons = new TableQuery(new Weapons(_))
  lazy val Species = new TableQuery(new Species(_))
  lazy val Planets = new TableQuery(new Planets(_))
  lazy val Characters = new TableQuery(new Characters(_))
  lazy val Organics = new TableQuery(new Organics(_))
  lazy val Droids = new TableQuery(new Droids(_))
  lazy val CharacterAssociations = new TableQuery(new CharacterAssociations(_))
  lazy val CharacterEpisodes = new TableQuery(new CharacterEpisodes(_))
  lazy val CharacterComments = new TableQuery(new CharacterComments(_))
  
  case class CharEpisodeDto(characterId: Int, episode: Int)
  case class CharAssociationDto(id: Int, targetId: Int, sourceId: Int, relation: String)
  case class CharacterDto(id: Int, kind: String, name: String, faction: Option[Int], favoriteWeaponId: Option[Int])
  case class OrganicDto(id: Int, kind: String, speciesId: Int, homePlanetId: Int)
  case class DroidDto(id: Int, kind: String, primaryFunction: Int)
  
  class Weapons(tag: Tag) extends Table[Weapon](tag, "weapons") {
    val id = column[Int]("id", O.PrimaryKey)
    val name = column[String]("name")
    def * = (id, name) <> (Weapon.tupled, Weapon.unapply)
  }
  
  class Species(tag: Tag) extends Table[poc.models.Species](tag, "species") {
    val id = column[Int]("id", O.PrimaryKey)
    val name = column[String]("name")
    def * = (id, name) <> (poc.models.Species.tupled, poc.models.Species.unapply)
  }
  
  class Planets(tag: Tag) extends Table[Planet](tag, "planets") {
    val id = column[Int]("id", O.PrimaryKey)
    val name = column[String]("name")
    val ecology = column[String]("ecology")
    def * = (id, name, ecology) <> (Planet.tupled, Planet.unapply)
  }
  
  class Characters(tag: Tag) extends Table[CharacterDto](tag, "characters") {
    val id = column[Int]("id", O.PrimaryKey)
    val kind = column[String]("kind")
    val name = column[String]("name")
    val faction = column[Option[Int]]("faction")
    val favoriteWeaponId = column[Option[Int]]("favorite_weapon_id")
    def * = (id, kind, name, faction, favoriteWeaponId) <> (CharacterDto.tupled, CharacterDto.unapply)
    
    lazy val favoriteWeaponIdFk = foreignKey("fk_favorite_weapon_id", favoriteWeaponId, Weapons)(
        r => r.id.?, onUpdate=ForeignKeyAction.Restrict, onDelete=ForeignKeyAction.Restrict)
  }
  
  class Organics(tag: Tag) extends Table[OrganicDto](tag, "characters_organics") {
    val id = column[Int]("id", O.PrimaryKey)
    val kind = column[String]("kind")
    val speciesId = column[Int]("species_id")
    val homePlanetId = column[Int]("home_planet_id")
    def * = (id, kind, speciesId, homePlanetId) <> (OrganicDto.tupled, OrganicDto.unapply)
    
    lazy val speciesIdFk = foreignKey("fk_species_id", speciesId, Species)(
        r => r.id, onUpdate=ForeignKeyAction.Restrict, onDelete=ForeignKeyAction.Restrict)
    lazy val homePlanetIdFk = foreignKey("fk_home_planet_id", homePlanetId, Planets)(
        r => r.id, onUpdate=ForeignKeyAction.Restrict, onDelete=ForeignKeyAction.Restrict)
  }
  
  class Droids(tag: Tag) extends Table[DroidDto](tag, "characters_droids") {
    val id = column[Int]("id", O.PrimaryKey)
    val kind = column[String]("kind")
    val primaryFunction = column[Int]("primary_function")
    def * = (id, kind, primaryFunction) <> (DroidDto.tupled, DroidDto.unapply)
  }
  
  class CharacterAssociations(tag: Tag) extends Table[CharAssociationDto](tag, "character_associations") {
    val id = column[Int]("id")
    val targetId = column[Int]("target_id")
    val sourceId = column[Int]("source_id")
    val relation = column[String]("relation")
    def * = (id, targetId, sourceId, relation) <> (CharAssociationDto.tupled, CharAssociationDto.unapply)
    
    lazy val targetIdFk = foreignKey("fk_target_id", targetId, Characters)(
        r => r.id, onUpdate=ForeignKeyAction.Restrict, onDelete=ForeignKeyAction.Restrict)
    lazy val sourceIdFk = foreignKey("fk_source_id", sourceId, Characters)(
        r => r.id, onUpdate=ForeignKeyAction.Restrict, onDelete=ForeignKeyAction.Restrict)
  }
  
  class CharacterEpisodes(tag: Tag) extends Table[CharEpisodeDto](tag, "character_episodes") {
    val characterId = column[Int]("character_id")
    val episode = column[Int]("episode")
    def * = (characterId, episode) <> (CharEpisodeDto.tupled, CharEpisodeDto.unapply)
    
    lazy val characterIdFk = foreignKey("fk_character_id", characterId, Characters)(
        r => r.id, onUpdate=ForeignKeyAction.Restrict, onDelete=ForeignKeyAction.Restrict)
  }
  
  class CharacterComments(tag: Tag) extends Table[Comment](tag, "character_comments") {
    val id = column[Int]("id", O.PrimaryKey, O.AutoInc)
    val commenterId = column[Int]("commenter_id")
    val commenteeId = column[Int]("commentee_id")
    val replyToId = column[Option[Int]]("reply_to_id")
    val comment = column[String]("comment")
    def * = (id, commenterId, commenteeId, replyToId, comment) <> (Comment.tupled, Comment.unapply)
    
    lazy val commenterIdFk = foreignKey("fk_commenter_id", commenterId, Characters)(
        r => r.id, onUpdate=ForeignKeyAction.Restrict, onDelete=ForeignKeyAction.Restrict)
    lazy val commenteeIdFk = foreignKey("fk_commentee_id", commenteeId, Characters)(
        r => r.id, onUpdate=ForeignKeyAction.Restrict, onDelete=ForeignKeyAction.Restrict)
  }
}
