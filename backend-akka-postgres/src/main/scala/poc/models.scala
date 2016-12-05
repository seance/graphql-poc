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
case class Association(character: Character, relation: String)

trait Character {
  val id: Int
  val kind: String
  val name: String
  val favoriteWeapon: Option[Weapon]
}

case class Organic(
    id: Int,
    name: String,
    favoriteWeapon: Option[Weapon],
    species: Species,
    homePlanet: Planet,
    kind: String = "organic") extends Character
    
case class Droid(
    id: Int,
    name: String,
    favoriteWeapon: Option[Weapon],
    primaryFunction: DroidFunction,
    kind: String = "droid") extends Character
    
