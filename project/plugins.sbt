resolvers ++= Resolver.sonatypeOssRepos("snapshots")

addSbtPlugin("org.scala-native" % "sbt-scala-native" % "0.4.17")

addSbtPlugin("com.indoorvivants" % "sbt-commandmatrix" % "0.0.5")

addSbtPlugin("com.github.sbt" % "sbt-ci-release" % "1.5.11")

addSbtPlugin("org.scalameta" % "sbt-scalafmt" % "2.4.6")

addSbtPlugin(
  "com.indoorvivants" % "subatomic-plugin" % "0.0.7"
)

val BindgenVersion =
  sys.env.getOrElse("SN_BINDGEN_VERSION", "0.0.16+1-f12b1354-SNAPSHOT")

addSbtPlugin(
  "com.indoorvivants" % "bindgen-sbt-plugin" % BindgenVersion
)

addSbtPlugin("com.eed3si9n" % "sbt-buildinfo" % "0.11.0")

addSbtPlugin("org.jmotor.sbt" % "sbt-dependency-updates" % "1.2.7")

addSbtPlugin("org.scalaxb" % "sbt-scalaxb" % "1.10.0")

libraryDependencies ++= List(
  "com.indoorvivants.detective" %% "platform" % "0.0.2",
  "org.relaxng" % "trang" % "20220510"
)
