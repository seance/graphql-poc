package poc.models

object Episode extends Enumeration {
  type Episode = Value
  val ANewHope = Value(1)
  val TheEmpireStrikesBack = Value(2) 
  val ReturnOfTheJedi = Value(3)
}

object DroidFunction extends Enumeration {
  type DroidFunction = Value
  val Astromech = Value(1)
  val Protocol = Value(2)
  val Assassin = Value(3)
}

import Episode._
import DroidFunction._

case class Weapon(id: Int, name: String)
case class Planet(id: Int, name: String, ecology: String)
case class Species(id: Int, name: String)
case class Association(id: Int, characterId: Int, relation: String)
case class Comment(id: Int, commenterId: Int, commenteeId: Int, replyToId: Option[Int], comment: String)

trait Character {
  val id: Int
  val kind: String
  val name: String
  val favoriteWeapon: Option[Weapon]
  val assocIds: Seq[Int]
}

case class Organic(
    id: Int,
    name: String,
    favoriteWeapon: Option[Weapon],
    assocIds: Seq[Int],
    species: Species,
    homePlanet: Planet) extends Character {
  override val kind: String = "organic"
}
    
case class Droid(
    id: Int,
    name: String,
    favoriteWeapon: Option[Weapon],
    assocIds: Seq[Int],
    primaryFunction: DroidFunction) extends Character {
  override val kind: String = "droid"
}
    
