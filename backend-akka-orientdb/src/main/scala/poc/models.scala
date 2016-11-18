package poc.models

import gremlin.scala._

@label("foo")
case class Foo(@id id: Option[Int], bar: String, zut: Int)
