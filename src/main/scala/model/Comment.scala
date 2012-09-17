import scala.reflect.BeanProperty
class Comment extends MysqlStore[Comment](){
	dataSourceKey="yy"
	table="mmx_comment"
	override val pk=List[String]("id")
	columns=List("id","blog_id","user_id","content","create_time")
	
	@BeanProperty
	var id:Int=_
	
	@BeanProperty
	var blog_id:Int=_
	
	@BeanProperty
	var user_id:Int=_
	
	@BeanProperty
	var content:String=_
	
	@BeanProperty
	var create_time:java.util.Date=_
	
	override def toString():String={
		"Comment=[id:"+id+",blog_id:"+blog_id+",user_id:"+user_id+",content:"+content+",create_time:"+create_time+"]"
	}
}

object Comment extends MysqlStore[Comment]{
	dataSourceKey="yy"
	table="mmx_comment"
	override val pk=List[String]("id")
	columns=List("id","blog_id","user_id","content","create_time")
}
