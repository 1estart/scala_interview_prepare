sealed trait Tree[+A]
case class Leaf[A](value: A) extends Tree[A]
case class Branch[A](left: Tree[A], right: Tree[A]) extends Tree[A]


case class State[S, A](run: S => (A, S)) {
    def map[B](f: A => B): State[S, B] = State(s => {
        val (a, newS) = run(s)
        (f(a), newS)
        })

    def flatMap[B](f: A => State[S, B]): State[S, B] = State(s => {
        val (a, newS) = run(s)
        f(a).run(newS)
    })
}


object State {

  def get[S]: State[S, S] = State(s => (s, s))
  
  def modify[S](f: S => S): State[S, Unit] = State(s => ((), f(s)))
  
  def pure[S, A](a: A): State[S, A] = State(s => (a, s))

   def numberLeaves[A](tree: Tree[A]): State[Int, Tree[(A, Int)]] = {
       tree match {
           case Leaf(value) =>
               for {
                   nextNumber <- get[Int]
                   _ <- modify[Int](_ + 1)
               } yield Leaf((value, nextNumber))
           case Branch(left, right) =>
               for {
                   leftTree <- numberLeaves(left)
                   rightTree <- numberLeaves(right)
               } yield Branch(leftTree, rightTree)
       }
   }
}
