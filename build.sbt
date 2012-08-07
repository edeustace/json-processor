import AssemblyKeys._ // put this at the top of the file

assemblySettings

name := "json-processor"

version := "0.0.1"

scalaVersion := "2.9.1"

resolvers += "repo.codahale.com" at "http://repo.codahale.com"

resolvers += "specs2 snapshots" at "http://oss.sonatype.org/content/repositories/snapshots"

resolvers += "specs2 releases"  at "http://oss.sonatype.org/content/repositories/releases"

libraryDependencies += "com.codahale" %% "jerkson" % "0.5.0"

libraryDependencies += "org.specs2" %% "specs2" % "1.12" % "test"

