name:= "hello-world"
 
version:= "1.0"
 
scalaVersion:= "2.9.1"

seq(webSettings :_*)

libraryDependencies ++= Seq(
  //"org.eclipse.jetty.orbit" % "javax.servlet.jsp" % "2.2.0.v201112011158",
  //"org.eclipse.jetty.orbit" % "javax.servlet" % "3.0.0.v201112011016",
  //"org.eclipse.jetty.orbit" % "javax.servlet.jsp.jstl" % "1.2.0.v201105211821",
  //"org.eclipse.jetty.orbit" % "javax.el" % "2.2.0.v201108011116",
  "org.eclipse.jetty" % "jetty-webapp" % "8.0.0.v20110901" % "container",
  "javax.servlet" % "servlet-api" % "3.0-alpha-1" % "provided",
  //"javax.servlet" % "jsp-api" % "2.0",
  //"javax.servlet" % "jstl" % "1.2",
  //"org.mortbay.jetty" % "jsp-2.1" % "6.1.14",
  //"javax.servlet.jsp" % "javax.servlet.jsp-api" % "2.2.1",
  //"javax.servlet" % "jstl" % "1.2",
  //"javax.servlet.jsp.jstl" % "jstl-api" % "1.2",
  //"javax.el" % "el-api" % "2.2.1-b04",
  //"org.mortbay.jetty" % "jsp-2.1" % "7.0.0pre2",
  //"org.mortbay.jetty" % "jsp-api-2.1" % "7.0.0pre2",
  //"org.mortbay.jetty" % "jsp-impl" % "2.2.2.b05.0",
  //"org.mortbay.jetty" % "jetty-util" % "7.0.0.pre5",
  "taglibs" % "standard" % "1.1.2",
  "org.slf4j" % "slf4j-api" % "1.6.4",
  "org.slf4j" % "slf4j-log4j12" % "1.6.4", 
  "commons-lang" % "commons-lang" % "2.6",
  "commons-logging" % "commons-logging" % "1.1.1",
  "commons-pool" % "commons-pool" % "1.5.5",
  "commons-dbcp" % "commons-dbcp" % "1.4",
  "commons-beanutils" % "commons-beanutils" % "1.8.3",
  "mysql" % "mysql-connector-java" % "5.1.21"
)
