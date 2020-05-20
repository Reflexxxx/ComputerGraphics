package compG.lab1;

import java.awt.*;
import java.util.Vector;

public class Main {

    //косое произведение векторов
    //возвращает true, если точка лежит слева от прямой
    static boolean vector_mult(Point A, Point B, double x, double y) {
        if ( ((B.x-A.x)*(y - A.y) - (B.y-A.y)*(x - A.x)) >= 0)
            return true;
        else
            return false;
    }

    static boolean check_attachment(double x, double y, Vector<Point> Poly) {  //возвращает true, если точка лежит слева от каждой прямой
        for(int i = 0; i < Poly.size()-1; i++) {
            if(!vector_mult(Poly.get(i), Poly.get(i+1), x, y))
                return false;
        }
        if (vector_mult(Poly.get(Poly.size()-1), Poly.get(0), x, y))
            return true;
        else
            return false;
    }

    public static void main(String[] args) {
         Vector<Point> Poly = new Vector<>();

         //задаем координаты против часовой стрелки
         Poly.add(new Point(20, 17));
         Poly.add(new Point(32, 12));
         Poly.add(new Point(44, 17));
         Poly.add(new Point(44, 25));
         Poly.add(new Point(32, 30));
         Poly.add(new Point(20, 25));

         if (check_attachment(10, 15, Poly)) {
             System.out.println("Принадлежит");
         } else {
             System.out.println("Не принадлежит");
         }

    }
}
