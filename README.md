# ExchangeRateUtility
---------------------------------------------------------
Project: Currency Exchange Rates Utility - README File
---------------------------------------------------------

Following steps are required to be considered before starting program:

* Configure JSON URL and Database Connection parameters in ./conf/app.properties file
* Create database schema and table in mysql database. Script is available in ./db/db-init.sql file
* JDK/JRE1.8* should be installed.
*By executing following command, application will run:

#java -jar ./dist/ExchangeRateUtility.jar ./conf/app.properties

* Following dependencies are included as external Jar files in libs folder
 1. java-json-schema.jar
 2. mysql-connector-java-8.019.jar

Please note Maven is not used as build tool to configure project and its associated dependencies

Following tools are used to complete project:
1. JRE 1.8.0
2. MySQL 8.0.19
3. Eclipse IDE 2019-12 (4.14.0)
