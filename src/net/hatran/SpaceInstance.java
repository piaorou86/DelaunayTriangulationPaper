package net.hatran;

import java.awt.Graphics;

//空间实例
public class SpaceInstance {
    private int InstanceID; // 实例编号
    private char FeatureType; // 实例所属特征
    private PositionInSpace positionInSpace; // 实例空间位置

    public void setInstanceID(int InstanceID) {
        this.InstanceID = InstanceID;
    }

    public int getInstanceID() {
        return this.InstanceID;
    }

    public void setFeatureType(char FeatureType) {
        this.FeatureType = FeatureType;
    }

    public char getFeatureType() {
        return FeatureType;
    }

    public void setPositionInSpace(PositionInSpace positionInSpace) {
        this.positionInSpace = positionInSpace;
    }

    public PositionInSpace getPositionInSpace() {
        return this.positionInSpace;
    }

    public void DisplaySpaceInstance() {
        System.out.print(this.InstanceID + " " + this.FeatureType + " ");
        this.positionInSpace.DisplayPositionInSpace();
        System.out.println();
    }
}
