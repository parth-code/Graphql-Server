
name := "Graphql-Server"

version := "0.1"

scalaVersion := "2.12.8"


enablePlugins(GraphQLSchemaPlugin, GraphQLQueryPlugin)

// https://mvnrepository.com/artifact/org.sangria-graphql/sangria
libraryDependencies += "org.sangria-graphql" %% "sangria" % "1.4.2"

// https://mvnrepository.com/artifact/org.sangria-graphql/sangria-marshalling-api
libraryDependencies += "org.sangria-graphql" %% "sangria-marshalling-api" % "1.0.3"

// https://mvnrepository.com/artifact/org.sangria-graphql/sangria-circe
libraryDependencies += "org.sangria-graphql" %% "sangria-circe" % "1.2.1"

// https://mvnrepository.com/artifact/com.typesafe.akka/akka-stream
libraryDependencies += "com.typesafe.akka" %% "akka-stream" % "2.5.25"

// https://mvnrepository.com/artifact/com.typesafe.akka/akka-http
libraryDependencies += "com.typesafe.akka" %% "akka-http" % "10.1.9"

// https://mvnrepository.com/artifact/com.typesafe.akka/akka-http-spray-json
libraryDependencies += "com.typesafe.akka" %% "akka-http-spray-json" % "10.1.9"

// https://mvnrepository.com/artifact/org.scalatest/scalatest
libraryDependencies += "org.scalatest" %% "scalatest" % "3.0.8" % Test