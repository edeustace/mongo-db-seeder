import sbt._
import Keys._
import com.ee.seeder._

object MyPlugin extends Plugin
{
  override lazy val settings = Seq(commands += myCommand)

  lazy val emptyDb =
    Command.command("empty-db") { (state: State) =>

      MongoDbSeeder.emptyDb
      state
    }
}