trait Printable[A] {
  def format(value: A): String
}

given Printable[Int] with
  def format(value: Int): String = value.toString

given  Printable[String] with
  def format(value: String): String = "\"" + value + "\""


object Printable:
  def format[A](value: A)(using p: Printable[A]): String =
    p.format(value)

  def print[A](value: A)(using p: Printable[A]): Unit =
    println(p.format(value))

extension [A](value: A)
  def print(using p: Printable[A]): Unit =
    Printable.print(value)

final case class Cat(name: String, age: Int, color: String)
object Cat:
  given Printable[Cat] with
    def format(cat: Cat): String =
      s"${cat.name} is a ${cat.age} year-old ${cat.color} cat."