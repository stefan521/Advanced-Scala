package lectures.part5typesystem

object SelfTypes extends App {

  // requiring a type to be mixed in
  trait Instrumentalist {
    def play(): Unit
  }

  // whoever implements Singer has to implement instrumentalist as well
  trait Singer { self: Instrumentalist => // this construct is called a self type. self is not a keyword
    def sing(): Unit
  }

  class LeadSinger extends Singer with Instrumentalist {
    override def play(): Unit = ???

    override def sing(): Unit = ???
  }

  // this is illegal cause you're not mixing in Instrumentalist
//  class Vocalist extends Singer {
//    override def sing(): Unit = ???
//  }

  val jamesHetfield = new Singer with Instrumentalist {
    override def play(): Unit = ???

    override def sing(): Unit = ???
  }

  class Guitarist extends Instrumentalist {
    override def play(): Unit = ???
  }

  val ericClapton = new Guitarist with Singer {
    override def sing(): Unit = ???
  }

  // vs inheritance
  class A
  class B extends A // B must also be an A

  trait T
  trait S { self: T => } // S requires a T. mixing in S requires mixing in T

  // CAKE PATTERN => "dependency injection"

  // DI
  class Component {
    // API
  }
  class ComponentA extends Component
  class ComponentB extends Component
  class DependentComponent(val Component: Component)


  // CAKE PATTERN
  trait ScalaComponent {
    // API
    def action(x: Int): String
  }

  trait ScalaDependentComponent { self: ScalaComponent =>
    def dependentAction(x: Int): String = action(x) + "this rocks" // compiler knows in advance action will be available
  }

  trait ScalaApplication{ self: ScalaDependentComponent => }

  // layer 1 - small components
  trait Picture extends ScalaComponent
  trait Stats extends ScalaComponent


  // layer 2 - compose
  trait Profile extends ScalaDependentComponent with Picture
  trait Analytics extends ScalaDependentComponent with Stats

  // layer 3 - app
  trait AnalyticsApp extends ScalaApplication with Analytics


  // cyclical dependencies ( illegal inheritance below)
//  class X extends Y
//  class Y extends X

  // kind of possible with self types - whoever implements tX must implement tY. Whoever implements tY must implement tX
  trait tX { self: tY => }
  trait tY { self: tX => }
 }
