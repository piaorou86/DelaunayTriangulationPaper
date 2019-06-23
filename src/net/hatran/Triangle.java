package net.hatran;

import java.awt.Graphics;
import java.text.DecimalFormat;


// 有向三角形结构
public class Triangle {

    PositionInSpace Pt1;
    PositionInSpace Pt2;
    PositionInSpace Pt3;
    DirectedEdge edge1;
    DirectedEdge edge2;
    DirectedEdge edge3;

    public Triangle(PositionInSpace pt1, PositionInSpace pt2, PositionInSpace pt3, DirectedEdge edge1, DirectedEdge edge2, DirectedEdge edge3)
    {
        this.Pt1 = pt1;
        this.Pt2 = pt2;
        this.Pt3 = pt3;
        this.edge1 = edge1;
        this.edge2 = edge2;
        this.edge3 = edge3;
    }

    // 计算欧几里得距离
    public double EuclideanDistance(Point3D p, Point3D q)
    {
        return Math.sqrt((p.X-q.X)*(p.X-q.X)+(p.Y-q.Y)*(p.Y-q.Y));
    }

    // 画三角形
    public void paint(Graphics g, WTrsf t, double height) {

        // 画线
        g.drawLine(t.x(Pt1.getX()), t.y(Pt1.getY(), height), t.x(Pt2.getX()), t.y(Pt2.getY(), height));
        g.drawLine(t.x(Pt1.getX()), t.y(Pt1.getY(), height), t.x(Pt3.getX()), t.y(Pt3.getY(), height));
        g.drawLine(t.x(Pt3.getX()), t.y(Pt3.getY(), height), t.x(Pt2.getX()), t.y(Pt2.getY(), height));

        // 标出坐标值
//		g.drawString ("(" + String.valueOf(Pt1.X) + "," + String.valueOf(Pt1.Y) + ")", t.x(Pt1.X), t.y(Pt1.Y));
//		g.drawString ("(" + String.valueOf(Pt2.X) + "," + String.valueOf(Pt2.Y) + ")", t.x(Pt2.X), t.y(Pt2.Y));
//		g.drawString ("(" + String.valueOf(Pt3.X) + "," + String.valueOf(Pt3.Y) + ")", t.x(Pt3.X), t.y(Pt3.Y));

        // 标出边的长度
        DecimalFormat df = new DecimalFormat("#.##");
//		g.drawString (String.valueOf(df.format(EuclideanDistance(Pt1, Pt2))), t.x((Pt1.X+Pt2.X)/2), t.y((Pt1.Y+Pt2.Y)/2));
//		g.drawString (String.valueOf(df.format(EuclideanDistance(Pt2, Pt3))), t.x((Pt2.X+Pt3.X)/2), t.y((Pt2.Y+Pt3.Y)/2));
//		g.drawString (String.valueOf(df.format(EuclideanDistance(Pt3, Pt1))), t.x((Pt3.X+Pt1.X)/2), t.y((Pt3.Y+Pt1.Y)/2));

//		System.out.println("边：(" + p1.x + ", " + p1.y + ")———(" + p2.x + ", " + p2.y + ")");
//		System.out.println("边：(" + p2.x + ", " + p2.y + ")———(" + p3.x + ", " + p3.y + ")");
//		System.out.println("边：(" + p3.x + ", " + p3.y + ")———(" + p1.x + ", " + p1.y + ")");
    }

    public void paint(Graphics g, WTrsf t, double height, boolean flag) {

        // 画线
        if (this.edge1.UsageCount == 2) {
            g.drawLine(t.x(Pt1.getX()), t.y(Pt1.getY(), height), t.x(Pt2.getX()),
                    t.y(Pt2.getY(), height));
        }
        if (this.edge2.UsageCount == 2) {
            g.drawLine(t.x(Pt1.getX()), t.y(Pt1.getY(), height), t.x(Pt3.getX()),
                    t.y(Pt3.getY(), height));
        }
        if (this.edge3.UsageCount == 2) {
            g.drawLine(t.x(Pt3.getX()), t.y(Pt3.getY(), height), t.x(Pt2.getX()),
                    t.y(Pt2.getY(), height));
        }

    }

    public void displayTriangle()
    {
        System.out.println("(" + this.Pt1.getX() + "," + this.Pt1.getY() +")  (" + this.Pt2.getX() + "," + this.Pt2.getY() + ")  (" + this.Pt3.getX() + "," + this.Pt3.getY() +")");
    }
}


