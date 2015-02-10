package fr.ign.geometry;

public class IntersectionArea {
	Primitive a;
	Primitive b;

	public IntersectionArea(Primitive a, Primitive b) {
		this.a = a;
		this.b = b;
	}

	public static double circular_triangle_area(double x0, double y0, double x1, double y1, double R) {
		double mx = 0.5 * (x0 + x1);
		double my = 0.5 * (y0 + y1);
		double R2 = R * R;
		double r2 = mx * mx + my * my;
		double r = Math.sqrt(r2);
		return (x1 - x0) * (y1 - y0) * 0.5 + R2 * Math.acos(r / R) - r * Math.sqrt(R2 - r2);
	}

	public static boolean intersection_area_rectangle_circle_aux(double area, boolean out00, boolean out01, boolean out11, boolean out10, double x0,
			double y0, double x1, double y1, double rn, double x02, double y02, double x12, double y12, double rn2) {
		if (!(out11 && x1 > 0 && y1 > 0))
			return false;
		if (out00 && x0 > 0 && y0 > 0) {
			area = 0;
			return true;
		}
		if (out01 && x0 > 0 && y1 > 0) {
			if (out10 && x1 > 0 && y0 > 0) {
				double xr0 = Math.sqrt(rn2 - y02);
				double yr0 = Math.sqrt(rn2 - x02);
				area = circular_triangle_area(x0, y0, xr0, yr0, rn);
				return true;
			} else {
				double yr0 = Math.sqrt(rn2 - x02);
				double yr1 = Math.sqrt(rn2 - x12);
				area += circular_triangle_area(x0, yr1, x0, yr0, rn) - (x1 - x0) * (y1 - yr1);
			}
		} else {
			if (out10 && x1 > 0 && y0 > 0) {
				double xr0 = Math.sqrt(rn2 - y02);
				double xr1 = Math.sqrt(rn2 - y12);
				area += circular_triangle_area(xr1, y0, xr0, y0, rn) - (y1 - y0) * (x1 - xr1);
			} else {
				double xr1 = Math.sqrt(rn2 - y12);
				double yr1 = Math.sqrt(rn2 - x12);
				area += circular_triangle_area(xr1, yr1, x1, y1, rn) - (x1 - xr1) * (y1 - yr1);
			}
		}
		return false;
	}

	public static double intersection_area(Rectangle2D r, Circle2D c) {
		if (r.is_degenerate() || c.is_degenerate())
			return 0.;
		Vector2D v = new Vector2D(r.center().minus(c.center()));
		double n2 = r.normal_squared_length();
		double vn = v.dotProduct(r.normal());
		double rn = Math.sqrt(c.squared_radius() * n2);
		double x0 = Math.max(vn - n2, -rn);
		double x1 = Math.min(vn + n2, rn);
		double dx = x1 - x0;
		if (dx <= 0)
			return 0.;
		Vector2D m = new Vector2D(-r.normal().y(), r.normal().x());
		double m2 = Math.abs(r.ratio()) * n2;
		double vm = v.dotProduct(m);
		double y0 = Math.max(vm - m2, -rn);
		double y1 = Math.min(vm + m2, rn);
		double dy = y1 - y0;
		if (dy <= 0)
			return 0.;
		double x02 = x0 * x0;
		double y02 = y0 * y0;
		double x12 = x1 * x1;
		double y12 = y1 * y1;
		double rn2 = rn * rn;
		boolean out00 = (x02 + y02 >= rn2);
		boolean out01 = (x02 + y12 >= rn2);
		boolean out11 = (x12 + y12 >= rn2);
		boolean out10 = (x12 + y02 >= rn2);
		double area = dx * dy;
		if (intersection_area_rectangle_circle_aux(area, out00, out01, out11, out10, x0, y0, x1, y1, rn, x02, y02, x12, y12, rn2))
			return area / n2;
		if (intersection_area_rectangle_circle_aux(area, out01, out11, out10, out00, -y1, x0, -y0, x1, rn, y12, x02, y02, x12, rn2))
			return area / n2;
		if (intersection_area_rectangle_circle_aux(area, out11, out10, out00, out01, -x1, -y1, -x0, -y0, rn, x12, y12, x02, y02, rn2))
			return area / n2;
		if (intersection_area_rectangle_circle_aux(area, out10, out00, out01, out11, y0, -x1, y1, -x0, rn, y02, x12, y12, x02, rn2))
			return area / n2;
		return area / n2;
	}

	public static double intersection_area(Circle2D c, Rectangle2D r) {
		return intersection_area(r, c);
	}

	public static boolean do_intersect(Rectangle2D r, Circle2D c) {
		if (r.is_degenerate() || c.is_degenerate())
			return false;
		Vector2D v = new Vector2D(r.center().minus(c.center()));
		double n2 = r.normal_squared_length();
		double vn = v.dotProduct(r.normal());
		double rn = Math.sqrt(c.squared_radius() * n2);
		double x0 = Math.max(vn - n2, -rn);
		double x1 = Math.min(vn + n2, rn);
		double dx = x1 - x0;
		if (dx <= 0)
			return false;
		Vector2D m = new Vector2D(-r.normal().y(), r.normal().x());
		double m2 = Math.abs(r.ratio()) * n2;
		double vm = v.dotProduct(m);
		double y0 = Math.max(vm - m2, -rn);
		double y1 = Math.min(vm + m2, rn);
		double dy = y1 - y0;
		if (dy <= 0)
			return false;
		double rn2 = rn * rn;
		double x02 = x0 * x0;
		double y02 = y0 * y0;
		if (x02 + y02 >= rn2 && x0 > 0 && y0 > 0)
			return false;
		double y12 = y1 * y1;
		if (x02 + y12 >= rn2 && x0 > 0 && y1 < 0)
			return false;
		double x12 = x1 * x1;
		if (x12 + y12 >= rn2 && x1 < 0 && y1 < 0)
			return false;
		if (x12 + y02 >= rn2 && x1 < 0 && y0 > 0)
			return false;
		return true;
	}

	public static boolean do_intersect(Circle2D c, Circle2D d) {
		Vector2D v = new Vector2D(d.center().minus(c.center()));
		double v2 = v.squared_length();
		return (v2 < d.squared_radius() + c.squared_radius() + 2 * c.radius() * d.radius());
	}

	// Tout vient de :
	// Weisstein, Eric W. "Circle-Circle Intersection." From MathWorld--A
	// Wolfram Web Resource.
	// http://mathworld.wolfram.com/Circle-CircleIntersection.html
	public static double intersection_area(Circle2D c0, Circle2D c1) {
		Vector2D diff = new Vector2D(c1.center().minus(c0.center()));
		double d2 = diff.squared_length();
		double r0 = c0.radius();
		double r02 = c0.squared_radius();
		double r1 = c1.radius();
		double r12 = c1.squared_radius();
		double a = d2 - r02 - r12;
		double b = 2 * r0 * r1;
		if (a > b) { // d²>(r0+r1)²
			return 0.;
		} else if (-a > b) { // d²<(r0-r1)²
			return Math.PI * Math.min(r02, r12);
		}
		double d = Math.sqrt(d2);
		double area = r02 * Math.acos((d2 + r02 - r12) / (2. * d * r0)) + r12 * Math.acos((d2 + r12 - r02) / (2. * d * r1)) - 0.5
				* Math.sqrt((-d + r0 + r1) * (d + r0 - r1) * (d - r0 + r1) * (d + r0 + r1));
		return area;
	}

	public static double triangle_area(double n2, double m2, double tx, double ty, double rx, double ry) {
		if (tx >= n2 || ry >= m2)
			return 0; // no intersection, above
		if (tx < -n2) {
			ty = ty + (tx + n2) * (ry - ty) / (tx - rx);
			tx = -n2;
		} // crop left
		if (ry < -m2) {
			rx = rx + (ry + m2) * (rx - tx) / (ty - ry);
			ry = -m2;
		} // crop bottom
		if (ty < ry)
			return 0; // no intersection, underneath
		if (ty <= m2) {
			if (rx <= n2)
				return (ty - ry) * (rx - tx) / 2; // all inside
			return (1 + (rx - n2) / (rx - tx)) * (ty - ry) * (n2 - tx) / 2; // cut right
		}
		if (rx <= n2)
			return (1 + (ty - m2) / (ty - ry)) * (rx - tx) * (m2 - ry) / 2; // cut
																			// top
		double mx = tx + (m2 - ty) * (rx - tx) / (ry - ty);
		if (mx >= n2)
			return (n2 - tx) * (m2 - ry); // rectangle
		double ny = ty + (tx - n2) * (ry - ty) / (tx - rx);
		return (mx - tx) * (m2 - ry) + ((m2 + ny) / 2 - ry) * (n2 - mx); // rectangle
																			// with
																			// the
																			// upper
		// right corner cut
	}

	enum Sign {
		NEGATIVE, POSITIVE, ZERO;
	}

	public static Sign sign(double t) {
		return (t < 0) ? Sign.NEGATIVE : ((t > 0) ? Sign.POSITIVE : Sign.ZERO);
	}

	public static double intersection_area(Rectangle2D a, Rectangle2D b) {
		 if(a.is_degenerate() || b.is_degenerate()) return 0;
		Vector2D v = new Vector2D(b.centerx - a.centerx, b.centery - a.centery);
		Vector2D m = new Vector2D(-a.normaly, a.normalx);
		Vector2D na = new Vector2D(a.normalx, a.normaly);
		Vector2D nb = new Vector2D(b.normalx, b.normaly);
		double n2 = na.getNormSq();
		double m2 = Math.abs(a.ratio) * n2;
		double br = Math.abs(b.ratio);
		double cx = na.dotProduct(v);
		double cy = m.dotProduct(v);
		double nx = na.dotProduct(nb);
		double ny = m.dotProduct(nb);
		switch (sign(nx)) {
		case ZERO: { // m and b.normal() are collinear
			ny = Math.abs(ny);
			double mx = br * ny;
			double lx = cx - mx;
			double rx = cx + mx;
			double by = cy - ny;
			double ty = cy + ny;
			if (rx <= -n2 || n2 <= lx || ty <= -m2 || m2 <= by)
				return 0;
			return (Math.min(n2, rx) - Math.max(-n2, lx)) * (Math.min(m2, ty) - Math.max(-m2, by)) / n2;
		}
		case NEGATIVE: // b.normal() =rotate180(b.normal())
		{
			nx = -nx;
			ny = -ny;
		}
		default:
			;
		}
		double mx = -br * ny;
		double my = br * nx;
		switch (sign(ny)) {
		case ZERO: { // n and b.normal() are collinear
			double lx = cx - nx;
			double rx = cx + nx;
			double by = cy - my;
			double ty = cy + my;
			if (rx <= -n2 || n2 <= lx || ty <= -m2 || m2 <= by)
				return 0;
			return (Math.min(n2, rx) - Math.max(-n2, lx)) * (Math.min(m2, ty) - Math.max(-m2, by)) / n2;
		}
		case NEGATIVE: // b.normal() =rotate90(b.normal())
			nx = -nx;
			// std::swap(nx,mx);
			double tmp = nx;
			nx = mx;
			mx = tmp;
			ny = -ny;
			// std::swap(ny,my);
			tmp = ny;
			ny = my;
			my = tmp;
		default: { // nx>0, ny>0, mx<0 and my>0 case
			double sumx = (nx + mx);
			double sumy = ny + my;
			double difx = nx - mx;
			double dify = ny - my;
			double x[] = { cx - sumx, cx + difx, cx + sumx, cx - difx }; // bottom,
																			// right,
																			// top,
																			// left
			double y[] = { cy - sumy, cy + dify, cy + sumy, cy - dify };
			if (y[0] >= m2 || y[2] <= -m2 || x[3] >= n2 || x[1] <= -n2)
				return 0; // one edge of "this" separates the 2 rectangles
			double area = triangle_area(n2, m2, x[2], y[2], x[1], y[1]) + triangle_area(m2, n2, y[3], -x[3], y[2], -x[2])
					+ triangle_area(n2, m2, -x[0], -y[0], -x[3], -y[3]) + triangle_area(m2, n2, -y[1], x[1], -y[0], x[0]);
			// iso-rectangle area
			double lx = x[0], by = y[1];
			double rx = x[2], ty = y[3];
			double s = 1;
			if (lx > rx) {
				// std::swap(lx,rx);
				tmp = lx;
				lx = rx;
				rx = tmp;
				s = -s;
			}
			if (by > ty) {
				// std::swap(by,ty);
				tmp = by;
				by = ty;
				ty = tmp;
				s = -s;
			}
			if (by >= m2 || ty <= -m2 || lx >= n2 || rx <= -n2) {
				return area / n2;
			}
			return (area + s * (Math.min(n2, rx) - Math.max(-n2, lx)) * (Math.min(m2, ty) - Math.max(-m2, by))) / n2;
		}
		}
	}

	public static boolean do_intersect(Rectangle2D a, Rectangle2D b) {
		Vector2D an = new Vector2D(a.normalx, a.normaly);
		Vector2D bn = new Vector2D(b.normalx, b.normaly);
		Vector2D v = new Vector2D(b.centerx - a.centerx, b.centery - a.centery);

		double det = Math.abs(an.x() * bn.y() - an.y() * bn.x());
		double dot = Math.abs(an.dotProduct(bn));

		double an2 = an.getNormSq();
		double br = Math.abs(b.ratio);
		double ax0 = an.dotProduct(v);
		double dax = dot + br * det + an2;
		if (ax0 * ax0 >= dax * dax)
			return false;

		double ar = Math.abs(a.ratio);
		double ay0 = an.x() * v.y() - an.y() * v.x();
		double day = det + br * dot + ar * an2;
		if (ay0 * ay0 >= day * day)
			return false;

		double bn2 = bn.getNormSq();
		double bx0 = bn.dotProduct(v);
		double dbx = dot + ar * det + bn2;
		if (bx0 * bx0 >= dbx * dbx)
			return false;

		double by0 = v.x() * bn.y() - v.y() * bn.x();
		double dby = det + ar * dot + br * bn2;
		return (by0 * by0 < dby * dby);
	}

	public double getArea() {
		if (a instanceof Rectangle2D && b instanceof Circle2D) {
			return intersection_area((Rectangle2D) a, (Circle2D) b);
		}
		if (a instanceof Circle2D && b instanceof Rectangle2D) {
			return intersection_area((Circle2D) a, (Rectangle2D) b);
		}
		if (a instanceof Circle2D && b instanceof Circle2D) {
			return intersection_area((Circle2D) a, (Circle2D) b);
		}
		return intersection_area((Rectangle2D) a, (Rectangle2D) b);
	}
}
