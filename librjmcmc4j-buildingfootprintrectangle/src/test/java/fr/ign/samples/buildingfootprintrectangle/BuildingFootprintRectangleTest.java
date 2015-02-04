package fr.ign.samples.buildingfootprintrectangle;

import java.io.File;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class BuildingFootprintRectangleTest {

  BuildingFootprintRectangle bfr = null;

  @Before
  public void setUp() throws Exception {
    this.bfr = new BuildingFootprintRectangle(new File("./src/test/resources/ZTerrain.tif"), 1f,
        250, 1, 10, 20, 5);
  }

  @Test
  public void testGetEnergy() {
    double[] parameterArray = new double[] { 0.5, 0.5, 0, 0.1, 0.5 };
    double energy = this.bfr.getEnergy(parameterArray);
    Assert.assertEquals(-151.35137273073195, energy, 0.0001);
  }

  @Test
  public void testWriteToShapefile() {
    double[] parameterArray = new double[] { 0.5, 0.5, 0, 0.1, 0.5 };
    this.bfr.writeToShapefile(parameterArray, new File("./target/result.shp"));
    double[] result = this.bfr.readFromShapefile(new File("./target/result.shp"));
    Assert.assertArrayEquals(parameterArray, result, 0.00001);
  }

  @Test
  public void testReadFromShapefile() {
    double[] parameterArray = this.bfr.readFromShapefile(new File(
        "./src/test/resources/result_1000000_-69735,908100_56,507382_-69679,400718.shp"));
    // this.bfr.writeToShapefile(parameterArray, new File("./target/result_1000000.shp"));
    double energy = this.bfr.getEnergy(parameterArray);
    Assert.assertEquals(-69679.400718, energy, 0.0001);
    parameterArray = this.bfr.readFromShapefile(new File("./src/test/resources/building0.shp"));
    energy = this.bfr.getEnergy(parameterArray);
    System.out.println("e = " + energy);
  }

}
