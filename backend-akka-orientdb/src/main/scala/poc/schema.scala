package poc.schema

import poc.models._
import poc.database._
import sangria.schema._
import gremlin.scala._

trait PocSchema {
  
  def foos(g: ScalaGraph) = g.V.hasLabel[Foo].map(_.toCC[Foo]).toList
  
  val FooType = ObjectType("Foo", fields[ScalaGraph, Foo](
      Field("id", IDType, resolve = _.value.id.toString),
      Field("bar", StringType, resolve = _.value.bar),
      Field("zut", IntType, resolve = _.value.zut)))
      
  val QueryType = ObjectType("Query", fields[ScalaGraph, Unit](
      Field("foos", ListType(FooType), resolve = c => foos(c.ctx))))
      
  val PocSchema = Schema(QueryType)
}
