ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.12.20"

lazy val root = (project in file("."))
  .settings(
    name := "AzureStorageUtilities"
  )

// https://mvnrepository.com/artifact/com.github.docker-java/docker-java
libraryDependencies += "com.github.docker-java" % "docker-java" % "3.4.0"

// https://mvnrepository.com/artifact/com.github.docker-java/docker-java-transport-httpclient5
libraryDependencies += "com.github.docker-java" % "docker-java-transport-httpclient5" % "3.4.0"

// https://mvnrepository.com/artifact/com.github.docker-java/docker-java-transport-zerodep
libraryDependencies += "com.github.docker-java" % "docker-java-transport-zerodep" % "3.4.0"

// https://mvnrepository.com/artifact/com.azure/azure-storage-blob
libraryDependencies += "com.azure" % "azure-storage-blob" % "12.28.1"

libraryDependencies += "org.scalatest" %% "scalatest" % "3.2.19" % Test