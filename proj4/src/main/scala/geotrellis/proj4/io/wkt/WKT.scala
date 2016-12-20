package geotrellis.proj4.io.wkt

import geotrellis.proj4.Memoize
import org.osgeo.proj4j.NotFoundException

import scala.io.Source

object WKT {
  private val wktResourcePath = "/geotrellis/proj4/wkt/epsg.properties"
  lazy val parsed: Map[Int, WktCS] = records.mapValues(WKTParser.apply)
  lazy val projections: Set[WktCS] = parsed.values.toSet
  lazy val records: Map[Int, String] = parseWktEpsgResource

  def parseWktEpsgResource(): Map[Int, String] = {
    //read input from epsg.properties file
    WKT.withWktFile { lines =>
      println(lines)
      val iter =
        for (line <- lines) yield {
          //split the line for parsing the wkt, aka remove code infront
          val firstEquals = line.indexOf("=")
          val code = line.substring(0, firstEquals).toInt
          val wktString = line.substring(firstEquals + 1)
          code -> wktString
        }
      iter.toMap
    }
  }

  def contains(input: WktCS): Boolean = {
    projections contains input
  }

  def getEPSGCodeOption(input: String): Option[Int] = {
    val wktParsed = WKTParser(input)
    parsed.find{
      case (epsgCode, wkt) => wkt == wktParsed
    }.map(_._1)
  }

  def getEPSGStringOption(input: Int): Option[String] = {
    records.get(input).map(_.toString)
  }


  /**
   * Returns the WKT representation given an EPSG code in the format EPSG:[number]
   * @param code
   * @return
   */
  def fromEPSGCode(code: Int): String = getEPSGStringOption(code).get

  /**
   * Returns the numeric code of a WKT string given the authority
   * @param wktString
   * @return
   */
  def getEPSGCode(wktString: String): String = s"EPSG:${getEPSGCodeOption(wktString).get}"

  def withWktFile[T](f: Iterator[String] => T) = {
    val stream = getClass.getResourceAsStream(wktResourcePath)
    try {
      f(Source.fromInputStream(stream).getLines())
    } finally {
      stream.close()
    }
  }

}
