package com.ee.seeder

import org.specs2.mutable.Specification
import com.mongodb.casbah.{MongoDB, MongoURI, MongoConnection, MongoCollection}
import com.mongodb.BasicDBObject


class MongoDbSeederSpec extends Specification {

  val rootPath = "modules/lib/"
  val uri = "mongodb://127.0.0.1:27017/mongo-db-seeder-specs"
  val mongoUri: MongoURI = MongoURI(uri)
  var connection: MongoConnection = null
  var db: MongoDB = null
  var contents: MongoCollection = null

  def openDb() {

    println("----- open db")
    if (connection == null) {
      connection = MongoConnection(mongoUri)
    }

    if (db == null) {
      db = connection("mongo-db-seeder-specs")
    }

    if (contents == null) {
      contents = db("content")
    }
  }

  def closeDb() {
    println("----- close db")
    if (db != null) {
      db.dropDatabase()
    }

    if (connection != null) {
      connection.close()
    }

    db = null
    connection = null
  }

  def resetContents() {
    println("----- reset contents")

    if (contents != null) {
      contents.drop()
    }
  }

  step(openDb())

  "seeder" should {

    "seed collections all collection names" in {

      MongoDbSeeder.emptyDb(uri,
        List(
          rootPath + "src/test/resources/multiple-collections",
          rootPath + "src/test/resources/multiple-collections-two"
        ))
    }

    "seed from folder with one file with json object on each line" in {
      MongoDbSeeder.seed(uri, List( rootPath + "src/test/resources/each-line"))
      Thread.sleep(1000)
      val count = contents.count(new BasicDBObject())
      count must equalTo(2)
      val items = contents.find(new BasicDBObject())
      val itemsList = items.toList
      itemsList(0).get("name") must equalTo("ed")
    }

    step(resetContents())

    "seed from list.json" in {
      MongoDbSeeder.seed(uri, List( rootPath + "src/test/resources/list-json"))
      Thread.sleep(1000)
      val count = contents.count(new BasicDBObject())
      count must equalTo(3)
    }

    step(resetContents())
    "seed individual files" in {
      MongoDbSeeder.seed(uri, List( rootPath + "src/test/resources/individual-files"))
      Thread.sleep(1000)
      val count = contents.count(new BasicDBObject())
      count must equalTo(4)
    }

    step(closeDb())
  }

}

