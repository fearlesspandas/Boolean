abstract class BooleanOP[A] {
  val gset:Set[A]
}
class Meet[A](s:A*) extends BooleanOP[A] {


  val gset: Set[A] = Set(s:_*)
  def ++(elemSet:Meet[A]) = new Meet[A](s.++:(elemSet.gset) :_*)
  //def ==[B](m:Meet[B]) = {gset==m.gset}
  override def equals(m:Any): Boolean = {

      //define recursive f to objectively produce
      //equality then call f inside fold left
      def f(x:Any,y:Any): Boolean = {
        println("rec")
        println(x.toString + " " +y.toString)
        (x,y) match {
        case m:(Meet[Any],Meet[Any]) => m._1.gset.zip(m._2.gset).foldLeft(f(m._1.gset.zip(m._2.gset).head._1,m._1.gset.zip(m._2.gset).head._2))( (acc,curr) => (acc && f(curr._1,curr._2)))

        case _ => x == y
      }

    }
    f(this,m)
  }
  def map[B](f: A => B ): Meet[B] = new Meet[B](s.map(f):_*)
  def flatMap[B]( f: A => Meet[B] ): Meet[B] = {
    val meetbset = s.map(f)
    meetbset.foldLeft(meetbset.head)( (acc,curr) => curr ++ acc )
  }
  override def toString():String = gset.toString

}

object Meet {
  def apply[A](s:A*): Meet[A] = new Meet[A](Set(s:_*).toSeq:_*)
}

class Join[A](s:A*) extends BooleanOP[A] {


  val gset: Set[A] = Set(s:_*)
  def ++(elemSet:Join[A]) = new Join[A](s.++:(elemSet.gset) :_*)
  def ==[B](m:Join[B]) = {gset==m.gset}
  def map[B](f: A => B ): Join[B] = new Join[B](s.map(f):_*)
  def flatMap[B]( f: A => Join[B] ): Join[B] = {
    val Joinbset = s.map(f)
    Joinbset.foldLeft(Joinbset.head)( (acc,curr) => curr ++ acc )
  }
  def equals(b:BooleanOP[A]):Boolean = {
    val z = b.gset.zip(gset)
    z.foldLeft(z.head._1.equals(z.head._2))( (acc,curr) => acc && curr._1.equals(curr._2))
  }

}

object Join {
  def apply[A](s:A*): Join[A] = new Join[A](Set(s:_*).toSeq:_*)
}
