object Main {
	def m(i:Int):String = {
		(i*2).toString
	}
	def main(args: Array[String]): Unit = {
	  // var user=new User("scala",1,"/scala.jepg","0101111","323","lamiu")
	  // val user=new User
	  // user.name="scala"
	  // user save
	  val data=List(1,2,3).map(m)
	  println(data)
	  data.foreach{x => println(x+"_1") 
	  println(x*2)}
	}
	
}