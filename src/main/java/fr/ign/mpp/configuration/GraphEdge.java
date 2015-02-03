package fr.ign.mpp.configuration;

import org.jgrapht.graph.DefaultWeightedEdge;

public class GraphEdge extends DefaultWeightedEdge {
  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  public GraphEdge() {
  }
//
//  @Override
//  public String toString() {
//    String result = "";
//    GraphConfiguration<Rectangle2D>.GraphVertex source = (GraphConfiguration<Rectangle2D>.GraphVertex) this.getSource();
//    GraphConfiguration<Rectangle2D>.GraphVertex target = (GraphConfiguration<Rectangle2D>.GraphVertex) this.getTarget();
//    Rectangle2D rSource = (Rectangle2D) source.value;
//    Rectangle2D rTarget = (Rectangle2D) target.value;
//    result += new GeometryFactory().createLineString(new Coordinate[] {
//        new Coordinate(rSource.centerx, -rSource.centery),
//        new Coordinate(rTarget.centerx, -rTarget.centery) });
//    return result;
//  }
};
