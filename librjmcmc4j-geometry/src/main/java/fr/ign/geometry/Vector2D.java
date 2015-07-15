package fr.ign.geometry;

public class Vector2D {
	double x;
	double y;

	public double x() {
		return this.x;
	}

	public double y() {
		return this.y;
	}

	public Vector2D(Point2D a, Point2D b) {
		this.x = b.x - a.x;
		this.y = b.y - a.y;
	}

	public Vector2D(Vector2D v) {
		this.x = v.x;
		this.y = v.y;
	}

	public Vector2D(double x, double y) {
		this.x = x;
		this.y = y;
	}

	public Vector2D(Point2D v) {
		this.x = v.x;
		this.y = v.y;
	}

	public double getNormSq() {
		return this.x * this.x + this.y * this.y;
	}

	public Vector2D subtract(final Vector2D p) {
		return new Vector2D(this.x - p.x, this.y - p.y);
	}

	public Vector2D add(final Vector2D p) {
		return new Vector2D(this.x + p.x, this.y + p.y);
	}

	public double dotProduct(final Vector2D v) {
		return this.x * v.x + this.y * v.y;
	}

	public double crossProduct(final Vector2D v) {
    return this.x * v.y - this.y * v.x;
  }

	public Vector2D scalarMultiply(double d) {
		return new Vector2D(d * this.x, d * this.y);
	}

	public Vector2D negate() {
		return new Vector2D(-this.x, -this.y);
	}

	public double getOrdinate(int i) {
		return i == 0 ? this.x : this.y;
	}

	public double squared_length() {
		return this.getNormSq();
	}
}
