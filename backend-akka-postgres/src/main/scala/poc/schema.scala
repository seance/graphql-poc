package poc.schema

import poc.models._
import poc.database._
import sangria.schema._

trait PocSchema { self: PocDatabase =>
  
  import dbConfig.driver.api._
  
  def foos = dbConfig.db.run(Foos.result)
  
  val FooType = ObjectType("Foo", fields[Unit, Foo](
      Field("id", IDType, resolve = _.value.id.toString),
      Field("bar", StringType, resolve = _.value.bar),
      Field("zut", IntType, resolve = _.value.zut)))
      
  val QueryType = ObjectType("Query", fields[Unit, Unit](
      Field("foos", ListType(FooType), resolve = _ => foos)))
      
  val PocSchema = Schema(QueryType)
}
