package net.hatran;


import java.awt.Graphics;
import java.util.ArrayList;
import java.util.List;

// 坐标点
public class Point3D {

    double X;
    double Y;
    double Z;

    List<DirectedEdge> edges = new ArrayList<DirectedEdge>();

    public Point3D()
    {

    }

    public Point3D(PositionInSpace position_in_space)
    {
        this.X = position_in_space.getX();
        this.Y = position_in_space.getY();
        this.Z = this.X*this.X + this.Y*this.Y;
    }

    public void setPoint3D(Point3D p)
    {
        this.X = p.X;
        this.Y = p.Y;
        this.Z = p.Z;
    }

    public boolean equal(Point3D p)
    {
        return (this.X==p.X && this.Y==p.Y);
    }

    public void paint(Graphics g, WTrsf t, double height) {
        // 绘制坐标点（绘制椭圆的轮廓：结果是在x、y、宽度和高度参数指定的矩形内形成一个圆圈或椭圆）
//		g.drawOval(t.x(X), t.y(Y), 4, 4);
        g.fillOval(t.x(X), t.y(Y, height), 4, 4);
        // 标出坐标值
//		g.drawString ("(" + String.valueOf(X) + "," + String.valueOf(Y) + ")", t.x(X), t.y(Y));
    }

    public void display()
    {
        System.out.println("(" + this.X + ", " + this.Y + ")");
    }

    public void displayPoint3D()
    {
        System.out.println("(" + this.X + ", " + this.Y + ", " + this.Z + ")");
        for(DirectedEdge e: edges)
        {
            e.displayDirectedEdge();
        }
    }
}

