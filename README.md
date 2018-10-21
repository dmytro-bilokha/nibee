# Nibee

The application is a very simple blog platform. Its main goal is to be
fast, simple and secure.  Posts' metadata is stored in the database. Posts'
content is stored on the hard drive in the specified directory.  The
application parses request URL, reads metadata from the DB and serves files
from the directory.

## Deployment Procedure

1. Install [Payara Application Server](https://payara.fish/). Nibee should
work with any other application server which matches the Java EE 7
specification, but it is not tested and the deployment procedure will be a
bit different.

2. Install and setup [MySQL](https://dev.mysql.com/). Again, Nibee should work with
any DB which has JDBC driver, but the deployment procedure will differ.

3. Run MySQL console and issue the following commands to create a database, 
database user and grant access: 
```SQL
CREATE DATABASE nibee CHARACTER SET utf8mb4;
CREATE USER 'nibee' IDENTIFIED BY 'secret_password_goes_here';
GRANT ALL ON nibee.* TO nibee;
```

4. Download MySQL [JDBC driver](http://dev.mysql.com/downloads/connector/j/) jar and 
store it locally.

5. Enter `asadmin` console and issue the following commands to create JDBC
connection pool and resource (you may need to tweak the following commands according to your configuration):
```
add-library /path/to/locally/stored/mysql-jdbc-driver.jar
create-jdbc-connection-pool --restype=javax.sql.DataSource --datasourceclassname=com.mysql.cj.jdbc.MysqlDataSource --property user=nibee:password=secret_password_goes_here:DatabaseName=nibee:ServerName=localhost:port=3306:useLegacyDatetimeCode=false:useJDBCCompliantTimezoneShift=true:useSSL=false --steadypoolsize=2 --maxpoolsize=2 --isconnectvalidatereq=true --validationmethod=table --validationtable=DUAL --validateatmostonceperiod=60 --ping=true nibeeJdbcPool
create-jdbc-resource --connectionpoolid=nibeeJdbcPool jdbc/nibeeDataSource
```

6. From `asadmin` console create application configuration properties resource and add required properties:
```
create-custom-resource --restype=java.util.Properties --factoryclass=org.glassfish.resources.custom.factory.PropertiesFactory --property postsRoot=your_posts_root_directory nibee/properties
set resources.custom-resource.nibee/properties.property.headlinersPerPage=10
set resources.custom-resource.nibee/properties.property.webLogRecordsMax=10000000
```

7. Put blog data under `your_posts_root_directory` from the previous step.

8. Deploy the application EAR artifact to the Payara server.

## Tested Environment Configuration

The application has been proven to work in following environment:

* Payara Application Server 5.183;
* MySQL 8.0.2;
* FreeBSD operation system version 11.2;
* OpenJDK 1.8.

