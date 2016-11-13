package poc.database

import poc.models._
import slick.driver.PostgresDriver.api._

trait PocDatabase {
  
  val db: Database
  
  def bootstrapDb = db.run {
    val foosSeed = Seq(Foo(0, "Hello", 42), Foo(0, "world", 13))
    
    DBIO.seq(
      Foos.schema.create,
      Foos ++= foosSeed)
  }
  
  lazy val Foos = new TableQuery(tag => new Foos(tag))
  
  class Foos(tag: Tag) extends Table[Foo](tag, "foos") {
    val id = column[Int]("id", O.AutoInc, O.PrimaryKey)
    val bar = column[String]("bar")
    val zut = column[Int]("zut")
    def * = (id, bar, zut) <> (Foo.tupled, Foo.unapply)
  }
}
