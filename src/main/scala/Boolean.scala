abstract class BooleanOP[A] {
  val gset:Set[A]
}
class OtherType[A]

case class Meet[A](s:A*) extends BooleanOP[A] {


  val gset: Set[A] = Set(s:_*)
  def ++(elemSet:Meet[A]) = new Meet[A](s.++:(elemSet.gset) :_*)
  //def ==[B](m:Meet[B]) = {gset==m.gset}
  //override
  override def equals(m:Any): Boolean = {
    m match {
      case m: Meet[_] => this.equalTo(m)
      case _ => false
    }
  }
  def ==(m:Meet[_]) = equalTo(m)
  def equalTo(m:Meet[_]): Boolean = {

      //define recursive f to objectively produce
      //equality then call f inside fold left
      def f(x:Meet[_],y:Meet[_]): Boolean = {
        println("rec")
        println(x.toString + " " +y.toString)
      //   (x,y) match {
      //   case (Meet(Meet(_*),_*),Meet(Meet(_*),_*)) => x.gset.zip(y.gset).foldLeft(f(x.gset.zip(y.gset).head._1,x.gset.zip(y.gset).head._2))( (acc,curr) => (acc && f(curr._1,curr._2)))
      //
      //   case _ => x == y
      // }
        println(x.gset.head.toString)
        println(y.gset.head.toString)
        (x,y) match {
        case (Meet(Meet(_*),_*),Meet(Meet(_*),_*)) => x.gset.head.equals(y.gset.head) && f(Meet(x.gset.tail.toSeq:_*),Meet(y.gset.tail.toSeq:_*));
        case (Meet(Join(_*),_*),Meet(Join(_*),_*)) => x.gset.head == y.gset.head && f(Meet(x.gset.tail.toSeq:_*),Meet(y.gset.tail.toSeq:_*));
        case (Meet(_*),Meet(_*)) => x.gset == y.gset
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




case class Join[A](s:A*) extends BooleanOP[A] {


  val gset: Set[A] = Set(s:_*)
  def ++(elemSet:Join[A]) = new Join[A](s.++:(elemSet.gset) :_*)
  //def ==[B](m:Join[B]) = {gset==m.gset}
  //override
  override def equals(m:Any): Boolean = {
    m match {
      case m: Join[_] => this.equalTo(m)
      case _ => false
    }
  }
  def ==(m:Join[_]) = equalTo(m)
  def equalTo(m:Join[_]): Boolean = {

      //define recursive f to objectively produce
      //equality then call f inside fold left
      def f(x:Join[_],y:Join[_]): Boolean = {
        println("rec")
        println(x.toString + " " +y.toString)
      //   (x,y) match {
      //   case (Join(Join(_*),_*),Join(Join(_*),_*)) => x.gset.zip(y.gset).foldLeft(f(x.gset.zip(y.gset).head._1,x.gset.zip(y.gset).head._2))( (acc,curr) => (acc && f(curr._1,curr._2)))
      //
      //   case _ => x == y
      // }
        println(x.gset.head.toString)
        println(y.gset.head.toString)
        (x,y) match {
        case (Join(Join(_*),_*),Join(Join(_*),_*)) => x.gset.head.equals(y.gset.head) && f(Join(x.gset.tail.toSeq:_*),Join(y.gset.tail.toSeq:_*));
        case (Join(Meet(_*),_*),Join(Meet(_*),_*)) => x.gset.head == y.gset.head && f(Join(x.gset.tail.toSeq:_*),Join(y.gset.tail.toSeq:_*));
        case (Join(_*),Join(_*)) => x.gset == y.gset
        case _ => x == y
      }

    }
    f(this,m)
  }
  def map[B](f: A => B ): Join[B] = new Join[B](s.map(f):_*)
  def flatMap[B]( f: A => Join[B] ): Join[B] = {
    val Joinbset = s.map(f)
    Joinbset.foldLeft(Joinbset.head)( (acc,curr) => curr ++ acc )
  }
  override def toString():String = gset.toString

}
