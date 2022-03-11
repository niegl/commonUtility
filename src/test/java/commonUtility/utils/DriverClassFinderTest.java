package commonUtility.utils;

class DriverClassFinderTest {
    @org.junit.jupiter.api.Test
    void readFromJarFile() {
        String className = FileKit.readFileFromJar(
                "C:\\Users\\nieguangling\\AppData\\Roaming\\DBeaverData\\drivers\\maven\\maven-central\\mysql\\mysql-connector-java-8.0.17.jar",
                "java.sql.Driver");
        System.out.println(className);
        String className0 = FileKit.readFileFromJar(
                        "C:\\Users\\nieguangling\\AppData\\Roaming\\DBeaverData\\drivers\\maven\\maven-central\\com.oracle.database.jdbc\\ojdbc8-12.2.0.1.jar",
                "java.sql.Driver");
        System.out.println(className0);
        String className1 = FileKit.readFileFromJar(
                "C:\\Users\\nieguangling\\AppData\\Roaming\\DBeaverData\\drivers\\maven\\maven-central\\com.microsoft.sqlserver\\mssql-jdbc-9.2.0.jre8.jar",
                "java.sql.Driver");
        System.out.println(className1);
        String className2 = FileKit.readFileFromJar(
                "C:\\文档\\历史\\历史资料\\hive\\hive-jdbc-1.2.1-standalone.jar",
                "java.sql.Driver");
        System.out.println(className2);
    }
}