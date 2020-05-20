package compG.lab7;

import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;

import java.util.ArrayList;
import java.util.Random;

public class Task7 implements Task {

    private static final int HEIGHT = 700;
    private static final int WIDTH = 1300;
    private static final int POINT_RADIUS = 8;
    private static final int BORDER_DISTANCE_H = 300;
    private static final int BORDER_DISTANCE_W = 500;

    private Group item_group;
    private ArrayList<Point2D> start_shape;
    private ArrayList<Point2D> all_points;


    Task7() {
        this.Setup();
    }
    @Override
    public Scene getScene() {
        return new Scene(item_group, WIDTH, HEIGHT);
    }
    void AddPointToShape(Point2D new_point) {
        new_point = new Point2D(new_point.getX(), HEIGHT - new_point.getY());
        item_group.getChildren().clear();
        boolean second = false;
        boolean found = false;
        Point2D p1 = null, p2 = null;
        for (int i = 0; i < start_shape.size(); i++) {
            Point2D a = start_shape.get(i);
            Point2D b = start_shape.get((i+1)%start_shape.size());
            Point2D c = start_shape.get((i+2)%start_shape.size());
            if (rotate(a, b, new_point) != rotate(b, c, new_point)) {
                found = true;
                if (!second) {
                    second = true;
                    p1 = b;
                } else {
                    p2 = b;
                    break;
                }
            }
        }
        if (found) {
            int c_x = 0, c_y = 0;
            for (Point2D point2D : start_shape) {
                c_x += point2D.getX();
                c_y += point2D.getY();
            }
            c_x /= start_shape.size();
            c_y /= start_shape.size();
            Point2D center_point = new Point2D(c_x, c_y);
            DrawPoint(center_point, Color.BLUE);

            int replace_ind = 0;
            for (int i = 0; i < start_shape.size(); i++) {
                Point2D a = start_shape.get(i);
                Point2D b = start_shape.get((i+1)%start_shape.size());
                if (rotate(a, b, new_point) != rotate(a, b, center_point)) {
                    if (a != p1 && a != p2) {
                        start_shape.remove(a);
                        i--;
                    }
                    else replace_ind = i+1;
                }
            }
            int k = (replace_ind + 1)%start_shape.size();
            if (start_shape.get(k) == p1 || start_shape.get(k) == p2 )
                start_shape.add(k, new_point);
            k = (replace_ind - 1 < 0 ? start_shape.size()-1 : replace_ind - 1);
            if (start_shape.get(k) == p1 || start_shape.get(k) == p2 )
                start_shape.add(replace_ind, new_point);
        }
        all_points.add(new_point);
        DrawShape(start_shape, Color.BLACK);
        for (Point2D all_point : all_points)
            DrawPoint(all_point, Color.RED);
        if (p1 != null) {
            DrawPoint(p1, Color.GREEN);
            DrawPoint(p2, Color.GREEN);
        }
    }

    private void Setup() {
        item_group = new Group();
        all_points = getShape();
        start_shape = (ArrayList<Point2D>) all_points.clone();
        DrawShape(start_shape, Color.BLACK);
    }

    private int rotate(Point2D a, Point2D b, Point2D c) {
        return (int)Math.signum((b.getX()-a.getX())*(c.getY()-b.getY())-(b.getY()-a.getY())*(c.getX()-b.getX()));
    }
    private ArrayList<Point2D> getShape() {
        Random random = new Random();
        ArrayList<Point2D> res = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            Point2D point = new Point2D(  (WIDTH  * 2 / 3) + (random.nextInt() % BORDER_DISTANCE_W ) - (BORDER_DISTANCE_W >> 1),
                    (HEIGHT * 2 / 3) + (random.nextInt() % BORDER_DISTANCE_H ) - (BORDER_DISTANCE_H >> 1));
            res.add(point);
        }
        return res;
    }
    private Point2D getPoint() {
        Random random = new Random();
        return new Point2D(  (WIDTH  * 2 / 3) + (random.nextInt() % BORDER_DISTANCE_W ) - (BORDER_DISTANCE_W >> 1),
                (HEIGHT * 2 / 3) + (random.nextInt() % BORDER_DISTANCE_H ) - (BORDER_DISTANCE_H >> 1));
    }
    private void DrawShape(ArrayList<Point2D> shape, Color color) {
        for (int i = 0; i < shape.size(); i++) {
            int j = (i+1) % shape.size();
            Line l = new Line(shape.get(i).getX(), HEIGHT - shape.get(i).getY(), shape.get(j).getX(), HEIGHT - shape.get(j).getY());
            l.setStroke(color);
            item_group.getChildren().add(l);
        }
    }
    private void DrawPoint(Point2D point, Color color) {
        Circle circle = new Circle(POINT_RADIUS);
        circle.setFill(color);
        circle.setTranslateX(point.getX());
        circle.setTranslateY(HEIGHT - point.getY());
        item_group.getChildren().add(circle);
    }
}