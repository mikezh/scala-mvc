import scala.reflect.BeanProperty
class Invite extends MysqlStore[Invite](){
	dataSourceKey="yy"
	table="mmx_invite"
	override val pk=List[String]("id")
	columns=List("id","user_id","email","content","invite_user_id","token","create_time")
	
	@BeanProperty
	var id:Int=_
	
	@BeanProperty
	var user_id:Int=_
	
	@BeanProperty
	var email:String=_
	
	@BeanProperty
	var content:String=_
	
	@BeanProperty
	var invite_user_id:Int=_
	
	@BeanProperty
	var token:String=_
	
	@BeanProperty
	var create_time:java.util.Date=_
	
	override def toString():String={
		"Invite=[id:"+id+",user_id:"+user_id+",email:"+email+",content:"+content+",invite_user_id:"+invite_user_id+",token:"+token+",create_time:"+create_time+"]"
	}
}

object Invite extends MysqlStore[Invite]{
	dataSourceKey="yy"
	table="mmx_invite"
	override val pk=List[String]("id")
	columns=List("id","user_id","email","content","invite_user_id","token","create_time")
}
