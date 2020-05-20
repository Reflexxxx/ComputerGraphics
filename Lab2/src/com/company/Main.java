package com.company;

import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

/**
 * Created by Julia on 3/15/2017.
 */

public class Main {

    public static int numb;

    public static int checkRightOfEdge(Point a, Point b, Point middle) {
        //-1 left, 0 on, 1 right
        long ax = a.x - middle.x;
        long ay = a.y - middle.y;
        long bx = b.x - middle.x;
        long by = b.y - middle.y;
        if (ay * by > 0)
            return 1;
        int s = Long.signum(ax * by - ay * bx);
        if (s == 0)
        {
            if (ax * bx <= 0)
                return 0;
            return 1;
        }
        if (ay < 0)
            return -s;
        if (by < 0)
            return s;
        return 1;
    }

    @SuppressWarnings("empty-statement")
    public static int checkRightOfLine(Point C, ArrayList<Point> line, ArrayList<Point> poligon){
        int i = 0;
        for(; poligon.get(line.get(i).y).y < C.y; i++);
        return checkRightOfEdge(poligon.get(line.get(i).x), poligon.get(line.get(i).y), C);
    }

    public static ArrayList<Point> inputDatas(String filename) throws FileNotFoundException {
        ArrayList<Point> arr = new ArrayList<>();

        Scanner scFile = new Scanner(new File(filename));
        while (scFile.hasNextInt()) {
            Point tmp = new Point();
            tmp.x = scFile.nextInt();
            tmp.y = scFile.nextInt();
            arr.add(tmp);
            //System.out.println(arr.get(arr.size() - 1));
        }
        return arr;
    }

    public static Point[] convert(ArrayList<Point> arr) {
        Point[] arrP = new Point[arr.size()];
        for(int i = 0;i <arrP.length;i++)
            arrP[i]=arr.get(i);
        return arrP;
    }

    public static ArrayList<Point> convert(Point[] arr) {
        ArrayList<Point> arrP = new ArrayList<>();
        arrP.addAll(Arrays.asList(arr));
        return arrP;
    }

    public static void main(String[] args) throws FileNotFoundException {
        while(true){
            ArrayList<Point> poligon = inputDatas("poligon.txt");
            ArrayList<Point> edges = inputDatas("edges.txt");
//        Point[] edgesP = convert(edges);
            int[] vertexP = new int[poligon.size()];

            for (int i = 0; i < vertexP.length; i++) {
                vertexP[i] = i;
            }

            Scanner sc = new Scanner(System.in);

            Point c = new Point(0,6);
            c.x=sc.nextInt();
            c.y=sc.nextInt();

            numb = 0;
            TreePoint tree = trapezium(convert(poligon),vertexP,convert(edges));
            System.out.println(binSearchT(tree,c,poligon));

            ArrayList<ArrayList<Point>> arrayLines = Lines(poligon,edges);
            System.out.println(binSearchL(arrayLines,c,poligon));
        }

    }

    public  static  int binSearchL(ArrayList<ArrayList<Point>> lines, Point C, ArrayList<Point> poligon){
        int mid = lines.size()/2;
        int r=lines.size()-1;
        int l=0;
        int tempComp = checkRightOfLine(C,lines.get(mid),poligon);
        while (tempComp != 0 && r - l > 1){
            if(tempComp == 1) l = mid;
            else r = mid;
            mid = l + (r - l)/2;
            tempComp = checkRightOfLine(C,lines.get(mid),poligon);
        }
        if(tempComp >= 0) return mid;
        else return mid - 1;
    }

    public static String binSearchT(TreePoint tree, Point C, ArrayList<Point> poligon){
        TreePoint temp = tree;
        while(temp.getFlag() != -1){
            if(temp.getFlag()==1){
                if(C.y < temp.getData().y) temp = temp.getLeftSon();
                else temp = temp.getRightSon();
            } else{
                if(checkRightOfEdge(poligon.get(temp.getData().x), poligon.get(temp.getData().y), C) == -1) temp = temp.getLeftSon();
                else temp = temp.getRightSon();
            }
        }
        return  temp.getLeafInfo();
    }



    public static int WeightIn(int[] Weight, ArrayList<Point> E, int index){
        int counter = 0;
        for(int i = 0; i<E.size();i++){
            if (E.get(i).y == index) counter += Weight[i];
        }
        return counter;
    }

    public static int edgesOut(ArrayList<Point> E, int index){
        int counter = 0;
        for(int i = 0; i<E.size();i++){
            if (E.get(i).x == index) counter ++;
        }
        return counter;
    }

    public static int WeightOut(int[] Weight, ArrayList<Point> E, int index){
        int counter = 0;
        for(int i = 0; i<E.size();i++){
            if (E.get(i).x == index) counter += Weight[i];
        }
        return counter;
    }

    public static int firstEdgeOut(int[] Weight, ArrayList<Point> E, int index){
        for (int i = 0;i<E.size();i++){
            if (E.get(i).x == index && Weight[i] > 0) return i;
        }
        return -1;
    }

    public static int firstEdgeIn(int[] Weight, ArrayList<Point> E, int index){
        for (int i = 0;i<E.size();i++){
            if (E.get(i).y == index && Weight[i] > 0) return i;
        }
        return -1;
    }

    public static ArrayList<ArrayList<Point>> Lines(ArrayList<Point> V, ArrayList<Point> E){
        ArrayList<ArrayList<Point>> arrayLines = new ArrayList<>();

        int[] Weight = new int[E.size()];

        for (int i = 0;i<E.size();i++){
            Weight[i]=1;
        }
        for (int i = 1; i<V.size()-1;i++){
            int win = WeightIn(Weight,E,i);
            int d1 = firstEdgeOut(Weight,E,i);
            int vOut = edgesOut(E, i);
            if (win > vOut) Weight[d1] = win - vOut + 1;
        }
        for (int i = V.size() - 2; i > 0; i--){
            int wout = WeightOut(Weight,E,i);
            int d2 = firstEdgeIn(Weight,E,i);
            int win = WeightIn(Weight,E,i);
            if (wout > win) Weight[d2] = wout - win + Weight[d2];
        }
        boolean flag=true;
        while(flag){
            ArrayList<Point> line = new ArrayList<>();
            int nextV = 0;
            while(nextV != V.size()-1){
                int nextE = firstEdgeOut(Weight, E, nextV);
                Weight[nextE]--;
                nextV = E.get(nextE).y;
                line.add(E.get(nextE));
            }
            arrayLines.add(line);
            flag = firstEdgeOut(Weight, E,0)!=-1;
        }

        return arrayLines;
    }



    public static boolean comparation(Point[] v, Point e, int dL, int uL){
        if(v[e.x].y <= v[dL].y && v[e.y].y >= v[uL].y) return true;
        return v[e.y].y <= v[dL].y && v[e.x].y >= v[uL].y;
    }

    public static int weight(TreePoint U){
        if(U == null) return 0;
        if(U.getFlag()<0) return 0;
        else if(U.getFlag()<1) return weight(U.getLeftSon())+weight(U.getRightSon())+1;
        else return weight(U.getLeftSon())+weight(U.getRightSon());
    }

    public static int weight(ArrayList<TreePoint> U){
        int res=0;
        for (int i = 0; i < U.size(); i++) {
            if(U.get(i).getFlag()>0) res+=weight(U.get(i));
        }
        return res;
    }

    public static TreePoint balance(ArrayList<TreePoint> U){
        TreePoint res = new TreePoint();
        if (U.isEmpty()) return null;
        if (U.size() == 1) return U.get(0);
        int weightU = weight(U);
        if (weightU > 0){
            int sum = 0;
            ArrayList<TreePoint> u1 = new ArrayList<>();
            ArrayList<TreePoint> u2 = new ArrayList<>();
            for (int i = 0; i < U.size(); i++){
                if(sum+weight(U.get(i)) < weightU/2) u1.add(U.get(i));
                else u2.add(U.get(i));
                sum+=weight(U.get(i));
            }
            res.setFlag(u1.get(u1.size() - 1).getFlag());
            res.setData(u1.get(u1.size() - 1).getData());
            res.setLeafInfo(u1.get(u1.size() - 1).getLeafInfo());
            u1.remove(u1.size()-1);
            res.setLeftSon(balance(u1));
            res.setRightSon(u2.get(1));
            res.getRightSon().setLeftSon(u2.get(0));
            u2.remove(0);
            u2.remove(1);
            res.getRightSon().setRightSon(balance(u2));
        } else {                                         // do balancing tree
            int mid = U.size()/2;
            ArrayList<TreePoint> u1 = new ArrayList<>();
            ArrayList<TreePoint> u2 = new ArrayList<>();
            for (int i = 0; i < mid; i++){
                u1.add(U.get(i));
            }
            for (int i = mid+1; i < U.size(); i++){
                u2.add(U.get(i));
            }
            res.setFlag(U.get(mid).getFlag());
            res.setData(U.get(mid).getData());
            res.setLeafInfo(U.get(mid).getLeafInfo());
            res.setLeftSon(balance(u1));
            res.setRightSon(balance(u2));
        }
        return res;
    }

    public static int[] sort(int[] V) {
        for(int i = 0; i < V.length;i++){
            int j;
            for(j = 0; j < i;j++)
                if(V[i] < V[j]) break;
            int temp = V[j];
            V[j] = V[i];
            for(; j < i;j++){
                int N = temp;
                temp = V[j+1];
                V[j+1] = N;
            }
        }
        return V;
    }

    public static Point[] add(Point[] V, Point E){
        Point[] arr = new Point[V.length + 1];
        System.arraycopy(V, 0, arr, 0, V.length);
        arr[V.length] = E;
        return arr;
    }

    public static int[] add(int[] V, int E){
        int[] arr = new int[V.length + 1];
        System.arraycopy(V, 0, arr, 0, V.length);
        arr[V.length] = E;
        return arr;
    }

    public static boolean contains(Point[] V, Point E){
        for (Point V1 : V)
            if (V1 == E) return true;
        return false;
    }

    public static boolean contains(int[] V, int E){
        for (int V1 : V)
            if (V1 == E) return true;
        return false;
    }

    public static TreePoint trapezium(Point[] poligon, int[] V, Point[] E){
        TreePoint tempTree = new TreePoint();
        V = sort(V);
        if (V.length<=0) {
            tempTree.setFlag(-1); //leaf
            tempTree.setLeafInfo("Leaf" + ++numb);
            return tempTree;
        }
        ArrayList<int[]> v = new ArrayList<>();
        ArrayList<Point[]> e = new ArrayList<>();
        ArrayList<ArrayList<TreePoint>> u = new ArrayList<>();
        for (int i = 0; i < 2; i++) {
            v.add(new int[0]);
            e.add(new Point[0]);
            u.add(new ArrayList<>());
        }

        Point mid = poligon[V[V.length/2]];
        int[] downLimit = {poligon[V[0]].y, mid.y};
        int[] upLimit = {mid.y, poligon[V[V.length-1]].y};

        for (Point E1 : E) {
            for (int k = 0; k < 2; k++) {
                if (poligon[E1.x].y > downLimit[k] && poligon[E1.x].y < upLimit[k]) {
                    if(!contains(v.get(k), E1.x)){
                        v.set(k, add(v.get(k), E1.x));
                    }
                    if (!contains(e.get(k),E1)) {
                        e.set(k, add(e.get(k),E1));
                    }
                }
                if (poligon[E1.y].y > downLimit[k] && poligon[E1.y].y < upLimit[k]) {
                    if(!contains(v.get(k), E1.y)){
                        v.set(k, add(v.get(k), E1.y));
                    }
                    if (!contains(e.get(k),E1)) {
                        e.set(k, add(e.get(k),E1));
                    }
                }
                if (comparation(poligon, E1, downLimit[k], upLimit[k])) {
                    u.get(k).add(trapezium(poligon, v.get(k), e.get(k)));
                    TreePoint localTree = new TreePoint();
                    localTree.setFlag(0); //edge
                    localTree.setData(E1);
                    u.get(k).add(localTree);
                    v.set(k,new int[0]);
                    e.set(k,new Point[0]);
                }
            }
        }
        u.get(0).add(trapezium(poligon, v.get(0), e.get(0)));
        u.get(1).add(trapezium(poligon, v.get(1), e.get(1)));
        tempTree.setFlag(1); //vertex
        tempTree.setData(mid);
        tempTree.setLeftSon(balance(u.get(0)));
        tempTree.setRightSon(balance(u.get(1)));
        return tempTree;
    }
}

