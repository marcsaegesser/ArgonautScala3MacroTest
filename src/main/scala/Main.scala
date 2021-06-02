import argonaut._
import Argonaut._

case class Person(name: String, age: Int)

given personDecode: DecodeJson[Person] = DecodeJson.derive[Person]

// given personDecode: DecodeJson[Person] =
//   DecodeJson { c =>
//     for {
//       name <- (c --\ "name").as[String]
//       age  <- (c --\ "age").as[Int]
//     } yield Person(name, age)
//   }

val personText = """{"name": "MyName", "age": 42}"""

@main def hello: Unit =
  println(s"Decode person...")
  val personJson = personText.decode[Person]
  println(personJson)

