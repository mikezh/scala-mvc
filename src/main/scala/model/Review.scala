import scala.reflect.BeanProperty
class Review extends MysqlStore[Review](){
	dataSourceKey="yy"
	table="mmx_review"
	override val pk=List[String]("id")
	columns=List("id","comment_id","content","create_time","user_id")
	
	@BeanProperty
	var id:Int=_
	
	@BeanProperty
	var comment_id:Int=_
	
	@BeanProperty
	var content:String=_
	
	@BeanProperty
	var create_time:java.util.Date=_
	
	@BeanProperty
	var user_id:Int=_
	
	override def toString():String={
		"Review=[id:"+id+",comment_id:"+comment_id+",content:"+content+",create_time:"+create_time+",user_id:"+user_id+"]"
	}
}

object Review extends MysqlStore[Review]{
	dataSourceKey="yy"
	table="mmx_review"
	override val pk=List[String]("id")
	columns=List("id","comment_id","content","create_time","user_id")
}
