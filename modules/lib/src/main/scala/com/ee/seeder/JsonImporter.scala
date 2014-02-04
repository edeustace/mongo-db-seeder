package com.ee.seeder

import com.mongodb.casbah.MongoCollection
import com.mongodb.casbah.Imports.MongoDBObject
import scala.io.Codec
import java.nio.charset.Charset
import com.mongodb.DBObject
import com.mongodb.util.JSON
import java.io.File
import org.bson.types.ObjectId
import com.ee.seeder.log.ConsoleLogger

object JsonImporter extends ConsoleLogger {

  override def mainLevel = MongoDbSeeder.logLevel

  /** Insert each line of the file as a single object
   * @param json
   * @param coll
   */
  def jsonLinesToDb(json: File, coll: MongoCollection) {

    val lines: Iterator[String] = io.Source.fromFile(json)(new Codec(Charset.forName("UTF-8"))).getLines()
    for (line <- lines) {
      if (line != null && line != "") {
        insertString(line, coll)
      }
    }
  }

  /** Read a complete file as a single json object
   * @param jsonPath
   * @param coll
   */
  def jsonFileToDb(jsonPath: String, coll: MongoCollection ) {
    val s = io.Source.fromFile(new File(jsonPath))(new Codec(Charset.forName("UTF-8"))).mkString
    coll.insert(JSON.parse(s).asInstanceOf[DBObject])
  }

  def jsonFileToItem(jsonPath: String, coll: MongoCollection) {
    val s = io.Source.fromFile(new File(jsonPath))(new Codec(Charset.forName("UTF-8"))).mkString
    val finalObject: String = replaceLinksWithContent(s)
    insertString(finalObject, coll, true)
  }

  /** Insert a json file that is a json array of items into the db
   * @param json = the json file
   * @param coll
   */
  def jsonFileListToDb(json:File, coll: MongoCollection) {
    coll.drop()
    val listString = io.Source.fromFile(json)(new Codec(Charset.forName("UTF-8"))).mkString
    val dbList = com.mongodb.util.JSON.parse(listString).asInstanceOf[com.mongodb.BasicDBList]
    dbList.toArray.toList.foreach(dbo => coll.insert(dbo.asInstanceOf[DBObject]))
  }

  /**
   * replace any interpolated keys with the path that they point to eg:
   * $[interpolate{/path/to/file.xml}] will be replaced with the contents of file.xml
   * @param s
   * @return
   */
  def replaceLinksWithContent(s: String): String = {

    /**
     * Load a string from a given path.
     * If it contains an interpolation token load the file from the given path,
     * remove new lines and escape "
     * @param path
     * @return
     */
    def loadString(path: String): String = {

      val root = new File(".")

      val s = io.Source.fromFile(new File(path))(new Codec(Charset.forName("UTF-8"))).mkString
      val lines = s
        .replace("\\", "\\\\\\\\")
        .replace("\n", "\\\n")
        .replace("\"", "'")
        .replace("$", "\\\\\\$")
        .replace("{", "\\\\{")
        .replace("}", "\\\\}")
      lines

    }

    val interpolated = StringUtils.interpolate(s, loadString)
    interpolated
  }

  private def insertString(s: String, coll: MongoCollection, printId: Boolean = false) = {
    val dbo: DBObject = JSON.parse(s).asInstanceOf[DBObject]

    val NO_ID = MongoDBObject.empty
    val id = if (dbo.get("_id") != null) dbo.get("_id") else NO_ID

    if (NO_ID.equals(id)) {
      debug("No id specified - just insert")
      coll.insert(dbo, coll.writeConcern)
    } else {
      coll.findOne( MongoDBObject("_id" -> id) ) match {
        case Some(obj) => {
          debug( Console.YELLOW + "Warning: " + Console.RESET + " document with this id already exists: " + id + " collection: " + coll.name)
        }
        case _ => {
          debug("id specified - insert: " + id)
          coll.insert(dbo, coll.writeConcern)
        }
      }
    }
  }



  def insertFilesInFolder(folder : File, collection : MongoCollection) {
    for (file <- folder.listFiles) {
      jsonFileToItem(folder.getAbsolutePath + "/" + file.getName, collection)
    }
  }


}

