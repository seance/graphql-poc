package poc.schema

import poc.models._
import sangria.schema._

trait PocSchema {
  
  val foos = Seq(Foo("Hello", 42), Foo("world", 13))
  
  val FooType = ObjectType("Foo", fields[Unit, Foo](
      Field("bar", StringType, resolve = _.value.bar),
      Field("zut", IntType, resolve = _.value.zut)))
      
  val QueryType = ObjectType("Query", fields[Unit, Unit](
      Field("foos", ListType(FooType), resolve = _ => foos)))
      
  val PocSchema = Schema(QueryType)
}
