import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import java.util.Date

object MessageService {

	def getMyMessage(d:List[String]):List[Blog]={
		val b=new Blog
		b.id=1
		b.content="xxx"
		b.create_time=new Date

		val b2=new Blog
		b2.id=1
		b2.content="yyy"
		b2.create_time=new Date
		List(b,b2)
	}

	def postMessage(d:List[String]):Boolean={
		
		true
	}

	def delMessage(d:List[String]):Boolean={
		true
	}

	def getFollowMessage():List[Blog]={
		List(new Blog)
	}

	def postComment(args:List[String]):Boolean={
		true
	}
	def getComments(args:List[String]):List[Comment]={
		Comment.q(List("id","content"),"where user_id=? and blog_id=?",List(1,args(0)))
	}

	def postReview(args:List[String]):Boolean={
		true
	}

	def getMyMoods(args:List[String]):List[String]={
		val m=1 :: Mood
		m.content.split(",").toList
	}

	def addMood(args:List[String]):Boolean={
		true
	}

	def delMood(args:List[String]):Boolean={
		true
	}
}