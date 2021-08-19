lazy val root = project.in(file("."))
  .aggregate(core, plugin)
  .dependsOn(core)
  .settings(inThisBuild(Seq(
    organization := "com.lightbend.benchdb",
    //version := "0.1-SNAPSHOT",
    scalaVersion := "2.12.14",
    scalacOptions ++= Seq("-deprecation", "-unchecked"),
  )))
  .settings(
    publishArtifact := false,
    publish := {},
    publishLocal := {},
  )

lazy val core = project.in(file("core"))
  .enablePlugins(BuildInfoPlugin)
  .settings(
    name := "benchdb-core",
    buildInfoKeys := Seq[BuildInfoKey](version),
    buildInfoPackage := "com.lightbend.benchdb",
    libraryDependencies ++= Seq(
      "org.eclipse.jgit" % "org.eclipse.jgit" % "5.12.0.202106070339-r",
      "com.monovore" %% "decline" % "1.4.0",
      "com.github.pathikrit" %% "better-files" % "3.9.1",
      "com.typesafe.slick" %% "slick" % "3.3.2",
      "mysql" % "mysql-connector-java" % "8.0.26" % "optional",
      "com.h2database" % "h2" % "1.4.200" % "optional",
      "com.typesafe" % "config" % "1.4.0",
      "org.slf4j" % "slf4j-api" % "1.7.32",
      "ch.qos.logback" % "logback-classic" % "1.1.6",
      "com.novocode" % "junit-interface" % "0.11" % "test"
    ),
    testOptions += Tests.Argument(TestFrameworks.JUnit, "-q", "-v", "-s", "-a"),
    Test / fork := true,
    Test / parallelExecution := false
  )

lazy val plugin = project.in(file("plugin"))
  .enablePlugins(SbtPlugin)
  .enablePlugins(BuildInfoPlugin)
  .settings(
    name := "sbt-benchdb",
    sbtPlugin := true,
    buildInfoKeys := Seq[BuildInfoKey](organization, core / name, version, core / scalaVersion),
    buildInfoPackage := "com.lightbend.benchdb.sbtplugin",
    publishMavenStyle := false,
    licenses += ("Apache-2.0", url("http://www.apache.org/licenses/LICENSE-2.0.html")),
    scriptedLaunchOpts ++= Seq("-Xmx1024M", "-Dplugin.version=" + version.value),
    scriptedBufferLog := false,
    scriptedDependencies := { val _ = ((core / publishLocal).value, publishLocal.value) },
  )
