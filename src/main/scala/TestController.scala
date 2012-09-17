import javax.servlet.http._
object testController{
	def hello(req: HttpServletRequest, resp: HttpServletResponse) = {
		resp.getWriter().print("Hello testController!")
	}
}