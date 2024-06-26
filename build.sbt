import bindgen.interface.Binding
import bindgen.plugin.BindgenMode

inThisBuild(
  List(
    homepage := Some(
      url("https://github.com/indoorvivants/scala-native-gtk-bindings")
    ),
    startYear := Some(2023),
    licenses := List(
      "Apache-2.0" -> url("http://www.apache.org/licenses/LICENSE-2.0")
    ),
    developers := List(
      Developer(
        "indoorvivants",
        "Anton Sviridov",
        "contact@indoorvivants.com",
        url("https://blog.indoorvivants.com")
      )
    ),
    crossScalaVersions := Nil
  )
)

organization := "com.indoorvivants.gnome"
sonatypeProfileName := "com.indoorvivants"

val publishing = Seq(
  organization := "com.indoorvivants.gnome",
  sonatypeProfileName := "com.indoorvivants"
  /* sonatypeCredentialHost := "s01.oss.sonatype.org" */
)

lazy val root = project
  .in(file("."))
  .aggregate(
    adwaita,
    gio,
    glib,
    gobject,
    cairo,
    gdkpixbuf,
    graphene,
    gtk4,
    harfbuzz,
    pango,
    `gir-schema`,
    girepository
  )
  .settings(
    publish / skip := true,
    publishLocal / skip := true,
    pushRemoteCacheTo := Some(
      MavenCache(
        "local-cache",
        (ThisBuild / baseDirectory).value / ".remote-cache"
      )
    )
  )

lazy val examples = project
  .in(file("examples"))
  .dependsOn(gtk4)
  .configure(pkgConfigured("gtk4"))
  .settings(publish / skip := true, publishLocal / skip := true)

lazy val adwaita = project
  .in(file("adwaita"))
  .dependsOn(gtk4)
  .configure(pkgConfigured("libadwaita-1"))
  .settings(
    bindgenBindings +=
      buildWithDependencies(
        "gdkpixbuf",
        "gio",
        "glib",
        "gobject",
        "graphene",
        "gtk",
        "libcairo",
        "libharfbuzz",
        "pango"
      ) {
        Binding
          .builder(findHeader("libadwaita-1", _ / "adwaita.h"), "adwaita")
          .withClangFlags(pkgConfig("libadwaita-1", "cflags"))
          .addCImport("adwaita.h")
          .withMultiFile(true)
      }
  )

lazy val gio = project
  .in(file("gio"))
  .dependsOn(glib, gobject)
  .configure(pkgConfigured("gio-2.0"))
  .settings(
    bindgenBindings +=
      buildWithDependencies("glib", "gobject") {
        Binding
          .builder(findHeader("gio-2.0", _ / "gio" / "gio.h"), "gio")
          .withClangFlags(pkgConfig("gio-2.0", "cflags"))
          .addCImport("gio.h")
          .withOpaqueStructs(Set("G*"))
          .withMultiFile(true)
      }
  )

lazy val glib = project
  .in(file("glib"))
  .configure(pkgConfigured("glib-2.0"))
  .settings(
    bindgenBindings +=
      Binding
        .builder(findHeader("glib-2.0", _ / "glib.h"), "glib")
        .withClangFlags(pkgConfig("glib-2.0", "cflags"))
        .addCImport("glib.h")
        .withMultiFile(true)
        .build
  )

lazy val gtk4 = project
  .in(file("gtk4"))
  .dependsOn(glib, gio, gobject, cairo, harfbuzz, graphene, pango, gdkpixbuf)
  .configure(pkgConfigured("gtk4"))
  .settings(
    bindgenBindings +=
      buildWithDependencies(
        "gdkpixbuf",
        "gio",
        "glib",
        "gobject",
        "graphene",
        "libcairo",
        "libharfbuzz",
        "pango"
      ) {
        Binding
          .builder(findHeader("gtk4", _ / "gtk" / "gtk.h"), "gtk")
          .withClangFlags(pkgConfig("gtk4", "cflags"))
          .addCImport("graphene.h")
          .withMultiFile(true)
      }
  )

lazy val gobject =
  project
    .in(file("gobject"))
    .dependsOn(glib)
    .configure(pkgConfigured("gobject-2.0"))
    .settings(
      bindgenBindings +=
        buildWithDependencies("glib", "gio") {
          Binding
            .builder(findHeader("gobject-2.0", _ / "glib-object.h"), "gobject")
            .withClangFlags(pkgConfig("gobject-2.0", "cflags"))
            .addCImport("glib-object.h")
            .withMultiFile(true)
        }
    )

lazy val pango =
  project
    .in(file("pango"))
    .dependsOn(glib, cairo, gobject, harfbuzz)
    .configure(pkgConfigured("pango"))
    .settings(
      bindgenBindings +=
        buildWithDependencies("glib", "libcairo", "gobject", "libharfbuzz") {
          Binding
            .builder(findHeader("pango", _ / "pango" / "pango.h"), "libpango")
            .withClangFlags(pkgConfig("pango", "cflags"))
            .addCImport("pango.h")
            .withMultiFile(true)
        }
    )

lazy val gdkpixbuf =
  project
    .in(file("gdkpixbuf"))
    .dependsOn(glib, cairo, gobject, gio)
    .configure(pkgConfigured("gdk-pixbuf-2.0"))
    .settings(
      bindgenBindings +=
        buildWithDependencies("glib", "gobject", "gio", "libcairo") {
          Binding
            .builder(
              findHeader("gdk-pixbuf-2.0", _ / "gdk-pixbuf" / "gdk-pixbuf.h"),
              "libgdkpixbuf"
            )
            .withClangFlags(pkgConfig("gdk-pixbuf-2.0", "cflags"))
            .withMultiFile(true)
        }
    )

lazy val cairo =
  project
    .in(file("cairo"))
    .dependsOn(glib)
    .configure(pkgConfigured("cairo"))
    .settings(
      bindgenBindings +=
        Binding
          .builder(findHeader("cairo", _ / "cairo.h"), "libcairo")
          .withClangFlags(pkgConfig("cairo", "cflags"))
          .addCImport("cairo.h")
          .withMultiFile(true)
          .build
    )

lazy val graphene =
  project
    .in(file("graphene"))
    .dependsOn(glib)
    .configure(pkgConfigured("graphene-1.0"))
    .settings(
      bindgenBindings +=
        Binding
          .builder(findHeader("graphene-1.0", _ / "graphene.h"), "libgraphene")
          .withClangFlags(pkgConfig("graphene-1.0", "cflags"))
          .addCImport("graphene.h")
          .addClangFlag(
            List("-Dsse2=false", "-Darm_neon=false", "-Dgcc_vector=false")
          )
          .addExternalName("graphene_simd4f_get", "<nopackage>")
          .withMultiFile(true)
          .build
    )

lazy val girepository =
  project
    .in(file("girepository"))
    .dependsOn(gobject)
    .configure(pkgConfigured("gobject-introspection-1.0"))
    .settings(
      bindgenBindings +=
        buildWithDependencies("glib", "gobject") {
          Binding
            .builder(
              findHeader("gobject-introspection-1.0", _ / "girepository.h"),
              "girepository"
            )
            .withClangFlags(pkgConfig("gobject-introspection-1.0", "cflags"))
            .addCImport("girepository.h")
            .withMultiFile(true)
        }
    )

lazy val harfbuzz =
  project
    .in(file("harfbuzz"))
    .dependsOn(glib)
    .configure(pkgConfigured("harfbuzz"))
    .settings(
      bindgenBindings +=
        Binding
          .builder(findHeader("harfbuzz", _ / "hb.h"), "libharfbuzz")
          .withClangFlags(pkgConfig("harfbuzz", "cflags"))
          .addCImport("hb.h")
          .withMultiFile(true)
          .build
    )

def generateXsd(
    fileName: String,
    sourceDirectoryValue: File,
    targetDirectoryValue: File
): Unit = {
  import com.thaiopensource.relaxng.input.parse.compact.CompactParseInputFormat
  import com.thaiopensource.relaxng.output.LocalOutputDirectory
  import com.thaiopensource.relaxng.output.xsd.XsdOutputFormat
  import com.thaiopensource.util.UriOrFile
  import com.thaiopensource.xml.sax.ErrorHandlerImpl

  val inputFormat = new CompactParseInputFormat()
  val outputFormat = new XsdOutputFormat()
  val inputFile = sourceDirectoryValue / "relaxng" / s"$fileName.rnc"
  val eh = new ErrorHandlerImpl()
  val params = Array[String]()
  val sc = inputFormat.load(
    UriOrFile.toUri(inputFile.toString),
    params,
    "xsd",
    eh,
    null
  )
  val resultDir = targetDirectoryValue / "xsd"
  resultDir.mkdirs()
  val result = resultDir / s"$fileName.xsd"
  val od = new LocalOutputDirectory(
    sc.getMainUri,
    result,
    ".xsd",
    "UTF-8",
    72,
    2
  )
  outputFormat.output(sc, od, params, "rnc", eh)
}

lazy val generateXsd = TaskKey[Unit]("generateXsd")

lazy val `gir-schema` = project
  .in(file("gir-schema"))
  .configure(pkgConfiguredSimple)
  .enablePlugins(ScalaxbPlugin)
  .settings(
    Compile / generateXsd := {
      generateXsd(
        "gir",
        (Compile / sourceDirectory).value,
        (Compile / sourceDirectory).value
      )
    },
    Compile / scalaxb / scalaxbPackageName := "com.indoorvivants.gnome.gir_schema",
    libraryDependencies ++= List(
      "javax.xml.bind" % "jaxb-api" % "2.3.1",
      "org.scala-lang.modules" %% "scala-parser-combinators" % "2.2.0",
      "org.scala-lang.modules" %% "scala-xml" % "2.1.0"
    )
  )

def pkgConfig(pkg: String, arg: String) = {
  import sys.process.*
  s"pkg-config --$arg $pkg".!!.trim.split(" ").toList
}

def findHeader(pkgName: String, file: java.io.File => java.io.File) = {
  val cFlags = pkgConfig(pkgName, "cflags")
    .filter(_.startsWith("-I"))
    .map(_.stripPrefix("-I"))
    .map(new File(_))

  cFlags
    .map(file)
    .filter(_.exists())
    .headOption
    .getOrElse(
      throw new sbt.internal.util.MessageOnlyException(
        s"Failed to find header in folders received from pkg-config: $cFlags"
      )
    )
}

def pkgConfiguredSimple: Project => Project = { proj =>
  proj
    .enablePlugins(ScalaNativePlugin)
    .settings(publishing)
    .settings(
      Compile / doc / sources := Seq.empty,
      pushRemoteCacheTo := Some(
        MavenCache(
          "local-cache",
          (ThisBuild / baseDirectory).value / ".remote-cache"
        )
      ),
      resolvers ++= Resolver.sonatypeOssRepos("snapshots"),
      scalaVersion := "3.3.3"
    )
}

def pkgConfigured(name: String): Project => Project = { proj =>
  pkgConfiguredSimple(proj)
    .enablePlugins(BindgenPlugin)
    .settings(publishing)
    .settings(
      nativeCompileOptions ++= {
        pkgConfig(name, "cflags")
      },
      nativeLinkingOptions ++= {
        pkgConfig(name, "libs")
      },
      bindgenMode := BindgenMode.Manual(
        scalaDir = sourceDirectory.value / "main" / "scala" / "generated",
        cDir =
          (Compile / resourceDirectory).value / "scala-native" / "generated"
      )
    )
}

def buildWithDependencies(deps: String*)(bb: Binding.Builder) = {
  def extPaths(dep: String) = dep match {
    case "glib" =>
      List(
        "*/glib-2.0/glib/*",
        "*/glib-2.0/glib.h",
        "*/glibconfig.h",
        "*/glib-2.0/gmodule.h"
      )
    case "gio" =>
      List(
        "*/glib-2.0/gio/*"
      )
    case "gobject" =>
      List(
        "*/glib-2.0/gobject/*",
        "*/glib-2.0/gobject.h"
      )
    case "libcairo" =>
      List("*/cairo/*")
    case "libharfbuzz" => List("*/harfbuzz/*")
    case "gtk" =>
      List(
        "*/gtk-4.0/gdk/*",
        "*/gtk-4.0/gsk/*",
        "*/gtk-4.0/gtk/*"
      )
    case "graphene"  => List("*/graphene-1.0/*")
    case "pango"     => List("*/pango-1.0/*")
    case "gdkpixbuf" => List("*/gdk-pixbuf-2.0/*")
  }

  val externals =
    deps.flatMap { dep => extPaths(dep).map(_ -> dep).toMap }.toMap

  bb.addExternalPaths(externals).build
}

Global / onChangedBuildSource := ReloadOnSourceChanges

pushRemoteCacheTo := Some(
  MavenCache(
    "local-cache",
    (ThisBuild / baseDirectory).value / "target" / "remote-cache"
  )
)
