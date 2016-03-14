package geotrellis.proj4

object WebMercator extends CRS {
  lazy val proj4jCrs = factory.createFromName("EPSG:3857")

  def epsgCode: Option[Int] = CRS.getEPSGCode(toProj4String + " <>")
}
