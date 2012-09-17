import scala.reflect.BeanProperty
class Blog extends MysqlStore[Blog](){
	dataSourceKey="yy"
	table="mmx_blog"
	override val pk=List[String]("id")
	columns=List("user_id","create_time","content","pics","mood")
	
	@BeanProperty
	var id:Int=_
	
	@BeanProperty
	var user_id:Int=_
	
	@BeanProperty
	var create_time:java.util.Date=_
	
	@BeanProperty
	var content:String=_
	
	@BeanProperty
	var pics:String=_
	
	@BeanProperty
	var mood:String=_

	var moods:List[String]=_
	
	override def toString():String={
		"Blog=[id:"+id+",user_id:"+user_id+",create_time:"+create_time+",content:"+content+",pics:"+pics+",mood:"+mood+"]"
	}
}

object Blog extends MysqlStore[Blog]{
	dataSourceKey="yy"
	table="mmx_blog"
	override val pk=List[String]("id")
	columns=List("id","user_id","create_time","content","pics","mood_id")
}
