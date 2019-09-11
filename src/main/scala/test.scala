// val meet = A => (B => M[] )
//
// def flatMap[B](f: (A) => U[B]): U[B]
//
// def unit: A → F[A]
// def map: F[A] → (A → B) → F[B]
// def flatten: F[F[A]] → F[A]
//
//
// Boolean Algebras are ordered structures
// Therefore our base structure for representing them can b a sequence.
// We can also consider a nested structure to represent ordering, where a<b if a in b
// (here we meen for 'a in b to be determined transitively. i.e. if a in x in b, then a in b )
//
//
// Meet and Join structures should have no ordering associated with them
// or at the very least, all possible orderings made equivalent. (While this
// is not necessary in implementation, we can think of this as meet and Join
// as being an extension of Set without any imposed ordering).
//
//
// we define meet and join then as two monads, with unit based on identity laws of each operation.
//
// We then have the following signatures in our functions over which to build our algebra.
//
// def unit: A → Meet[A]
// def map: Meet[A] → (A → B) → Meet[B]
// def flatten: Meet[Meet[A]] → Meet[A]
//
// def unit: A → Join[A]
// def map: Join[A] → (A → B) → Join[B]
// def flatten: Join[Join[A]] → Join[A]
//
//
// alternatively we can work with flatmaps only (rather than splitting into map and flatten operations)
//
// def flatMap[B](f: (A) => Meet[B]): Meet[B]
// def flatMap[B](f: (A) => Join[B]): Join[B]
//
// Note that implementations of these functions must satisfy the following axioms
// (with respect to themselves and not eachother) to produce a proper monad.
//
//
// left-identity law
//   unit(x).flatMap(f) == f(x)
//
// right-identity law
//   m.flatMap(unit) == m
//
// associativity law
//   m.flatMap(f).flatMap(g) == m.flatMap(x ⇒ f(x).flatMap(g))
//
//
//
// The case classes constructed below will provide us with associativity
// commutativity, and equality/identity of the meet and join operations.
// However, distributivity among the operations, as well as negation
// still needs to be defined to complete our boolean algebra structure.
//
//
// abstract class Monoid[A] {
//   def flatMap[B]( f: A => Monoid[B] ): Monoid[B]
//   def map[B](f: A => B ): Monoid[B]
// }
//
// These two classes should be generalized to a commutative operation class
// If we have two commutative complementary operations we can generalize how they distribute
// as well
//
//
// abstract class BooleanOP[A] {
//   val gset:Set[A]
// }
// class Meet[A](s:A*) extends BooleanOP[A] {
//
//
//   val gset: Set[A] = Set(s:_*)
//   def ++(elemSet:Meet[A]) = new Meet[A](s.++:(elemSet.gset) :_*)
//   //def ==[B](m:Meet[B]) = {gset==m.gset}
//   override def equals(m:Any): Boolean = {
//
//       //define recursive f to objectively produce
//       //equality then call f inside fold left
//       def f(x:Any,y:Any): Boolean = {
//         println("rec")
//         (x,y) match {
//         case m:(Meet[Any],Meet[Any]) => m._1.gset.zip(m._2.gset).foldLeft(f(m._1.gset.zip(m._2.gset).head._1,m._1.gset.zip(m._2.gset).head._2))( (acc,curr) => (acc && f(curr._1,curr._2)))
//         case _ => x == y
//       }
//     }
//     f(this,m)
//   }
//   def map[B](f: A => B ): Meet[B] = new Meet[B](s.map(f):_*)
//   def flatMap[B]( f: A => Meet[B] ): Meet[B] = {
//     val meetbset = s.map(f)
//     meetbset.foldLeft(meetbset.head)( (acc,curr) => curr ++ acc )
//   }
//
// }
//
// object Meet {
//   def apply[A](s:A*): Meet[A] = new Meet[A](Set(s:_*).toSeq:_*)
// }
//
// class Join[A](s:A*) extends BooleanOP[A] {
//
//
//   val gset: Set[A] = Set(s:_*)
//   def ++(elemSet:Join[A]) = new Join[A](s.++:(elemSet.gset) :_*)
//   def ==[B](m:Join[B]) = {gset==m.gset}
//   def map[B](f: A => B ): Join[B] = new Join[B](s.map(f):_*)
//   def flatMap[B]( f: A => Join[B] ): Join[B] = {
//     val Joinbset = s.map(f)
//     Joinbset.foldLeft(Joinbset.head)( (acc,curr) => curr ++ acc )
//   }
//   def equals(b:BooleanOP[A]):Boolean = {
//     val z = b.gset.zip(gset)
//     z.foldLeft(z.head._1.equals(z.head._2))( (acc,curr) => acc && curr._1.equals(curr._2))
//   }
//
// }
//
// object Join {
//   def apply[A](s:A*): Join[A] = new Join[A](Set(s:_*).toSeq:_*)
// }
//
// case class Join[A](s:Set[A]) extends Set[A] {
//   def iterator: Iterator[A] = s.iterator
//   def contains(key: A): Boolean = s.contains(key)
//   def +(elem: A): Join[A] = new Join[A](s.+(elem))
//   def -(elem: A): Join[A] = new Join[A](s.-(elem))
//   override def equals(j: Any): Boolean = {
//     if(j.isInstanceOf[Join[A]]){
//       s == j
//     }else{
//       false
//     }
//   }
//   def flatMap(f: (A => Join[A]) ): Join[A] = {
//     Join(s.flatMap(f))
//   }
// }
//
// the next step is to consider how to fundamentally equate
// Join( (Meet((a,b)) , c) ) where we start with inconsistent typing
// with
// Meet( ( Join( (a,c) ) , Join( (b,c) ) ) ) where we have consistent typing
//
// Join( Set(Meet((a,b)) , c) ) => Join( (Meet((a,b)) , Meet(Set(c))) ) => Meet( Set(Join( ( Set(a) ++: Set(c)) ), Join(Set(b) ++: Set(c) ) )  )
// Start with inconsistent type =>    Convert to Common type            =>  Factor by common type and distribute
//
// Join(Set(Meet(a),Meet(b))) => Meet(Join(Meet(a).merge(b)))
//
// We currently get the following values
//
// scala> testmeet
// res74: Meet[Join[Int]] = Set(Set(1, 3), Set(2, 3))
//
// scala> testjoin
// res75: Join[Any] = Set(Set(1, 2), 3)
//
//
// want to go from a,b to Meet(a:_*,b:_*)
// BooleanAlgebra(Axioms: Set[A]) extends Set {
//   def meet(a:A,b:A) = {
//     case
//     Set(a,b).flatMap(())
//   }
// }
