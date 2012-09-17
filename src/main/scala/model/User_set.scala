import scala.reflect.BeanProperty
class User_set extends MysqlStore[User_set](){
	dataSourceKey="yy"
	table="mmx_user_set"
	override val pk=List[String]("user_id")
	columns=List("user_id","background")
	
	@BeanProperty
	var user_id:Int=_
	
	@BeanProperty
	var background:String=_
	
	override def toString():String={
		"User_set=[user_id:"+user_id+",background:"+background+"]"
	}
}

object User_set extends MysqlStore[User_set]{
	dataSourceKey="yy"
	table="mmx_user_set"
	override val pk=List[String]("user_id")
	columns=List("user_id","background")
}
