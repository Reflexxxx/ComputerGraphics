package com.company;

import java.awt.*;
import java.util.ArrayList;
import java.util.LinkedList;

// Швидкобол
public class QuickHull {
    // Червоні точки, що належать опуклій оболонці
    private LinkedList<Point> convexHull;

    // Побудова опуклої оболонки
    public LinkedList<Point> createConvexHull(ArrayList<Point> points) {
        convexHull = new LinkedList<>();

        if (!isValid(points)) {
            return convexHull;
        }

        // Ліва та права крайні точки
        var left = findTheLeftestPoint(points);
        var right = findTheRightestPoint(points);

        // Точки, які знаходяться по різні боки від прямої, що з'єднує ліву та праву крайні точки
        var upperPoints = new ArrayList<Point>(points.size());
        var lowerPoints = new ArrayList<Point>(points.size());

        // Вектор з точки right у точку left
        var splittingVector = new Point(right.x - left.x, right.y - left.y);

        // Визначення точок, які знаходяться по різні боки від прямої, що з'єднує ліву та праву крайні точки
        for (Point point : points) {
            if (isOnTheLeftSide(new Point(point.x - left.x, point.y - left.y), splittingVector)) {
                upperPoints.add(point);
            } else {
                lowerPoints.add(point);
            }
        }

        // Додавання двох описаних точок до опуклої оболонки
        convexHull.add(left);
        convexHull.add(right);

        // Рекурсивна побудова наступних частин опуклої оболонки
        convexHullRecursive(upperPoints, left, right);
        convexHullRecursive(lowerPoints, right, left);
        return convexHull;
    }

    // Якщо точок немає, або вона одна
    private boolean isValid(ArrayList<Point> points) {
        if (points.isEmpty())
            return false;

        if (points.size() == 1) {
            convexHull.add(points.get(0));
            return false;
        }
        return true;
    }

    // Пошук крайної зліва точки за абсцисою
    private Point findTheLeftestPoint(ArrayList<Point> points) {
        var left = points.get(0);
        for (int i = 1; i < points.size(); i++) {
            if (points.get(i).getX() < left.getX())
                left = points.get(i);
        }
        return left;
    }

    // Пошук крайної справа точки за абсцисою
    private Point findTheRightestPoint(ArrayList<Point> points) {
        Point right = points.get(0);
        for (int i = 1; i < points.size(); i++) {
            if (points.get(i).getX() > right.getX())
                right = points.get(i);
        }
        return right;
    }

    // Рекурсивна побудова опуклої оболонки
    private void convexHullRecursive(ArrayList<Point> points, Point left, Point right) {
        if (points.isEmpty())
            return;

        if (points.size() == 1) {
            convexHull.add(points.get(0));
            return;
        }

        // Вектор з точки right у точку left
        var splittingVector = new Point(right.x - left.x, right.y - left.y);

        // Точка, що знахдиться якнайдалі від нього
        var farthest = findTheFarthestPoint(points, left, splittingVector);
        convexHull.add(farthest);

        var newUpperPointsLeft = new ArrayList<Point>(points.size());
        var newUpperPointsRight = new ArrayList<Point>(points.size());

        splittingVector = new Point(farthest.x - left.x, farthest.y - left.y);
        var rightSplittingVector = new Point(right.x - farthest.x, right.y - farthest.y);

        for (Point point : points) {
            if (point.equals(farthest))
                continue;
            if (isOnTheLeftSide(new Point(point.x - left.x, point.y - left.y), splittingVector)) {
                newUpperPointsLeft.add(point);
            } else if (isOnTheLeftSide(new Point(point.x - farthest.x, point.y - farthest.y), rightSplittingVector)) {
                newUpperPointsRight.add(point);
            }
        }
        convexHullRecursive(newUpperPointsLeft, left, farthest);
        convexHullRecursive(newUpperPointsRight, farthest, right);

    }

    /*
    Якщо відомі дві точки, то вектор, що їх з'єднує, можна отримати відніманням двох векторів напрямлених з початку
    координат. Наприклад, є точка A і точка B:
    вектор(AB) = вектор(B) - вектор(A), тобто х(AB) = х(B) - х(A), у(AB) = у(B) - у(A).
    Отже, виходить, що якщо є вектор(AB), заданий координатами А(x, y), В(x, y) і є точка Р(x, y), то для того щоб
    дізнатися чи лежить вона зліва чи справа, треба дізнатися знак добутку:                                                            }
    (х(B) - х(А)) * (у(Р) - у(А)) - (у(В) - у(А)) * (х(Р) - х(А))
    */
    private boolean isOnTheLeftSide(Point vectorA, Point vectorB) {
        int res = vectorA.x * vectorB.y - vectorA.y * vectorB.x;
        if (res <= 0) {
            return false;
        }
        return true;
    }

    // Формула для визначення відстані від точки М до прямої Ах + Ву + С = 0
    private double distance(Point M, double A, double B, double C) {
        return Math.abs(A * M.getX() - B * M.getY() + C) / Math.sqrt(A * A + B * B);
    }

    // Пошук точки, що знаходиться якнайдалі від нього
    private Point findTheFarthestPoint(ArrayList<Point> upperPoints, Point left, Point splittingVector) {
        double A = splittingVector.y;
        double B = splittingVector.x;
        double C = left.getY() * splittingVector.getX() - left.getX() * splittingVector.getY();

        // Найбільша відстань
        double maxDistance = distance(upperPoints.get(0), A, B, C);

        // Індекс точки, що знаходиться якнайдалі
        int maxId = 0;

        // Визначення точки, що має найбільшу відстань
        for (int i = 1; i < upperPoints.size(); i++) {
            double d = distance(upperPoints.get(i), A, B, C);
            if (d > maxDistance) {
                maxDistance = d;
                maxId = i;
            } else if (d == maxDistance) {
                if (upperPoints.get(i).getX() < upperPoints.get(maxId).getX()) {
                    maxId = i;
                }
            }
        }
        return upperPoints.get(maxId);
    }
}