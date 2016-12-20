package geotrellis.proj4.io.wkt

import scala.io.Source

object WKTRegistry {

  private var espgWkt: scala.collection.mutable.Set[Tree] = scala.collection.mutable.Set.empty

  private val wktResourcePath = "/geotrellis/proj4/wkt/epsg.properties"

  private def withWktFile[T](f: Iterator[String] => T) = {
    val stream = getClass.getResourceAsStream(wktResourcePath)
    try {
      f(Source.fromInputStream(stream).getLines())
    } finally {
      stream.close()
    }
  }

  def parseWktEpsgResource() = {
    //read input from epsg.properties file
    withWktFile { lines =>
      for (line <- lines) {
        //split the line for parsing the wkt, aka remove code infront
        val firstEquals = line.indexOf("=")
        val wktString = line.substring(firstEquals + 1)
        //parse the wkt string
        espgWkt += WKTParser(wktString)
      }
    }
  }

  def containsObject(input: Tree): Boolean = {
    espgWkt contains input
  }

}
