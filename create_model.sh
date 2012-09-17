#!/bin/bash
#Create database table corresponding to the scala class

#create the specified table, if not, create all tables
#tableName=mmx_blog
#the directory where is scala class created, if not, default current directory
#target_folder=/home/michael/scala/
target_folder="/Users/michael/Google Drive/EverBox/code/scala-mvc/src/main/scala/model/"

#database info
dbHost=10.11.200.233
dbUser=devuser
dbPass=devuser
dbName=yy

#database table name prefix
table_prefix=mmx_

tables=`mysql -u$dbUser -p$dbPass -h$dbHost $dbName <<EOF | tail -n +2 
show tables; 
EOF`
for table in $tables
do
	class_name=`echo ${table#*$table_prefix} | perl -pe 's/.*/\u$&/'`
	file_name="${target_folder}"${class_name}.scala
	#echo table_name : $table
	#echo file_name : $file_name
	#echo class_name : $class_name
	fields=`mysql -u$dbUser -p$dbPass -h$dbHost $dbName <<EOF | tail -n +2 
select column_name,data_type,case column_key when 'PRI' then 1 else 0 end is_pk  
from information_schema.columns where table_name='$table' ; 
EOF`
	#echo $fields
	if [ -z $tableName ] || [ $table = $tableName ];then
	echo create table model:$table
	echo "import scala.reflect.BeanProperty
class $class_name extends MysqlStore[$class_name](){
	dataSourceKey=\"$dbName\"
	table=\"$table\"
	override val pk=List[String](@pk@)
	columns=List(@columns@)
	" > "$file_name"
	i=1
	n=1
	columnStr=""
	pkStr=""
	for f in $fields;
	do
		[ $i -eq 4 ] && i=1;
		[ $i -eq 1 ] && fname=$f;
		[ $i -eq 2 ] && type=$f;
		[ $i -eq 3 ] && pk=$f;
		if [ $i -eq 3 ];then
			#echo $fname : $type : $pk
			columnStr=${columnStr}$fname,
			[ $pk -eq 1 ] && pkStr=${pkStr}$fname,
			stype=""
			[ $type = "int" ] && stype="Int"
			[ $type = "smallint" ] && stype="Int"
			[ $type = "bigint" ] && stype="Int"
			[ $type = "tinyint" ] && stype="Int"
			[ $type = "varchar" ] && stype="String"
			[ $type = "timestamp" ] && stype="java.util.Date"
			echo "	@BeanProperty
	var $fname:$stype=_
	" >> "$file_name"
			let n++
		fi
		let i++
	done
	pks=\"`echo ${pkStr%,} |sed "s/,/\",\"/g"`\"
	columns=\"`echo ${columnStr%,} |sed "s/,/\",\"/g"`\"
	#echo pks=$pks
	#echo columns=$columns
	columns_to_str=`echo $columnStr|sed 's@\([a-zA-Z_]*\)@\1:\"\+\1\+\"@g'`
	#echo columns_to_str=$columns_to_str
	echo "	override def toString():String={
		\"$class_name=[${columns_to_str%,:\"++\"}]\"
	}
}

object $class_name extends MysqlStore[$class_name]{
	dataSourceKey=\"$dbName\"
	table=\"$table\"
	override val pk=List[String](@pk@)
	columns=List(@columns@)
}" >> "$file_name"
	sed "s/@pk@/${pks}/g; s/@columns@/$columns/g" "${file_name}"> "${file_name}"_tmp
	mv "${file_name}"_tmp "${file_name}"
fi
done
exit 0