import javax.servlet.http.HttpServletRequest


abstract class DBStore[T] extends Page{

	def save():Boolean
	// def save(req:HttpServletRequest,data:Map[String,String]):T
	// def save(data:Map[String,Any]):Int

	// def load(id:Int):T
	// def load(conditions:Map[String,Any]):T
	// def load(sql:String,params:List[Any]):T
	// def load(field:List[String],id:Int):T
	// def load(field:List[String],conditions:Map[String,Any]):T
	// def load(field:List[String],sql:String,params:List[Any]):T

	// def find(sql:String,params:List[Any]):List[T]
	// def find(conditions:Map[String,Any]):List[T]
	// def find(field:List[String],sql:String,params:List[Any]):List[T]
	// def find(field:List[String],conditions:Map[String,Any]):List[T]

	// def update():Int
	// def update(id:Int,params:Map[String,Any]):Int
	// def update(params:Map[String,Any]):Int
	// def update(sql:String,params:List[Any]):Int

	// def delete():Int
	// def delete(id:Int):Int
	// def delete(conditions:Map[String,Any]):Int
	// def delete(sql:String,params:List[Any]):Int	
}