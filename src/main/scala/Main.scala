@main def run(): Unit =
  println("Hello, World!!!".toUpperCase)
  println(9 |+| 20)
  println("Hello " |+| "World")
  println(Option(4) |+| Option(15))
  println(Option(4) |+| None)

trait MySemigroup[A]:
  def combine(x: A, y: A): A

object MySemigroup:
  given MySemigroup[Int] with
    def combine(x: Int, y: Int): Int = x  + y

  given MySemigroup[String] with
    def combine(x: String, y: String): String = x + y

  given [A](using s: MySemigroup[A]): MySemigroup[Option[A]] with
    def combine(x: Option[A], y: Option[A]): Option[A] = (x, y) match
      case (Some(a), Some(b)) => Option(s.combine(a, b))
      case (Some(a), None)    => Option(a)
      case (None, Some(b))    => Option(b)
      case (None, None)       => Option.empty

extension [A](x: A)
  def |+|(y: A)(using s: MySemigroup[A]): A = s.combine(x, y)
