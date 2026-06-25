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
