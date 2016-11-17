package poc.database

import poc.models._
import slick.backend.DatabaseConfig
import slick.driver.JdbcProfile

trait PocDatabase {
  
  val dbConfig: DatabaseConfig[JdbcProfile]
  
  import dbConfig.driver.api._
  
  lazy val Foos = new TableQuery(tag => new Foos(tag))
  
  class Foos(tag: Tag) extends Table[Foo](tag, "foos") {
    val id = column[Int]("id", O.AutoInc, O.PrimaryKey)
    val bar = column[String]("bar")
    val zut = column[Int]("zut")
    def * = (id, bar, zut) <> (Foo.tupled, Foo.unapply)
  }
}
