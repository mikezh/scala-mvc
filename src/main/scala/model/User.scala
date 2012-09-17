import scala.reflect.BeanProperty
class User extends MysqlStore[User](){
	dataSourceKey="yy"
	table="mmx_user"
	override val pk=List[String]("id")
	columns=List("id","email","nickname","pass","sex","create_time")
	
	@BeanProperty
	var id:Int=_
	
	@BeanProperty
	var email:String=_
	
	@BeanProperty
	var nickname:String=_
	
	@BeanProperty
	var pass:String=_
	
	@BeanProperty
	var sex:Int=_
	
	@BeanProperty
	var create_time:java.util.Date=_
	
	override def toString():String={
		"User=[id:"+id+",email:"+email+",nickname:"+nickname+",pass:"+pass+",sex:"+sex+",create_time:"+create_time+"]"
	}
}

object User extends MysqlStore[User]{
	dataSourceKey="yy"
	table="mmx_user"
	override val pk=List[String]("id")
	columns=List("id","email","nickname","pass","sex","create_time")
}
