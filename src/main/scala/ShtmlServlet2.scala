import javax.servlet.http._
import scala.io.Source
import scala.actors._
import scala.collection.mutable.Map
import scala.util.matching.Regex

/**
shtml模板解析
**/
class ShtmlServletT extends HttpServlet {

    val controllerMap=Map[String,(Object,java.lang.reflect.Method)]()
    val templateMap=Map[String,String]()
    val scriptR="""\$\{\s*(\s*[a-z|A-Z]+\.[a-z|A-Z]+(\(.*\))?)\s*\}""".r
    val ifScriptR="""\$\{\s*if\(\s*(\s*[a-z|A-Z]+\.[a-z|A-Z]+(\(.*\))?)\s*\)\s*\}(?!\$\{).*\$\{end\}""".r
    val ifElseScriptR="""\$\{\s*if\(\s*(\s*[a-z|A-Z]+\.[a-z|A-Z]+(\(.*\))?)\s*\)\s*\}(?!\$\{).*\$\{else\}.*\$\{end\}""".r
    val paramR="""^\$\{.*\(""".r
    val keyR="""\(.*\)""".r
    var htmlPath=""

    val scripts=List[(Regex,(String,HttpServletRequest,HttpServletResponse)=>(String,Any))](
          (scriptR,execute) , (ifScriptR,executeIfElse), (ifElseScriptR,executeIf))

    override def init(){
      htmlPath=System.getProperty("user.dir")+"/src/main/webapp/html/"
      println("htmlPath="+htmlPath)
      val files=new java.io.File(htmlPath).listFiles
      files.foreach{f=>
        if(f.getName.endsWith(".shtml"))
          templateMap("/"+f.getName)=Source.fromFile(f.getPath,"UTF-8").mkString
      }
    }

    override def doGet(req: HttpServletRequest, resp: HttpServletResponse) =
      doPost(req,resp)

    
    override def doPost(req: HttpServletRequest, resp: HttpServletResponse) ={
      resp.setCharacterEncoding("UTF-8")
      val url=req.getRequestURI()
      println("url==>"+url)
      if(url=="/reload.shtml")
        init()
      else{
        var text= templateMap(url)
        //执行scala语句
        // text=(text /: scriptR.findAllIn(text)){(total,scr)=>
        //   val result=execute(scr,req,resp)
        //   //println("result=>"+result._2)
        //   if(result._2==null)
        //     total.replace(result._1,"")
        //   else
        //     total.replace(result._1,result._2.toString)
        // }

        // //执行if..end语句
        // text=(text /: ifScriptR.findAllIn(text)){(total,scr)=>
        //   println("ifscript="+scr)
        //   val result=executeIf(scr,req,resp)
        //   println("result=>"+result._2)
        //   if(result._2==null)
        //     total.replace(result._1,"")
        //   else
        //     total.replace(result._1,result._2.toString)
        // }

        // //执行if..else..end语句
        // text=(text /: ifElseScriptR.findAllIn(text)){(total,scr)=>
        //   println("ifElsescript="+scr)
        //   val result=executeIf(scr,req,resp)
        //   println("result=>"+result._2)
        //   if(result._2==null)
        //     total.replace(result._1,"")
        //   else
        //     total.replace(result._1,result._2.toString)
        // }
        text=executeScript(text,req,resp)
        resp.getWriter.println(text)
      }
    }

    def executeScript(content:String,req: HttpServletRequest, resp: HttpServletResponse):String = {
      var t=content
      (t /: scripts){(text, s)=>
        (text /: s._1.findAllIn(text)){(total,scr)=>
          println("scriptName="+scr)
          println("======================")
          val result=s._2(scr,req,resp)
          println("result=>"+result)
          if(result._1!=null){
            if(result._2==null)
              total.replace(result._1,"")
            else
              total.replace(result._1,result._2.toString)
          }else
            total
        }
      }
    }

    //反射执行
    def execute(str: String,req: HttpServletRequest, resp: HttpServletResponse):(String,Any) = {
        //println("execute===>"+str.replace(" ",""))
        val key=keyR.replaceAllIn(str,"").replace(" ","")
        //println("key="+key)
        val params=getParams(str,req,resp)
        //println("params="+params)
        if(controllerMap.contains(key)){
          val cAndM=controllerMap(key)
          //println("========cache==========")
          if(params!=null){ 
            (str,cAndM._2.invoke(cAndM._1,params))
          }else{
           (str,cAndM._2.invoke(cAndM._1,req,resp))
         }
        }else{
            val cmArray=key.replace("${","").replace("}","").split("\\.")
            val className=cmArray(0)
            val methodName=cmArray(1)
            // println("The first "+className+","+methodName)
            val controller=Class.forName(className)
            if(str.contains("(")){
              val me=controller.getMethod(methodName,classOf[List[String]])
              controllerMap(key)=(controller,me)
              (str,me.invoke(controller,params))
            // println(controllerMap)
            }else{
              val me=controller.getMethod(methodName,classOf[HttpServletRequest],
                classOf[HttpServletResponse])
              controllerMap(key)=(controller,me)
              (str,me.invoke(controller,req,resp))
            }
        }
    }

    //反射执行if..end表达式
    def executeIf(str: String,req: HttpServletRequest, resp: HttpServletResponse):(String,Any) = {
      val script=str.replace(" ","").replace("if(","").replace(")}","}").replace("${end}","")
      println(script)
      val boolScript=script.substring(0,script.indexOf("}")+1)
      // if(result.contains("${"))
      //   (null,null)
      val scriptResult=execute(boolScript,req,resp)
      if(scriptResult._2==true){
        val result=script.substring(script.indexOf("}")+1)
        (str,executeScript(result,req,resp))
      }else
        (str,"")
    }

    //反射执行if..else..end表达式
    def executeIfElse(str: String,req: HttpServletRequest, resp: HttpServletResponse):(String,Any) = {
      val script=str.replace(" ","").replace("if(","").replace(")}","}").replace("${end}","")
      println(script)
      val boolScript=script.substring(0,script.indexOf("}")+1)
      val result=script.substring(script.indexOf("}")+1).split("\\$\\{else\\}")
      result.foreach{x=>println("result==="+x)}
      // if(result.exists(_.contains("${")))
      //   (null,null)
      val scriptResult=execute(boolScript,req,resp)
      if(scriptResult._2==true)
        (str,executeScript(result(0),req,resp))
      else
        (str,executeScript(result(1),req,resp))
    }


    //获取参数
    def getParams(str: String, req: HttpServletRequest, resp: HttpServletResponse):List[String] = {
        if(str.contains("(")){
          val params=paramR.replaceAllIn(str,"").replace(")}","").split(",").toList
          //println("params="+params)
          params.map{x=>
            if(x.startsWith("param."))
              req.getParameter(x.trim.substring(6))
            else
              x.trim
          }
        }else{
          null
        }
    }
    def main(args: Array[String]): Unit = {
      println(System.getProperty("user.dir"))
    }
    
}
