import java.sql.SQLException
import javax.sql.DataSource
import scala.collection.mutable.Map

import org.apache.commons.dbcp.BasicDataSource

/**
 * 数据源工厂
 * @author Michael.Zhang
 *
 * 2012-4-10
 */
object DataSourceFactory {

	val MYSQL_DRIVER="com.mysql.jdbc.Driver"
	val dataSourceMap=Map[String, BasicDataSource]()
	
	/**
	 * 初始化数据源
	 */
	def initDB(dbkey:String, url:String, username:String, passwd:String):Unit= {
		// Set<String> keys=PropertiesUtil.getAllKeys()
		// for (String key : keys) {
		// 	if(!key.startsWith(config_db_prefix))
		// 		continue
			
		// 	String urlkey=key.substring(3)
		// 	String url = PropertiesUtil.getString(urlkey)
		// 	if (url == null) {
		// 		System.err.println(urlkey + "'s value is null.")
		// 		continue
		// 	}

			val dataSource = new BasicDataSource
			dataSource.setDriverClassName(MYSQL_DRIVER)
			// if (url.contains(",")) {
			// 	String[] urlAndUser = url.split(",")
			// 	dataSource.setUrl(urlAndUser[0])
			// 	dataSource.setUsername(urlAndUser[1])
			// 	dataSource.setPassword(urlAndUser[2])
			// } else {

			dataSource.setUrl(url)
			dataSource.setUsername(username)
			dataSource.setPassword(passwd)
			// }
			dataSource.setMaxActive(5)
			// dataSource.setMaxWait(PropertiesUtil.getInt("maxWait"))
			// dataSource.setMaxIdle(PropertiesUtil.getInt("maxIdle"))
			// dataSource.setTimeBetweenEvictionRunsMillis(PropertiesUtil
			// 		.getInt("timeBetweenEvictionRunsMillis"))
			// dataSource.setMinEvictableIdleTimeMillis(PropertiesUtil
			// 		.getInt("minEvictableIdleTimeMillis"))
			// dataSource.setValidationQuery("select 1")
			// dataSource.setValidationQueryTimeout(PropertiesUtil.getInt("validationQueryTimeout"))
		// 	dataSources.put(urlkey, dataSource)
		// }
		dataSourceMap(dbkey)=dataSource
	}

	/**
	 * 获取数据源
	 * @param key
	 * @return
	 */
	def getDataSource(key:String):DataSource = {
		dataSourceMap(key)
	}

	/**
	 * 关闭数据源
	 */
	def closeDB():Unit= {
		for ( dataSource <- dataSourceMap.values) {
				dataSource.close()
		}
	}
}

