package com.company;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.LinkedList;

// Полотно
public class DrawArea extends JComponent {
    // Усі точки
    private ArrayList<Point> points;

    // Червоні точки, що належать опуклій оболонці
    private LinkedList<Point> convexHull;

    // Радіус точки на полотні
    private final int PAINT_RADIUS = 10;

    private Image image;
    private Graphics2D graphics2D;

    // Блокування помилкових натиснень
    public boolean lockDrawArea;

    private void drawPoint(int x, int y, int i) {
        graphics2D.fillOval(x, y, PAINT_RADIUS, PAINT_RADIUS);
        repaint();
    }


    public DrawArea() {
        setDoubleBuffered(false);
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (!lockDrawArea) {
                    drawPoint(e.getX(), e.getY(), points.size());
                    points.add(e.getPoint());
                }
            }
        });
    }

    // Очищення даних вікна
    public void clearData() {
        clear();
        initialize();
    }

    // "Швидка" побудова опуклої оболонки
    public void createConvexHull() {
        clear();
        graphics2D.setPaint(Color.BLACK);
        for (int i = 0; i < points.size(); i++) {
            drawPoint(points.get(i).x, points.get(i).y, i);
        }

        var quickHull = new QuickHull();
        convexHull = quickHull.createConvexHull(points);

        // Фарбування опуклої опуклої оболонки червоним кольором
        graphics2D.setPaint(Color.red);
        for (int i = 0; i < convexHull.size(); i++) {
            graphics2D.drawString(Integer.toString(i), convexHull.get(i).x, convexHull.get(i).y + 2 * PAINT_RADIUS);
            graphics2D.fillOval(convexHull.get(i).x, convexHull.get(i).y, PAINT_RADIUS, PAINT_RADIUS);
        }
        graphics2D.setPaint(Color.black);
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        if (image == null) {
            image = createImage(getSize().width, getSize().height);
            graphics2D = (Graphics2D) image.getGraphics();
            graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            clearData();
        }
        g.drawImage(image, 0, 0, null);
    }

    private void initialize() {
        points = new ArrayList<>();
        graphics2D.setPaint(Color.black);
    }

    private void clear() {
        graphics2D.setPaint(Color.white);
        graphics2D.fillRect(0, 0, getWidth(), getHeight());
        repaint();
    }
}