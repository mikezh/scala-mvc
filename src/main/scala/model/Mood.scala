import scala.reflect.BeanProperty
class Mood extends MysqlStore[Mood](){
	dataSourceKey="yy"
	table="mmx_mood"
	override val pk=List[String]("id")
	columns=List("id","content","color","pic")
	
	@BeanProperty
	var id:Int=_
	
	@BeanProperty
	var content:String=_
	
	@BeanProperty
	var color:String=_
	
	@BeanProperty
	var pic:String=_
	
	override def toString():String={
		"Mood=[id:"+id+",content:"+content+",color:"+color+",pic:"+pic+"]"
	}
}

object Mood extends MysqlStore[Mood]{
	dataSourceKey="yy"
	table="mmx_mood"
	override val pk=List[String]("id")
	columns=List("id","content","color","pic")
}
