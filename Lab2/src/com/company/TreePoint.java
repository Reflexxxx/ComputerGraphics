package com.company;

import java.awt.*;
public class TreePoint {
    int flag; //-1 leaf,1 vertex, 0 edge
    String leafInfo;
    Point data;
    TreePoint leftSon, rightSon;

    TreePoint(){
        flag = 0;
        leafInfo = "";
        data = new Point(0,0);
        leftSon = rightSon=null;
    }

    public void setLeafInfo(String leafInfo) {
        this.leafInfo = leafInfo;
    }

    public void setFlag(int flag) {
        this.flag = flag;
    }

    public void setData(Point data) {
        this.data = data;
    }

    public void setLeftSon(TreePoint leftSon) {
        this.leftSon = leftSon;
    }

    public void setRightSon(TreePoint rightSon) {
        this.rightSon = rightSon;
    }

    public Point getData() {
        return data;
    }

    public TreePoint getLeftSon() {
        return leftSon;
    }

    public String getLeafInfo() {
        return leafInfo;
    }

    public TreePoint getRightSon() {
        return rightSon;
    }

    public int getFlag() {
        return flag;
    }
}
