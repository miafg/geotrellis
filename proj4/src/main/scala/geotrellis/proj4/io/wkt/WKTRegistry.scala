package geotrellis.proj4.io.wkt

import scala.io.Source
import scala.util.parsing.combinator._
import WKTParser._

object WKTRegistry extends RegexParsers {
  private var currentSet: scala.collection.mutable.Set[Any] = scala.collection.mutable.Set.empty

  var currentSet: scala.collection.mutable.Set[Any] = scala.collection.mutable.Set.empty

  private val wktResourcePath = "/geotrellis/proj4/wkt/epsg.properties"

  private def withWktFile[T](f: Iterator[String] => T) = {
    val stream = getClass.getResourceAsStream(wktResourcePath)
    try {
      f(Source.fromInputStream(stream).getLines())
    } finally {
      stream.close()
    }
  }

  def createTree() = {
    var currentSet: scala.collection.mutable.Set[Any] = scala.collection.mutable.Set.empty
    //read input from epsg.properties file
    withWktFile { lines =>
      for (line <- lines) {
        //split the line for parsing the wkt, aka remove code infront
        val firstEquals = line.indexOf("=")
        val wktString = line.substring(firstEquals + 1)
        //parse the wkt string
        parseAll(parseWKT, wktString) match {
          case Success(wktObject, _) => {
            currentSet += wktObject
          }
          case Failure(msg, tail) => {
            val sb = new StringBuilder()
            sb.append(tail)
            println("FAILURE: " + msg + "\n")
          }
          case Error(msg, _) => println("ERROR: " + msg)
        }
      }
    }
  }

  def containsObject(input: Any): Boolean = {
    currentSet contains input
  }

}
