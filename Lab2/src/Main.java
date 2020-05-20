import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;

public class Main {

    public static int checkRightOfEdge(Point a, Point b, Point middle) {
        // -1 left, 0 on, 1 right
        // D = (х3 - х1) * (у2 - у1) - (у3 - у1) * (х2 - х1)
        int D = (middle.x - a.x) * (b.y - a.y) - (middle.y - a.y) * (b.x - a.x);
        if (D == 0) {
            if (b.y == a.y) {//horizontal line
                if (a.x < middle.x) return 1;
                if (b.x > middle.x) return -1;
            }
        }
        return Long.signum(D);
    }

    public static int checkRightOfLine(Point C, ArrayList<Point> line, ArrayList<Point> vertex) {
        int n = line.size();
        int mid = n / 2;
        int r = n - 1;
        int l = 0;
        boolean wasChange = true;

        while (wasChange) {
            wasChange = false;
            if (vertex.get(line.get(mid).x).y > C.y) {
                if (r == mid) return checkRightOfEdge(vertex.get(line.get(mid).x), vertex.get(line.get(mid).y), C);
                r = mid;
                mid = l + (r - l) / 2;
                wasChange = true;
            } else if (vertex.get(line.get(mid).y).y < C.y) {
                if (l == mid && r != mid) {
                    mid = r;
                    wasChange = true;
                } else {

                    l = mid;
                    mid = l + (r - l) / 2;
                    wasChange = true;
                }
            } else return checkRightOfEdge(vertex.get(line.get(mid).x), vertex.get(line.get(mid).y), C);
        }

        return checkRightOfEdge(vertex.get(line.get(mid).x), vertex.get(line.get(mid).y), C);
    }

    public static ArrayList<Point> inputData(String filename) throws FileNotFoundException {
        ArrayList<Point> arr = new ArrayList<>();

        Scanner scFile = new Scanner(new File(filename));
        while (scFile.hasNextInt()) {
            Point tmp = new Point();
            tmp.x = scFile.nextInt();
            tmp.y = scFile.nextInt();
            arr.add(tmp);
        }
        return arr;
    }

    public static void main(String[] args) throws FileNotFoundException {

        ArrayList<Point> vertex = inputData("vertex.txt");
        ArrayList<Point> edges = inputData("edges.txt");

        ArrayList<ArrayList<Point>> arrayLines = Lines(vertex, edges);

        while (true) {
            Scanner sc = new Scanner(System.in);

            Point c = new Point(0, 0);
            c.x = sc.nextInt();
            c.y = sc.nextInt();

            Point p = binSearchL(arrayLines, c, vertex);
            if (p.y == 0) System.out.println("line " + p.x);
            else System.out.println("sector " + p.x);
        }
    }

    public static Point binSearchL(
            ArrayList<ArrayList<Point>> lines, Point C, ArrayList<Point> vertex) {
        int n = lines.size();
        int mid = n / 2;
        int r = n - 1;
        int l = 0;

        if (vertex.get(lines.get(0).get(lines.get(0).size() - 1).y).y < C.y) return new Point(-1, 1);
        if (vertex.get(lines.get(0).get(0).x).y > C.y) return new Point(-1, 1);
        if (C.equals(vertex.get(lines.get(0).get(lines.get(0).size() - 1).y))) return new Point(mid, 0);
        if (C.y == vertex.get(lines.get(0).get(lines.get(0).size() - 1).y).y) return new Point(-1, -1);

        if (checkRightOfLine(C, lines.get(l), vertex) == -1
                || (checkRightOfLine(C, lines.get(r), vertex) == 1)) return new Point(-1, 1);


        int tempComp = checkRightOfLine(C, lines.get(mid), vertex);
        while (tempComp != 0 && r - l > 1) {
            if (tempComp == 1) l = mid;
            else r = mid;
            mid = l + (r - l) / 2;
            tempComp = checkRightOfLine(C, lines.get(mid), vertex);
        }

        if (tempComp == 0) return new Point(mid, 0);
        if (tempComp > 0) return new Point(mid, 1);
        else return new Point(mid - 1, 1);
    }

    public static int WeightIn(int[] Weight, ArrayList<Point> E, int index) {
        int counter = 0;
        for (int i = 0; i < E.size(); i++) {
            if (E.get(i).y == index) counter += Weight[i];
        }
        return counter;
    }

    public static int edgesOut(ArrayList<Point> E, int index) {
        int counter = 0;
        for (Point point : E) {
            if (point.x == index) counter++;
        }
        return counter;
    }

    public static int WeightOut(int[] Weight, ArrayList<Point> E, int index) {
        int counter = 0;
        for (int i = 0; i < E.size(); i++) {
            if (E.get(i).x == index) counter += Weight[i];
        }
        return counter;
    }

    public static int firstEdgeOut(int[] Weight, ArrayList<Point> E, int index) {
        for (int i = 0; i < E.size(); i++) {
            if (E.get(i).x == index && Weight[i] > 0) return i;
        }
        return -1;
    }

    public static int firstEdgeIn(int[] Weight, ArrayList<Point> E, int index) {
        for (int i = 0; i < E.size(); i++) {
            if (E.get(i).y == index && Weight[i] > 0) return i;
        }
        return -1;
    }

    public static ArrayList<ArrayList<Point>> Lines(ArrayList<Point> V, ArrayList<Point> E) {
        ArrayList<ArrayList<Point>> arrayLines = new ArrayList<>();

        int[] Weight = new int[E.size()];

        for (int i = 0; i < E.size(); i++) {
            Weight[i] = 1;
        }
        for (int i = 1; i < V.size() - 1; i++) {
            int win = WeightIn(Weight, E, i);
            int d1 = firstEdgeOut(Weight, E, i);
            int vOut = edgesOut(E, i);
            if (win > vOut) Weight[d1] = win - vOut + 1;
        }
        for (int i = V.size() - 2; i > 0; i--) {
            int wout = WeightOut(Weight, E, i);
            int d2 = firstEdgeIn(Weight, E, i);
            int win = WeightIn(Weight, E, i);
            if (wout > win) Weight[d2] = wout - win + Weight[d2];
        }
        boolean flag = true;
        while (flag) {
            ArrayList<Point> line = new ArrayList<>();
            int nextV = 0;
            while (nextV != V.size() - 1) {
                int nextE = firstEdgeOut(Weight, E, nextV);
                Weight[nextE]--;
                nextV = E.get(nextE).y;
                line.add(E.get(nextE));
            }
            arrayLines.add(line);
            flag = firstEdgeOut(Weight, E, 0) != -1;
        }

        int n = arrayLines.size();
        for (int i = 0; i < n; i++) {
            int k = arrayLines.get(i).size();
            System.out.println("Chain " + i);


            for (int j = 0; j < k; j++) {
                System.out.println(
                        arrayLines.get(i).get(j).x + " -> " + arrayLines.get(i).get(j).y);
            }
        }

        return arrayLines;
    }
}
