import java.lang.reflect.ParameterizedType
import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.sql.ResultSetMetaData
import java.sql.SQLException
import javax.sql.DataSource
import javax.servlet.http.HttpServletRequest
import org.apache.commons.beanutils.BeanUtils
import scala.collection.mutable.MutableList

class MysqlStore[T] extends Page{
	val entityClass=(this.getClass.getGenericSuperclass.asInstanceOf[ParameterizedType])
		.getActualTypeArguments()(0).asInstanceOf[Class[T]]
	var dataSourceKey:String=null
	var table:String=null
	val pk:List[String]=List[String]("id")
	var columns:List[String]=null
	var ds:DataSource=null
	var connection:Connection=null
	private var autoCommit=true
	private var _orderByColumn:String=null
	private var _desc:Boolean=true
	private var _orderBy:Map[String,Boolean]=null

	private def getConnection():Connection = {
		if(connection==null){
			if(ds==null) ds=DataSourceFactory.getDataSource(dataSourceKey)
			connection=ds.getConnection
		}else autoCommit=false 
		connection
	}

	private def getFieldValue(field: String):Any = {
		val f=this.getClass.getMethod(field)
		if(f!=null) f.invoke(this) else null
	}
	private def getValues(field:List[String]):List[Any]=field.map(getFieldValue)
	private def setFieldValue(obj:T, field: String,value: String):Unit = BeanUtils.setProperty(obj, field, value)
	private def setFieldValue(field: String,value: String):Unit = setFieldValue(this.asInstanceOf[T],field,value)
	private def getMapKV(data:Map[String,Any]):(List[String],List[Any])={
		var keys=data.keySet.toList
		val values:List[Any] = keys.map(data(_))
		(keys,values)
	}

	private def insertSql(field:List[String]):String = {
		var sql="INSERT INTO "+table+" ("
		sql=(sql /: field){ _+_+"," }+") VALUES ("
		sql=(sql /: field){ (s,c) => s+"?," }+")"
		sql.replaceAll(""",\)""","""\)""")
	}
	private def deleteSql(whereField:List[String]):String = {
		var sql="DELETE FROM "+table+" WHERE (1=1)"
		(sql /: whereField){ _+" AND "+_+"=?" }
	}
	private def updateSql(field:List[String],whereField:List[String]):String ={
		var sql="UPDATE "+table+" SET "
		sql=(sql /: field){ _+_+"=?," }+" WHERE (1=1)"
		sql=(sql /: whereField){ _+" AND "+_+"=?" }
		sql.replaceAll("""\?, WHERE ""","""\? WHERE """)
	}
	private def selectSql(field:List[String],whereField:List[String]):String ={
		val fields=if(field==null) "*" else field.mkString(",")
		var sql="SELECT "+fields+" FROM "+table + " WHERE (1=1)"
		sql=(sql /: whereField){ _+" AND "+_+"=?"}
		if(field!=null && field(0)=="COUNT(*)")
			sql
		else{
			if(_orderByColumn!=null)
				sql+" ORDER BY "+_orderByColumn+(if(_desc) " DESC" else " ASC")
			else if(_orderBy!=null){ 
				sql+=" ORDER BY "
				(sql /: _orderBy){(s,bundle)=>
					val (c,d)=bundle
					sql+","+c+(if(d) " DESC " else " ASC ")
				}.replaceAll(" ORDER BY ,"," ORDER BY ")
			}
			else sql
		}
	}

	private def executeSql[X](sql:String)(fieldValue:List[Any])(init:Any)(fun:PreparedStatement=>X):X={
		println("sql:"+sql)
		println("params:"+fieldValue)
		connection = getConnection()
		var pstmt:PreparedStatement=null
		var result=init.asInstanceOf[X]
		try {
			pstmt = connection.prepareStatement(sql)
			var x=1
			fieldValue.foreach{v => 
				pstmt.setObject(x, v.asInstanceOf[Object])
				x=x+1
			}
			result=fun(pstmt)
			println("result:"+result)
		}catch {
			case e : Exception => println(e.getMessage)
			// case _ => println
		} finally {
			try {
				if (pstmt != null) pstmt.close
				if (autoCommit) {
					connection.close
					connection = null
				}
			} catch {
				case e : Exception => println(e.getMessage)
				// case _ => println
			}
		}
		result
	}

	private def update(pstmt:PreparedStatement):Int=pstmt.executeUpdate
	private def selectList(pstmt:PreparedStatement):List[T]={
		var dataList=MutableList[T]()
		val rs = pstmt.executeQuery
		if (rs != null) {
			while (rs.next) {
				val rsmd=rs.getMetaData
				val bean = entityClass.newInstance
				for(i <- 1 to rsmd.getColumnCount){
					val value=rs.getString(i)
					// println(i+"===>"+value)
					if(value!=null) setFieldValue(bean,rsmd.getColumnName(i),value)
				}
				// println("bean="+bean)
				dataList += bean
			}
		}
		rs.close
		dataList.toList
	}
	private def selectNumber(pstmt:PreparedStatement):Int={
		val rs = pstmt.executeQuery
		val r=if (rs != null && rs.next) rs.getInt(1) else 0
		rs.close
		r
	}
	private def select(pstmt:PreparedStatement):T={
		val rs = pstmt.executeQuery
		if (rs != null && rs.next) {
			val rsmd=rs.getMetaData
			val bean = entityClass.newInstance
			for(i <- 1 to rsmd.getColumnCount){
				val value=rs.getString(i)
				println(i+"===>"+value)
				if(value!=null) setFieldValue(bean,rsmd.getColumnName(i),value)
			}
			rs.close
			// println("bean="+bean)
			bean
		} else null.asInstanceOf[T]
	}
	private def executeHalfSql(begin:String, sql:String, value:List[Any]):Int={
		if(!sql.toLowerCase.startsWith(begin)) 0
		else begin match {
			case "(" => 
				executeSql("INSERT INTO "+table+" "+sql)(value)(0)(update)
			case "set" => 
				executeSql("UPDATE "+table+" "+sql)(value)(0)(update)
			case "where" => 
				executeSql("DELETE FROM "+table+" "+sql)(value)(0)(update)
			case _ => 0
		}
	}
	private def isExecuteHalfSql(begin:String, sql:String, value:List[Any]):Boolean=if(executeHalfSql(begin,sql,value)>0) true else false
	private def isExecute(sql:String, value:List[Any]):Boolean=if(executeSql(sql)(value)(0)(update)>0) true else false
	
	//insert self
	def ++():Boolean={
		var allColumns=columns
		if(getFieldValue(pk(0))!=null)
			allColumns=pk ::: allColumns
		val sql=insertSql(allColumns)
		isExecute(sql,getValues(allColumns))
	}
	//insert by map
	def +(data:Map[String,Any]):Boolean={
		val datakv=getMapKV(data)
		isExecute(insertSql(datakv._1), datakv._2)
	}
	//insert by sql
	def +(sql:String,value:List[Any]):Boolean=isExecuteHalfSql("(", sql, value)
	//insert by request
	def +(req:HttpServletRequest,data:Map[String,String]):Boolean=this.+((Map[String,Any]() /: data){(m,d)=>
		val (k,v)=d
		m + (k->req.getParameter(v))
	})

	//delete self
	def --():Boolean=isExecute(deleteSql(pk),getValues(pk))
	//delete by pk
	def -(pkValue:Int):Boolean={
		val fieldValue:List[Any] = List(pkValue)
		isExecute(deleteSql(pk),fieldValue)
	}
	//delete by conditions
	def -(conditions:Map[String,Any]):Int={
		val datakv=getMapKV(conditions)
		executeSql(deleteSql(datakv._1))(datakv._2)(0)(update)
	}
	//delete by sql
	def -(whereSql:String,value:List[Any]):Int=executeHalfSql("where", whereSql, value)
	
	//update self
	def /():Boolean=isExecute(updateSql(columns,pk), getValues(columns))
	//update by map
	def /(data:Map[String,Any]):Boolean={
		val datakv=getMapKV(data)
		isExecute(updateSql(datakv._1,pk), datakv._2)
	}
	//update by sql
	def /(sql:String,value:List[Any]):Int=executeHalfSql("set",sql,value)
	//update by request
	def /(req:HttpServletRequest,data:Map[String,String]):Boolean=this./((Map[String,Any]() /: data){(m,d)=>
		val (k,v)=d
		m + (k->req.getParameter(v))
	})

	//update or save
	// def /+()={
	// 	if()
	// }

	//select one record by self
	def ::():T=executeSql(selectSql(null,pk))(getValues(pk))(null)(select)
	//select one record by conditions
	def ::(conditions:Map[String,Any]):T=this.::(null,conditions)
	//select one record by where sql
	def ::(sql:String,params:List[Any]):T=executeSql("SELECT * FROM "+table+" "+sql)(params)(null)(select)
	//select one record's fields by pk
	def ::(field:List[String],id:Int):T=executeSql(selectSql(field,pk))( List(id))(null)(select)
	def ::(id:Int):T=this.::(null,id)
	//select one record's fields by conditions
	def ::(field:List[String],conditions:Map[String,Any]):T={
		val datakv=getMapKV(conditions)
		executeSql(selectSql(field, datakv._1))(datakv._2)(null)(select)
	}
	//select one record's fields by sql
	def ::(field:List[String],sql:String,params:List[Any]):T=executeSql("SELECT "+field.mkString(",")+" FROM "+table+" "+sql)(params)(null)(select)

	//select many records by conditions
	def q(conditions:Map[String,Any]):List[T]=this.q(null,conditions)
	def q(conditions:Map[String,Any],execute:(String,Any)=>Any):List[T]=this.q(null,conditions,execute)
	//select many records's fields by conditions
	def q(field:List[String],conditions:Map[String,Any]):List[T]={
		val datakv=getMapKV(conditions)
		var sql=selectSql(field, datakv._1)
		if(_perPageSize>0){
			this.dataCount=this.%(conditions)
			this.calculate
			sql+=" LIMIT "+ startRecord+ ", "+_perPageSize
		}
		executeSql(sql)(datakv._2)(List[T]())(selectList)
	}
	private def selectExecute(sql:String, params:List[Any], execute:(String,Any)=>Any):List[T]=
		executeSql(sql)(params)(List[T]()){pstmt=>
			var dataList=MutableList[T]()
		val rs = pstmt.executeQuery
		if (rs != null) {
			while (rs.next) {
				val rsmd=rs.getMetaData
				val bean = entityClass.newInstance
				for(i <- 1 to rsmd.getColumnCount){
					val name=rsmd.getColumnName(i)
					val value=execute(name,rs.getObject(i))
					// println(i+"===>"+value)
					if(value!=null) setFieldValue(bean,name,value.toString)
				}
				// println("bean="+bean)
				dataList += bean
			}
		}
		rs.close
		dataList.toList
		}
	def q(field:List[String],conditions:Map[String,Any],execute:(String,Any)=>Any):List[T]={
		val datakv=getMapKV(conditions)
		var sql=selectSql(field, datakv._1)
		this.selectExecute(sql,datakv._2,execute)
	}
	private def setLimit(whereSql:String,params:List[Any]):String = {
		if(_perPageSize>0){
			this.dataCount=this.%(whereSql,params)
			this.calculate
			whereSql+" LIMIT "+ startRecord+ ", "+_perPageSize
		}else whereSql
	}
	
	//select many records by sql
	def q(whereSql:String,params:List[Any]):List[T]=executeSql("SELECT * FROM "+table+" "+setLimit(whereSql,params))(params)(List[T]())(selectList)
	//select many records's fields by sql
	def q(field:List[String],whereSql:String,params:List[Any]):List[T]=executeSql("SELECT "+field.mkString(",")+" FROM "+table+" "+setLimit(whereSql,params))(params)(List[T]())(selectList)
	//select and execute method to change field's value
	def q(field:List[String], whereSql:String, params:List[Any], execute:(String,Any)=>Any):List[T]={
		val sql="SELECT "+field.mkString(",")+" FROM "+table+" "+setLimit(whereSql,params)
		selectExecute(sql,params,execute)
	}
		

	//get count
	def %(sql:String,params:List[Any]):Int = executeSql("SELECT COUNT(*) FROM "+table+" "+sql)(params)(0)(selectNumber)
	def %(conditions:Map[String,Any]):Int = {
		val datakv=getMapKV(conditions)
		executeSql(selectSql(List("COUNT(*)"), datakv._1))( datakv._2)(0)(selectNumber)
	}

	//check exist
	def ?(conditions:Map[String,Any]):Boolean=if(this.%(conditions)>0) true else false
	def ?(sql:String,params:List[Any]):Boolean=if(this.%(sql,params)>0) true else false

	//set page 
	def pageNum(size:Int):T={
		_perPageSize=size
		this.asInstanceOf[T]
	}
	def page(p:Int):T={
		_page=p
		this.asInstanceOf[T]
	}

	//set order
	def orderBy(c:String):T={
		_orderByColumn=c
		this.asInstanceOf[T]
	}
	def orderBy(orderMap:Map[String,Boolean]):T={
		_orderBy=orderMap
		this.asInstanceOf[T]
	}
	def asc():T={
		this._desc=false
		this.asInstanceOf[T]
	}
	def desc():T={
		this._desc=true 
		this.asInstanceOf[T]
	}

	//transaction execute
	def startTransaction():T={
		getConnection().setAutoCommit(false)
		this.asInstanceOf[T]
	}
	def joinTransaction(obj : MysqlStore[Any]):T={
		if(connection == null){
			connection=obj.connection
			this.asInstanceOf[T]
		}else{
			obj.connection=connection
			obj.asInstanceOf[T]
		}
	}
	def commit():Boolean = {
		var r=false
		try {
			if (connection != null) connection.commit
			r=true
		} catch {
			case e:Exception => 
				connection.rollback
				println(e.getMessage)
		} finally {
			if (connection != null)
				try {
					connection.close
				} catch {
					case e:Exception => println(e.getMessage)
				}
		}
		r
	}
	def rollback():Unit= {
		println("==rollback==")
		try {
			if (connection != null)
				connection.rollback
		} catch {
			case e:Exception => println
		} finally {
			if (connection != null)
			try {
				connection.close
			} catch {
				case e:Exception => println(e.getMessage)
			}
		}
	}
}