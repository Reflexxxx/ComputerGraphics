package com.company;

import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

import static java.lang.Integer.max;
import static java.lang.Integer.min;

public class Main {

    //введення даних з файлу
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

    //отримання відсортованих вершин за у координатою
    public static ArrayList<Integer> getSortedVertex(ArrayList<Point> vertex) {
        ArrayList<Integer> arr = new ArrayList<>();
        int maxIndex = 0;
        for (int i = 1; i < vertex.size(); i++) {
            if (vertex.get(i).y > vertex.get(maxIndex).y)
                maxIndex = i;
        }
        int minIndex;
        int minY = Integer.MIN_VALUE;
        for (int i = 0; i < vertex.size(); i++) {
            minIndex = maxIndex;
            for (int j = 0; j < vertex.size(); j++) {
                if (vertex.get(j).y > minY && vertex.get(j).y < vertex.get(minIndex).y) {
                    minIndex = j;
                }
            }
            arr.add(minIndex);
            minY = vertex.get(minIndex).y;
            if (minIndex == maxIndex) return arr;
        }
        return arr;
    }

    //перевірка чи ребро лежить в смузі
    static boolean crossStrip(Strip strip, ArrayList<Point> vertex, Point edge) {
        int downY, upY;
        downY = min(vertex.get(edge.x).y, vertex.get(edge.y).y);
        upY = max(vertex.get(edge.x).y, vertex.get(edge.y).y);
        if (strip.pointTop.y <= downY || strip.pointBottom.y >= upY)
            return false;
        return true;
    }

    //преодбробка. Сортуються всі відрізки всередині кожної смуги
    static ArrayList<Strip> getStrips(ArrayList<Point> vertex, ArrayList<Point> edges, ArrayList<Integer> sortedVertex) {
        ArrayList<Strip> arr = new ArrayList<>();

        for (int i = 1; i < sortedVertex.size(); i++) {
            Strip strip = new Strip(sortedVertex.get(i - 1), sortedVertex.get(i), vertex);
            for (int j = 0; j < edges.size(); j++) {
                if (crossStrip(strip, vertex, edges.get(j)))
                    strip.edges.add(j);
            }
            strip. sort(vertex, edges);
            arr.add(strip);
        }
        return arr;
    }

    //пошук смужки в якій знаходиться задана точка (бінарним пошуком)
    static Strip getStrip(Point p, ArrayList<Strip> strips) {
        int top = strips.size() - 1;
        int bottom = 0;
        int mid = (top + bottom) / 2;
        Strip strip = strips.get(mid);
        int tempComputation = strip.contain(p);
        while (tempComputation != 0) {
            if (tempComputation == -1) {
                top = mid;
                mid = (top + bottom) / 2;
            } else {
                if (bottom == mid) {
                    mid = top;
                } else {
                    bottom = mid;
                    mid = (top + bottom) / 2;
                }
            }
            strip = strips.get(mid);
            tempComputation = strip.contain(p);
        }
        //println(mid);
        return strip;
    }

    //основна функція програми
    public static void main(String[] args) throws FileNotFoundException {
        ArrayList<Point> vertex = inputData("vertex.txt");
        ArrayList<Point> edges = inputData("edges.txt");

        //сортування вершин за у координатою
        ArrayList<Integer> sortedVertex = getSortedVertex(vertex);

        System.out.print("Sorted vertex: ");
        for (int i = 0; i < sortedVertex.size(); i++) {
            System.out.print(sortedVertex.get(i) + " ");
        }

        //предобробка. Сортуються всі відрізки всередині кожної смуги
        ArrayList<Strip> strips = getStrips(vertex, edges, sortedVertex);

        System.out.println();
        for (int i = 0; i < strips.size(); i++) {
            System.out.print(i + " : ");
            System.out.println(strips.get(i).edges);
        }
        System.out.println();

        Scanner sc = new Scanner(System.in);
        Point p = new Point(2, 2);
        while (true) {
            p.x = sc.nextInt();
            p.y = sc.nextInt();
            if (p.y < strips.get(0).pointBottom.y || p.y > strips.get(strips.size() - 1).pointTop.y) {
                System.out.println("outside the strips");
            } else {
                //пошук смужки в якій знаходиться задана точка
                Strip strip = getStrip(p, strips);
                System.out.print("strip.pointBottom and strip.pointTop: ");
                System.out.println(strip.pointBottom.toString() + strip.pointTop.toString());

                //отримання ребер між якими знаходиться задана точка в смузі
                Point between = strip.getEdges(p, vertex, edges);
                System.out.print("between edges: ");
                System.out.println(between);

                if (between.x == -1 || between.y == -1) {
                    System.out.println("not in graph");
                } else {
                    System.out.print("by vertex: ");
                    System.out.println(edges.get(between.x).toString() + edges.get(between.y).toString());
                    System.out.println("by edges: " + vertex.get(edges.get(between.x).x).toString() + ' ' + vertex.get(edges.get(between.x).y).toString() +
                            '\t' + (vertex.get(edges.get(between.y).x).toString() + ' ' + vertex.get(edges.get(between.y).y).toString()));
                }
            }
        }
    }
}
