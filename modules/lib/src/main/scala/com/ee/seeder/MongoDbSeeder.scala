package com.ee.seeder

import java.io.File
import scala.Some
import com.ee.seeder.model._
import com.mongodb.casbah.{ MongoDB, MongoURI, MongoConnection, MongoCollection }
import com.ee.seeder.log.ConsoleLogger
import com.ee.seeder.log.ConsoleLogger.Level

class Seeder(val db: MongoDB, override val mainLevel: Level.Level) extends ConsoleLogger {

  case class SeedList(path: String, formats: Seq[SeedFormat])

  def emptyDb(paths: List[String], log: Boolean = true): Unit = {
    val seedLists = paths.map(buildSeedList)
    val collectionNames = seedLists.map(_.formats.map(_.collection))
    val stripped = collectionNames.flatten.distinct

    def empty(db: MongoDB, name: String): Unit = {
      db(name).drop()
      if (log) {
        debug(" Emptqqy: " + name + " count: " + db(name).count())
      }
    }

    withDb(stripped, empty)
  }

  def seed(paths: List[String], clear: Boolean): Unit = {
    if (clear) {
      emptyDb(paths, true)
    }
    paths.foreach(seedPath(_))
  }

  def unseed(paths: List[String]): Unit = {
    emptyDb(paths)
  }

  private def buildSeedList(path: String): SeedList = {
    val folder: File = new File(path)

    if (folder.exists && folder.isDirectory) {
      val files = for (file <- folder.listFiles()) yield file
      SeedList(path = path, formats = files.flatMap(getSeedFormat).toList)
    } else {
      warn("Error: ignored path: " + path)
      SeedList(path, Seq())
    }
  }

  private def seedPath(path: String) {

    info("seed - path: " + path)
    val seedList = buildSeedList(path)

    withDb(seedList.formats.toList, (db: MongoDB, f: SeedFormat) => {
      val collection: MongoCollection = db(f.collection)
      info("collection: " + collection.name)
      debug("Before collection: " + collection.name)
      f match {
        case JsonOnEachLine(c, file) => JsonImporter.jsonLinesToDb(file, collection)
        case JsonListFile(c, file) => JsonImporter.jsonFileListToDb(file, collection)
        case JsonFilesAreChildren(c, file) => JsonImporter.insertFilesInFolder(file, collection)
        case _ => //nothing for now
      }
      debug("After --> seed: " + collection.name + " " + collection.count())
    })
  }

  /**
   * provide a db for a fn so it can do some work, then close the connection.
   * @param list
   * @param fn
   * @tparam T
   */
  private def withDb[T](list: List[T], fn: (MongoDB, T) => Unit): Unit = list.foreach(fn(db, _))

  private def getSeedFormat(f: File): Option[SeedFormat] = {
    if (f.isDirectory) {
      getSingleList(f) match {
        case Some(listFile) => Some(JsonListFile(f.getName, listFile))
        case _ => Some(JsonFilesAreChildren(f.getName, f))
      }
    } else {
      if (f.getName.endsWith(".json")) Some(JsonOnEachLine(f.getName.replace(".json", ""), f)) else None
    }
  }

  private def getSingleList(directory: File): Option[File] = {

    val listFile = for (f <- directory.listFiles; if (f.getName == "list.json")) yield f

    if (listFile.length != 1) {
      None
    } else {
      Some(listFile(0))
    }
  }
}

object MongoDbSeeder extends ConsoleLogger {

  var logLevel: Level.Value = Level.ERROR

  override def mainLevel = logLevel

  case class SeedList(path: String, formats: Seq[SeedFormat])

  def seed(uri: String, paths: List[String], clear:Boolean): Unit = {
    info("seed - uri: " + uri)
    run(uri) { db =>
      val seeder = new Seeder(db, logLevel)
      seeder.seed(paths,clear)
    }
  }

  def unseed(uri: String, paths: List[String]): Unit = {
    info("unseed - uri: " + uri)
    run(uri) { db =>
      val seeder = new Seeder(db, logLevel)
      seeder.unseed(paths)
    }
  }

  private def run(uri: String)(fn: (MongoDB => Unit)): Unit = {
    connect(uri).map {
      (tuple) =>
        val (connection, db) = tuple
        fn(db)
        connection.underlying.close()
    }.getOrElse(warn("No db found!"))
  }

  private def connect(uri: String): Option[(MongoConnection, MongoDB)] = {
    try {
      val mongoUri: MongoURI = MongoURI(uri)
      mongoUri.database.map { n =>
        val connection: MongoConnection = MongoConnection(mongoUri)
        (connection, connection(n))
      }
    } catch {
      case e: Throwable => {
        error("Error: " + e.getMessage)
        None
      }
    }
  }
}
