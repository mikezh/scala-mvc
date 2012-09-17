import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

object UserService {

	def checkUsername(d:List[String]):Boolean={
		true
	}

	def register(d:List[String]):Boolean={

		true
	}

	def login(d:List[String]):Boolean={
		true
	}

	def logout():Boolean={
		true
	}

	def invite(args:List[String]):Boolean={
		true
	}

	def getInvites(args:List[String]):List[Invite]={
		List(new Invite)
	}

	def delInvite(args:List[String]):Boolean={
		true
	}

	def acceptInvite(args:List[String]):Boolean={
		true
	}

	def getFans(args:List[String]):User={
		new User
	}

	def delFans(args:List[String]):Boolean={
		true
	}
}