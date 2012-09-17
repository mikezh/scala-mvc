import scala.reflect.BeanProperty
class Follower extends MysqlStore[Follower](){
	dataSourceKey="yy"
	table="mmx_follower"
	override val pk=List[String]("user_id")
	columns=List("user_id","follower_id","start_time")
	
	@BeanProperty
	var user_id:Int=_
	
	@BeanProperty
	var follower_id:Int=_
	
	@BeanProperty
	var start_time:java.util.Date=_
	
	override def toString():String={
		"Follower=[user_id:"+user_id+",follower_id:"+follower_id+",start_time:"+start_time+"]"
	}
}

object Follower extends MysqlStore[Follower]{
	dataSourceKey="yy"
	table="mmx_follower"
	override val pk=List[String]("user_id")
	columns=List("user_id","follower_id","start_time")
}
