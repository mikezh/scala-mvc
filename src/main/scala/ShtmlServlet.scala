import javax.servlet.http._
import scala.io.Source
import scala.actors._
import scala.collection.mutable.Map
import scala.util.matching.Regex
import scala.xml._

/**
shtml模板解析
**/
class ShtmlServlet extends HttpServlet {

    val controllerMap=Map[String,(Object,java.lang.reflect.Method)]()
    val templateMap=Map[String,Elem]()
    val tagMap=Map[String,Elem]()
    val htmlDoctype="""<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
    """
    var htmlPath=""

    override def init(){
      htmlPath=System.getProperty("user.dir")+"/src/main/webapp/html/"
      println("htmlPath="+htmlPath)
      val files=new java.io.File(htmlPath).listFiles
      files.foreach{f=>
        if(f.getName.endsWith(".shtml")){
          val htmlStr=Source.fromFile(f.getPath,"UTF-8").mkString
          val xmlStr=htmlStr.substring(htmlStr.indexOf(">")+2)
          println(xmlStr)
          templateMap("/"+f.getName)=XML.loadString(xmlStr)
        }
      }
      println(templateMap)
      DataSourceFactory.initDB("yy","jdbc:mysql://10.11.200.233:3306/yy","devuser","devuser")
      DataSourceFactory.initDB("test","jdbc:mysql://10.11.140.235:3306/test","root","1234")
    }

    override def destroy(){
      DataSourceFactory closeDB
    }

    override def doGet(req: HttpServletRequest, resp: HttpServletResponse) =
      doPost(req,resp)

    override def doPost(req: HttpServletRequest, resp: HttpServletResponse) ={
      resp.setCharacterEncoding("UTF-8")
      val url=req.getRequestURI()
      println("url==>"+url)
      if(url=="/reload.shtml")
        init()
      else if(templateMap.contains(url)){
        val xml= templateMap(url)
        val newXml=parseXml(xml,req,resp,null)
        println("html=="+htmlDoctype+newXml)
        resp.getWriter.println(htmlDoctype+newXml.mkString)
      }
    }

    def parseXml(node: Node,req: HttpServletRequest, resp: HttpServletResponse, obj: Map[String,Any]): Node = node match {
        case elem @ Elem(_, "s", _, _, child @ _*) => 
          var script=child.mkString
          if(script=="")
            script=elem.attribute("value").mkString
            val result=execute(script,req,resp,obj)
          if(result==null)
            new Text("")
          else
            new Text(result.toString)
        case elem @ Elem(_, "if", _, _, child @ _*) => 
          val script=elem.attribute("test").mkString
            println("child class="+child)
          if(execute(script,req,resp,obj) == true){
            val ifNode=child.filter{_.label!="else"}
            new Group(ifNode.map{parseXml(_,req,resp,obj)})
          }else{
            new Group((elem \ "else").map{parseXml(_,req,resp,obj)})
          }
        case elem @ Elem(_, "for", _, _, child @ _*) => 
          val script=elem.attribute("items").mkString
          val result=execute(script,req,resp,obj)
          var tempKey="$"
          val key=elem.attribute("val").mkString
          if(key!="") tempKey+=key
          println("for key="+tempKey)
          var tempValue=obj
          if(obj==null)
            tempValue=Map()
          val nodeBuffer=(new NodeBuffer /: result.asInstanceOf[List[Any]]){(t,r)=>
            (t /: child){(tt,n)=>
                tempValue += (tempKey->r)
                tt += parseXml(n,req,resp,tempValue)
            }
          }
          new Group(nodeBuffer)
        case elem @ Elem(_, "x", _, _, child @ _*) => 
          val field=elem.attribute("value").mkString
          println("field=="+field)
          if(field!=""){
            val value=getValue(obj,field)
            if(value==null) null else new Text(value.toString)
          }else
            new Text(obj("$").toString)
        case elem @ Elem(_, "include", _, _, child @ _*) => 
          var file=elem.attribute("file").mkString
          var tempValue=obj
          if(file!=""){
            if(file.contains("?")){
              val fAndP=file.split("\\?")
              file=fAndP(0)
              if(obj==null)
                tempValue=Map()
              for(param <- fAndP(1).split("#") if(param!="")){
                val kv=param.split("=")
                tempValue+=("$"+kv(0)->kv(1))
              }
            }
            val subXml= templateMap(file)
            parseXml(subXml,req,resp,tempValue)
          }else elem
        case elem @ Elem(_, _, _, _, child @ _*) => elem.asInstanceOf[Elem].copy(child = child.map{ parseXml(_,req,resp,obj)})
        case other => other
      }

    //反射执行scala
    def execute(str: String,req: HttpServletRequest, resp: HttpServletResponse, obj: Map[String,Any]):Any = {
        println("execute===>"+str.replace(" ",""))
        val key=if(str.contains("(")) str.replace(" ","").substring(0,str.indexOf("(")) else str.replace(" ","")
        val params=getParams(str,req,resp,obj)
        //println("params="+params)
        var result:Object=""
        if(controllerMap.contains(key)){
          val cAndM=controllerMap(key)
          // println("========cache==========")
          if(params!=null){ 
            result=cAndM._2.invoke(cAndM._1,params)
          }else{
            result=cAndM._2.invoke(cAndM._1,req,resp)
         }
        }else{
            val cmArray=key.split("\\.")
            val className=cmArray(0)
            val methodName=cmArray(1)
            println("The first "+className+","+methodName)
            val controller=Class.forName(className)
            if(str.contains("(")){
              val me=controller.getMethod(methodName,classOf[List[String]])
              controllerMap(key)=(controller,me)
              result=me.invoke(controller,params)
            // println(controllerMap)
            }else{
              val me=controller.getMethod(methodName,classOf[HttpServletRequest],
                classOf[HttpServletResponse])
              controllerMap(key)=(controller,me)
              result=me.invoke(controller,req,resp)
            }
        }
        result
    }
    // 获取参数
    def getParams(str: String, req: HttpServletRequest, resp: HttpServletResponse, obj:Map[String,Any]):List[String] = {
        if(str.contains("(")){
          val params=str.substring(str.indexOf("(")+1,str.indexOf(")")).split(",").toList
          println("params="+params)
          params.map{x=>
            //println("x=="+x)
            if(x.startsWith("param."))
              req.getParameter(x.trim.substring(6))
            else if(x.startsWith("$")){
                // println("$")
              if(x.contains(".")){
                if(obj==null) null else getFieldValue(obj(x.substring(0,x.indexOf("."))),x.substring(x.indexOf(".")+1)).toString
              }else
                if(obj==null) null else obj(x).toString
            }else
              x.trim
          }
        }else null
    }

    // 获取<x value=""/>值
    def getValue(obj:Map[String,Any], field:String):Any = {
      println("getValue: obj=="+obj+",field=="+field)
        var kAndField=field.trim
        val flag=kAndField.indexOf(".")
        var key=field
        if(flag>0)
          key=kAndField.substring(0,flag)
        val tempValue=obj(key)
        if(flag<0) tempValue
        else{
          val fields=kAndField.substring(flag+1).split("\\.")
          var r:Any=null
          (tempValue /: fields){(r,f)=>
            getFieldValue(r,f)
          }
        }
    }

    // 获取属性值
    def getFieldValue(obj:Any, field:String):Any = {
      if(obj==null) null
      else{
        println("obj=="+obj)
        println("field=="+field)
        val me=obj.getClass.getMethod(field)
        me.invoke(obj)
      }
    }
}
