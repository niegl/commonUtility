package commonUtility.utils;

import static org.junit.jupiter.api.Assertions.*;

class DriverClassFinderTest {
    @org.junit.jupiter.api.Test
    void readFromJarFile() {
        String className = DriverClassFinder.readFromJarFile(
                "C:\\Users\\nieguangling\\AppData\\Roaming\\DBeaverData\\drivers\\maven\\maven-central\\mysql\\mysql-connector-java-8.0.17.jar",
                "java.sql.Driver");
        System.out.println(className);
        String className0 = DriverClassFinder.readFromJarFile(
                        "C:\\Users\\nieguangling\\AppData\\Roaming\\DBeaverData\\drivers\\maven\\maven-central\\com.oracle.database.jdbc\\ojdbc8-12.2.0.1.jar",
                "java.sql.Driver");
        System.out.println(className0);
        String className1 = DriverClassFinder.readFromJarFile(
                "C:\\Users\\nieguangling\\AppData\\Roaming\\DBeaverData\\drivers\\maven\\maven-central\\com.microsoft.sqlserver\\mssql-jdbc-9.2.0.jre8.jar",
                "java.sql.Driver");
        System.out.println(className1);
        String className2 = DriverClassFinder.readFromJarFile(
                "C:\\文档\\历史\\历史资料\\hive\\hive-jdbc-1.2.1-standalone.jar",
                "java.sql.Driver");
        System.out.println(className2);
    }
}