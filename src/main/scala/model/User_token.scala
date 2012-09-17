import scala.reflect.BeanProperty
class User_token extends MysqlStore[User_token](){
	dataSourceKey="yy"
	table="mmx_user_token"
	override val pk=List[String]("id")
	columns=List("id","user_id","token","create_time","user_time")
	
	@BeanProperty
	var id:Int=_
	
	@BeanProperty
	var user_id:Int=_
	
	@BeanProperty
	var token:String=_
	
	@BeanProperty
	var create_time:java.util.Date=_
	
	@BeanProperty
	var user_time:Int=_
	
	override def toString():String={
		"User_token=[id:"+id+",user_id:"+user_id+",token:"+token+",create_time:"+create_time+",user_time:"+user_time+"]"
	}
}

object User_token extends MysqlStore[User_token]{
	dataSourceKey="yy"
	table="mmx_user_token"
	override val pk=List[String]("id")
	columns=List("id","user_id","token","create_time","user_time")
}
