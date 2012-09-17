import javax.servlet.http._
import scala.collection.mutable.HashMap

class MvcServlet extends HttpServlet {

  val controllerMap=new HashMap[String,(Object,java.lang.reflect.Method)]

  override def doGet(req: HttpServletRequest, resp: HttpServletResponse) =
    doPost(req,resp)
  override def doPost(req: HttpServletRequest, resp: HttpServletResponse) ={
    //resp.getWriter().print("Hello World,post hahahahahah!")
  	val url=req.getRequestURI()
  	println("url==>"+url)
  	val classAndMethod=url.split("/")
    if(controllerMap.contains(url)){
        val cAndM=controllerMap(url)
        cAndM._2.invoke(cAndM._1,req,resp)
    }else{
        val className=classAndMethod(1)
        val methodName=classAndMethod(2)
        val controller=Class.forName(className+"Controller")
        val me=controller.getMethod(methodName,classOf[HttpServletRequest],classOf[HttpServletResponse])
        controllerMap + url -> (controller,me)
        me.invoke(controller,req,resp)
    }
  }

}
