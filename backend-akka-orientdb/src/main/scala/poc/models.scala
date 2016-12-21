package poc.models

import gremlin.scala._

object DroidFunction extends Enumeration {
  type DroidFunction = Value
  val Astromech = Value(1)
  val Protocol = Value(2)
  val Assassin = Value(3)
}

import DroidFunction._

case class Weapon(
    id: Int,
    name: String)

case class Species(
    id: Int,
    name: String)
    
case class Planet(
    id: Int,
    name: String,
    ecology: String)

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
  override val kind = "organic"
}

case class Droid(
    id: Int,
    name: String,
    favoriteWeapon: Option[Weapon],
    assocIds: Seq[Int],
    primaryFunction: DroidFunction) extends Character {
  override val kind = "droid"
}

case class Association(
    assocId: Int,
    targetId: Int,
    sourceId: Int,
    relation: String)
