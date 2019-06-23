package net.hatran;
public class WTrsf {
    public double magx = 1;
    public double magy = 1;
    public double xoff;
    public double yoff;

    public WTrsf(double amagx, double amagy, double anxoff, double anyoff) {

        magx = amagx;
        magy = amagy;
        xoff = anxoff;
        yoff = anyoff;

    }

    public int x(double xv) {

//		System.out.println("xoff:" + xoff);
//		return (int) (20 + magx * xv + xoff);
        return (int) (20 + magx * (xv - xoff));

    }

    public int y(double yv, double height) {

//		System.out.println("yoff:" + yoff);
//		return (int) (height - 10 - magy * yv + yoff);
        return (int) (height - 10 - magy * (yv - yoff));
    }

//	public int r(double rv) {
//
//		return (int) (mag * rv);
//
//	}

    public double xi(int xv) {

        return (xv - xoff) / magx;

    }

    public double yi(int yv) {

        return (yv - yoff) / magy;

    }

//	public double ri(int rv) {
//
//		return rv / mag;
//
//	}

}

