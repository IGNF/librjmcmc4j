package fr.ign.geometry;

import java.util.List;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.LinearRing;
import org.locationtech.jts.geom.Polygon;

import fr.ign.rjmcmc.kernel.SimpleObject;

public class Circle2D implements Primitive, SimpleObject {
	public double center_x;
	public double center_y;
	public double radius;

	public Circle2D(double cx, double cy, double r) {
		this.center_x = cx;
		this.center_y = cy;
		this.radius = r;
	}

	@Override
	public double[] toArray() {
		return new double[] { this.center_x, this.center_y, this.radius };
	}

	@Override
	public int size() {
		return 3;
	}

	@Override
	public void set(List<Double> list) {
		this.center_x = list.get(0);
		this.center_y = list.get(1);
		this.radius = list.get(2);
	}

	@Override
	public Object[] getArray() {
		return new Object[] { this.toGeometry() };
	}

	@Override
	public double intersectionArea(Primitive p) {
		IntersectionArea i = new IntersectionArea(this, p);
		return i.getArea();
	}

	@Override
	public Geometry toGeometry() {
		GeometryFactory geomFact = new GeometryFactory();
		int size = 100;
		double increment = Math.PI * 2. / size;
		Coordinate[] pts = new Coordinate[size + 1];
		for (int i = 0; i < size; i++) {
			double x = this.center_x + this.radius * Math.cos(i * increment);
			double y = this.center_y + this.radius * Math.sin(i * increment);
			pts[i] = new Coordinate(x, y);
		}
		pts[size] = pts[0];// repeat first point
		LinearRing ring = geomFact.createLinearRing(pts);
		Polygon poly = geomFact.createPolygon(ring, null);
		return poly;
	}

	public boolean is_degenerate() {
		return this.radius == 0.;
	}

	public Point2D center() {
		return new Point2D(this.center_x, this.center_y);
	}

	public double squared_radius() {
		return this.radius * this.radius;
	}

	public double radius() {
		return this.radius;
	}
	public double getArea() {
		return this.squared_radius() * Math.PI;
	}
}
