package com.ee.seeder

import java.io.File
import scala.Some
import com.ee.seeder.model._
import com.mongodb.casbah.{MongoDB, MongoURI, MongoConnection, MongoCollection}

object MongoDbSeeder {


  case class SeedList(path: String, formats: Seq[SeedFormat])


  def emptyDb(uri:String, paths:List[String]) {
    val seedLists = paths.map(buildSeedList)
    val collectionNames = seedLists.map( _.formats.map(_.collection))
    val stripped = collectionNames.flatten.distinct
    withDb(uri, stripped, (db : MongoDB, name : String) => db(name).drop())
  }

  def seed(uri:String, paths:List[String]) {
    
    emptyDb(uri, paths)

    paths.foreach( seedPath(uri, _))
  }

  private def buildSeedList(path: String): SeedList = {
    val folder: File = new File(path)

    if(folder.exists && folder.isDirectory){
      val files = for (file <- folder.listFiles()) yield file
      SeedList(path = path, formats = files.flatMap(getSeedFormat).toList)
    } else {
      println("ignored path: " + path)
      SeedList(path, Seq())
    } 
  }

  private def seedPath(uri: String, path: String) {

    val seedList = buildSeedList(path)

    withDb(uri, seedList.formats.toList, (db : MongoDB, f : SeedFormat) => {
      val collection: MongoCollection = db(f.collection)

      f match {
        case JsonOnEachLine(c, file) => JsonImporter.jsonLinesToDb(file, collection)
        case JsonListFile(c, file) => JsonImporter.jsonFileListToDb(file, collection)
        case JsonFilesAreChildren(c, file) => JsonImporter.insertFilesInFolder(file, collection)
        case _ => //nothing for now
      }
    })
  }

  /**
   * provide a db for a fn so it can do some work, then close the connection.
   * @param uri
   * @param list
   * @param fn
   * @tparam T
   */
  private def withDb[T]( uri : String, list : List[T], fn : (MongoDB, T)  => Unit ) {
    val mongoUri : MongoURI = MongoURI(uri)

    mongoUri.database match {
      case Some(dbName) => {
        val connection: MongoConnection = MongoConnection(mongoUri)
        val db = connection(dbName)
        list.foreach(fn(db,_))
        connection.close()
      }
      case _ => //skip for now.
    }
  }

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
