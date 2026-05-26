package fr.ign.geometry;

public class Square2D extends Rectangle2D {

    public Square2D(double cx, double cy, double nx, double ny) {
        super(cx, cy, nx, ny, 1);
    }

    @Override
    public int size() {
        return 4;
    }  
}
