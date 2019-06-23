package net.hatran;

import java.awt.Color;
import java.awt.Graphics;
import java.math.BigInteger;

//实例空间位置
public class PositionInSpace {
    private double X;
    private double Y;
    private int Feature;
    private int Instance;

    public void setX(double X) {
        this.X = X;
    }

    public double getX() {
        return this.X;
    }

    public void setY(double Y) {
        this.Y = Y;
    }

    public double getY() {
        return this.Y;
    }

    public void setFeature(int Feature) {
        this.Feature = Feature;
    }

    public int getFeature() {
        return this.Feature;
    }

    public void setInstance(int Instance) {
        this.Instance = Instance;
    }

    public int getInstance() {
        return this.Instance;
    }


    public void paint(Graphics g, WTrsf t, String key, double height) {
        // 绘制坐标点（绘制椭圆的轮廓：结果是在x、y、宽度和高度参数指定的矩形内形成一个圆圈或椭圆）
        g.drawOval(t.x(X), t.y(Y, height), 4, 4);
        // 标出顶点的名称
        g.setColor(Color.black);
        g.drawString (key, t.x(X), t.y(Y, height));
        // 标出坐标值
//		g.drawString ("(" + String.valueOf(X) + "," + String.valueOf(Y) + ")", t.x(X), t.y(Y));
    }

    public void DisplayPositionInSpace() {
        System.out.print("(" + X + "," + Y + ")");
    }

    @Override //
    public boolean equals(Object obj) {
        if (obj == null || getClass() != obj.getClass())
            return false;

        if (obj == this)
            return true;

        PositionInSpace pos = (PositionInSpace) obj;
        return Feature == pos.Feature && Instance == pos.Instance;

    }

    @Override
    public int hashCode() {
        return Feature;
    }


}
