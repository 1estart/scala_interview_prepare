type Graph = Map[Int, List[Int]]

object GraphAlgorithms:

  // dfs
  def dfs(graph: Graph, start: Int, visited: Set[Int] = Set.empty): Unit =
    if !visited.contains(start) then
      println(s"DFS visit node: $start")
      val newVisited = visited + start
      
      val neighbors = graph.getOrElse(start, Nil)
      neighbors.foreach(neighbor => dfs(graph, neighbor, newVisited))

  // bfs
  @annotation.tailrec
  def bfs(
      graph: Graph, 
      queue: List[Int], 
      visited: Set[Int] 
  ): Unit = queue match
    case Nil => 
      () 
      
    case currentNode :: restOfQueue =>
      if visited.contains(currentNode) then
        bfs(graph, restOfQueue, visited)
      else
        println(s"BFS visit node: $currentNode")
        val newVisited = visited + currentNode
        
        val neighbors = graph.getOrElse(currentNode, Nil)
        val newNeighbors = neighbors.filterNot(newVisited.contains)
        
        bfs(graph, restOfQueue ++ newNeighbors, newVisited)
    

  def printGraph(g: Graph): Unit =
        println("Graph structure (node -> neighbors):")
        g.keys.toList.sorted.foreach { node =>
        val neighbors = g.getOrElse(node, List.empty)
        println(s"  $node -> [${neighbors.mkString(", ")}]")
        }

  