name := "RecordHolder"
scalaVersion := "2.12.0"

libraryDependencies += "javax" % "javaee-api" % "7.0" % "provided"
//libraryDependencies += "org.apache.myfaces.core" % "myfaces-api" % "2.2.11"
//libraryDependencies += "org.apache.myfaces.core" % "myfaces-impl" % "2.2.11"
libraryDependencies += "com.sun.faces" % "jsf-api" % "2.2.13"
libraryDependencies += "com.sun.faces" % "jsf-impl" % "2.2.13"
libraryDependencies += "org.primefaces" % "primefaces" % "6.0"
libraryDependencies += "org.primefaces.themes" % "all-themes" % "1.0.10"
//libraryDependencies += "org.hibernate" % "hibernate-core" % "5.2.4.Final"

enablePlugins(TomcatPlugin)
enablePlugins(WarPlugin)
