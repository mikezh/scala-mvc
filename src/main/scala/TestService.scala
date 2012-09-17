import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

object TestService {
	// def p(req:HttpServletRequest,name:String)=req.getParameter(name)

	// def first(req:HttpServletRequest,resp:HttpServletResponse):String = {
	// 	"frist="+p(req, "ss")
	// }

	def second(str:List[String]):String = {
		str.mkString(",")+".||size="+str.size
	}

	def three(arg: List[String]) = {
		if(arg.length>0 && arg(0)=="1")
			true 
		else
			false
	}
	def four(args: List[String]): List[String] = {
	  args
	}
	def five(args: List[String]): List[User] = {
	  // (scala.collection.mutable.List[User]() /: args){(l,a)=>
	  // 	l+new User(a.toInt,"mike","lamiu")
	  // }
	  // val address=new Address("beijing","lamiu")
	  // List(new User(1,"mike",address),new User(2,"mike2",address),new User(3,"mike3",address))
	  List()
	}

	def six(args: List[String]): Boolean = {
	  if(args.size>0 && args(0).toInt>4)
	  	true
	  else 
	  	false
	}

	def dbSave(arg: List[String]): Boolean = {
		val user=new User
	  	user.nickname="scala"
	  	user.++
	  	true
	}
	def dbSave2(arg: List[String]): Boolean = {
		val u=new User
		var r=false
		try { 
		  	u.startTransaction() + Map("name" -> arg(0),"age" -> arg(1),"code" -> arg(2))
			// val i=1/0
			u + Map("name" -> arg(0),"age" -> arg(1),"code" -> arg(2))
			r=u.commit
		} catch {
		  	case e: Exception => 
		  	  println(e.getMessage)
			  u.rollback()
		}
		r
	}

	def dbSave3(req:HttpServletRequest,resp:HttpServletResponse): Boolean = {
		User + (req,Map("name" -> "n","age" -> "a","code" -> "c"))
	}

	def dbDel(args:List[String]): Boolean = {
		User - args(0).toInt
	}

	def dbDel2(args:List[String]): Boolean = {
		val user=new User
		user.id=25
		user --
	}

	def load(args:List[String]): Mood = {
		// val user=new User
		// user.id=args(0).toInt
		// user ::
		Mood.::(1)
	}

	def load2(args:List[String]): User = {
		2 :: User
	}

	def find(args:List[String]): List[User] = {
		 User.q(Map("name" -> "michael", "age"->30),{(f,v)=>
			f match {
				case "age" => 20+v.asInstanceOf[Int]
				case "name" => "hohoho "+v
				case _ => v
			}
			
		})
	}

	def find2(args:List[String]): List[User] = {
		(new User).pageNum(2).page(args(0).toInt).orderBy("id").desc.q(List("name","age"),Map("name" -> "scala"),{(f,v)=>
			f match {
				case "age" => 2+v.asInstanceOf[Int]
				case "name" => "hello "+v
				// case _ => v
			}
			
		})
	}

	def find3(args:List[String]): List[User] = {
		User pageNum 2 page (args(0).toInt) orderBy "id" desc() q ("where name=?",List("scala"))
	}

	def find4(args:List[String]): List[User] = {
		User q (List("id","name","age","address"), "where name=?",List("scala"))
	}

	def count(args:List[String]): Int = {
		User % ("where name=?",List("scala"))
	}
	def count2(args:List[String]): Int = {
		User % Map("name" -> "michael", "age"->30)
	} 
	def update(args:List[String]): Int = {
		User / ("set name=?, age=? where id=?",List("michael111", 300,35))
	}
	def se(args:List[String]): List[User] = {
		User.q(List("id","name","age","address"),"where name=?",List("scala"),{(f,v)=>
			f match {
				case "age" => 2+v.asInstanceOf[Int]
				case "name" => "hehehe"
				case _ => v
			}
			
		})
	}


	// def main(args: List[String]): Unit = {
 //      println(getValue(new User(1,"a","b"),"name"))
	// }
	
}