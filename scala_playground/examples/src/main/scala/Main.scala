@main def run(): Unit =
  println("Hello, scala 3 with cats!!!".toUpperCase)

  // MySemigroup
  println(9 |+| 20)
  println("Hello " |+| "World")
  println(Option(4) |+| Option(15))
  println(Option(4) |+| None)

  //  Printable
  42.print
  "Uraboras".print

  Cat("Garfield", 5, "Orange").print

  // Graphs
  val graph: Graph = Map(
    1 -> List(2, 3),
    2 -> List(1, 4),
    3 -> List(1, 5),
    4 -> List(2),
    5 -> List(3)
  )

  GraphAlgorithms.printGraph(graph)

  GraphAlgorithms.dfs(graph, 1)
  
  GraphAlgorithms.bfs(graph, List(1), Set.empty)
  
  import sttp.client4.quick.*

  println(quickRequest.get(uri"http://httpbin.org/ip").send())


  val env = Map("population" -> 1000000.0)
  val expr = Div(Mul(Add(Num(1), Num(2)), Add(Num(3), Num(4))), Var("population"));
  println(Expr.eval(expr, env))

  val tree = Branch(Leaf("a"), Branch(Leaf("b"), Leaf("c")))
  val stateComputation = State.numberLeaves(tree)

  val (resultTree, finalState) = stateComputation.run(0)
  println(resultTree)
  println(finalState)

