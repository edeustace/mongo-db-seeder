package com.ee.seeder.log

import com.ee.seeder.log.ConsoleLogger.Level

object ConsoleLogger{
  object Level extends Enumeration{
    type Level = Value
    val OFF,ERROR,WARN,INFO,DEBUG = Value

    import Console._
    private val colors = Seq("", RED, YELLOW, BLUE, GREEN)
    def color(l:Level):String = colors(l.id)
  }
}

trait ConsoleLogger {
  def mainLevel : Level.Value = Level.OFF

  def prefix : String = "mongo-db-seeder"

  def error(msg : => String) : Unit = msgOut(Level.ERROR, msg)
  def warn(msg : => String) : Unit = msgOut(Level.WARN, msg)
  def debug(msg : => String) : Unit =  msgOut(Level.DEBUG, msg)
  def info(msg : => String) : Unit =  msgOut(Level.INFO, msg)

  def msgOut(level : Level.Level, msg : => String) : Unit = {
    if(level.id <= mainLevel.id ){
      val open = wrap(Console.CYAN, "[")
      val close = wrap(Console.CYAN, "]")
      Console.println( open + prefix + close + " " + wrap(Level.color(level), level.toString) + " " + msg )
    }
  }

  private def wrap(color:String, msg: => String) : String = color + msg + Console.RESET
}
