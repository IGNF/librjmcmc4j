package fr.ign.geometry;

public class Point2D {

	double x;
	double y;

	public Point2D(double x, double y) {
		this.x = x;
		this.y = y;
	}

	public double x() {
		return this.x;
	}

	public double y() {
		return this.y;
	}

	public Point2D minus(Point2D m) {
		return new Point2D(this.x - m.x(), this.y - m.y());
	}

	public Point2D minus(Vector2D m) {
		return new Point2D(this.x - m.x(), this.y - m.y());
	}

	public Point2D minus(double mx, double my) {
		return new Point2D(this.x - mx, this.y - my);
	}

	public Point2D plus(Vector2D m) {
		return new Point2D(this.x + m.x(), this.y + m.y());
	}

	public Point2D plus(double mx, double my) {
		return new Point2D(this.x + mx, this.y + my);
	}

	public Vector2D subtract(Point2D p) {
		return new Vector2D(this.x - p.x, this.y - p.y);
	}

	public double getOrdinate(int i) {
		return i == 0 ? this.x : this.y;
	}

}
