## Example Scala 3 Argonaut given macro derivation deadlock

I'm not sure if this is an Argonaut thing, a Scala 3 thing, or me being stupid, but the following code hangs.

```scala
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
```

If I replace the macro-derived decoder with the manually generated version it works fine. So it seems to have something to do with the macro.

A thread dump of a deadlocked app shows the main thread is blocked on a lazy initialization.

```
"run-main-0" #147 prio=5 os_prio=0 tid=0x00007f7bbc03f800 nid=0x1385e in Object.wait() [0x00007f7bdb7fc000]
   java.lang.Thread.State: WAITING (on object monitor)
	at java.lang.Object.wait(Native Method)
	- waiting on <0x00000000f57593d8> (a java.lang.Object)
	at java.lang.Object.wait(Object.java:502)
	at scala.runtime.LazyVals$.wait4Notification(LazyVals.scala:89)
	- locked <0x00000000f57593d8> (a java.lang.Object)
	at Main$package$.personCodec(Main.scala:6)
	at Main$package$.x$1(Main.scala:6)
	at Main$package$.personCodec(Main.scala:6)
	at Main$package$.hello(Main.scala:13)
	at hello.main(Main.scala:10)
	at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method)
	at sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:62)
	at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)
	at java.lang.reflect.Method.invoke(Method.java:498)
	at sbt.Run.invokeMain(Run.scala:133)
	at sbt.Run.execute$1(Run.scala:82)
	at sbt.Run.$anonfun$runWithLoader$5(Run.scala:110)
	at sbt.Run$$Lambda$4715/546573507.apply$mcV$sp(Unknown Source)
	at scala.runtime.java8.JFunction0$mcV$sp.apply(JFunction0$mcV$sp.java:23)
	at sbt.util.InterfaceUtil$$anon$1.get(InterfaceUtil.scala:17)
	at sbt.TrapExit$App.run(TrapExit.scala:258)
	at java.lang.Thread.run(Thread.java:748)
```
