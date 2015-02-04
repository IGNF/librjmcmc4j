package fr.ign.geometry;

public class Segment2DIterator {
  Segment2D segment;
  int[] step;
  int[] p;
  int[] q;
  double[] NextCrossingT;
  double[] DeltaT;
  int i;
  double t;

  public Segment2DIterator(Segment2D s) {
    this.t = 0;
    this.step = new int[2];
    this.p = new int[2];
    this.q = new int[2];
    this.NextCrossingT = new double[2];
    this.DeltaT = new double[2];
    for (int j = 0; j < 2; ++j) {
      this.p[j] = (int) Math.floor(s.getStart().getOrdinate(j));
      this.q[j] = (int) Math.floor(s.getEnd().getOrdinate(j));
      if (this.p[j] == this.q[j]) {
//        System.out.println("p["+j+"]="+p[j] + " = q["+j+"]="+q[j]);
        this.NextCrossingT[j] = 2;
        this.step[j] = 0;
        this.DeltaT[j] = 0;
        continue;
      }
//      System.out.println("p["+j+"]="+p[j] + " <> q["+j+"]="+q[j]);
      int dir = (this.q[j] > this.p[j]) ? 1 : 0;
      double invlength = 1.0 / (s.getEnd().getOrdinate(j) - s.getStart().getOrdinate(j));
      this.NextCrossingT[j] = (this.p[j] + dir - (s.getStart().getOrdinate(j))) * invlength;
      this.step[j] = 2 * dir - 1;
      this.DeltaT[j] = this.step[j] * invlength;
    }
    this.i = (this.NextCrossingT[1] < this.NextCrossingT[0]) ? 1 : 0;
    if (this.p[i] == this.q[i])
      this.NextCrossingT[i] = 1;
    if (this.NextCrossingT[i] <= 0)
      this.next();
//    System.out.println("i = " + i);
//    System.out.println("NextCrossingT[i] = " + this.NextCrossingT[i]);
//    System.out.println("step = " + this.step[0]+", "+this.step[1]);
  }

  public boolean begin() {
    return this.t == 0;
  }
  public boolean end() {
    return this.t == 1;
  }
  public int x() {
    return this.p[0];
  }

  public int y() {
    return this.p[1];
  }
  public double length() {
    return this.NextCrossingT[i]-t;
  }
  public int axis() {
    return this.i;
  }
  public double step(int k) {
    return this.step[k];
  }

  public Segment2DIterator next() {
    this.t = this.NextCrossingT[i];
    this.p[i] += this.step[i];
    this.NextCrossingT[i] += this.DeltaT[i];
    this.i = (this.NextCrossingT[1] < this.NextCrossingT[0]) ? 1 : 0;
    if (this.p[i] == this.q[i])
      this.NextCrossingT[i] = 1;
    return this;
  }
}
