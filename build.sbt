name := "template-scala-parallel-classification"

organization := "org.apache.predictionio"

scalaVersion := "2.10.5"

libraryDependencies ++= Seq(
  "org.apache.predictionio" %% "apache-predictionio-core" % "0.10.0-incubating" % "provided",
  "org.apache.spark"        %% "spark-core"               % "1.4.1" % "provided",
  "org.apache.spark"        %% "spark-mllib"              % "1.4.1" % "provided",
  "org.scalatest"           %% "scalatest"                % "2.2.1" % "test")

parallelExecution in Test := false
