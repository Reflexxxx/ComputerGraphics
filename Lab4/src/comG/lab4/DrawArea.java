package comG.lab4;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.ArrayList;
import java.util.LinkedList;

public class DrawArea extends JComponent {

    enum Mode {
        DRAWING_POINTS, DRAWING_RECTANGLE
    }

    private final int PAINT_RADIUS = 10;

    private Mode drawingMode;
    private Point fixedRectanglePoint;
    private Point currentMousePosition;
    private ArrayList<Point> points;
    private TwoDTree tree;
    private LinkedList<Integer> selectedPoints;
    private boolean drawingNewRectangle;

    private Image image;
    private Graphics2D graphics2D;

    public DrawArea() {
        setDoubleBuffered(false);
        addListeners();
    }

    public void setChoosingRegionMode() {
        drawingMode = Mode.DRAWING_RECTANGLE;
        tree = build2DTree(points, null);
    }

    public void setDrawingPointsMode() {
        drawingMode = Mode.DRAWING_POINTS;
    }

    @Override
    protected void paintComponent(Graphics g) {
        if(image == null) {
            initialize();
        }
        g.drawImage(image, 0, 0, null);
    }

    private void initialize() {
        image = createImage(getSize().width, getSize().height);
        graphics2D = (Graphics2D) image.getGraphics();
        graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        clear();

        drawingMode = Mode.DRAWING_POINTS;
        fixedRectanglePoint = null;
        currentMousePosition = null;
        points = new ArrayList<>();
        drawingNewRectangle = true;
        graphics2D.setPaint(Color.black);
    }

    private void addListeners() {
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                switch (drawingMode) {
                    case DRAWING_POINTS:
                        if(noPointAreIntersected(e.getPoint())) {
                            addPoint(e.getPoint());
                            clear();
                            repaintSetOfPoints();
                        }
                        break;
                    case DRAWING_RECTANGLE:
                        Point center = new Point((fixedRectanglePoint.x + currentMousePosition.x) / 2,
                                (fixedRectanglePoint.y + currentMousePosition.y) / 2);
                        int w, h;
                        if(currentMousePosition.getY() > fixedRectanglePoint.getY()) {
                            if(currentMousePosition.getX() > fixedRectanglePoint.getX()) {
                                w = (currentMousePosition.x - fixedRectanglePoint.x) / 2;
                                h = (currentMousePosition.y - fixedRectanglePoint.y) / 2;
                            } else {
                                w = (- currentMousePosition.x + fixedRectanglePoint.x) / 2;
                                h = (currentMousePosition.y - fixedRectanglePoint.y) / 2;
                            }
                        } else {
                            if(currentMousePosition.getX() < fixedRectanglePoint.getX()) {
                                w = (- currentMousePosition.x + fixedRectanglePoint.x) / 2;
                                h = (- currentMousePosition.y + fixedRectanglePoint.y) / 2;
                            } else {
                                w = (currentMousePosition.x - fixedRectanglePoint.x) / 2;
                                h = (- currentMousePosition.y + fixedRectanglePoint.y) / 2;
                            }
                        }

                        searchPointsInArea(tree, center, w, h);
                        graphics2D.setPaint(Color.red);
                        for(int i = 0; i < selectedPoints.size(); i++) {
                            graphics2D.fillOval(points.get(selectedPoints.get(i)).x, points.get(selectedPoints.get(i)).y, PAINT_RADIUS, PAINT_RADIUS);
                            graphics2D.drawString(Integer.toString(selectedPoints.get(i)), points.get(selectedPoints.get(i)).x, points.get(selectedPoints.get(i)).y);
                        }
                        repaint();
                        drawingNewRectangle = true;
                        selectedPoints = null;
                        break;
                }
            }
        });

        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if((drawingMode == Mode.DRAWING_RECTANGLE) &&  (graphics2D != null)){
                    if(fixedRectanglePoint == null) {
                        fixedRectanglePoint = e.getPoint();
                    }

                    if(currentMousePosition != null) {
                        clear();
                        repaintSetOfPoints();
                        if(drawingNewRectangle) {
                            fixedRectanglePoint = e.getPoint();
                            drawingNewRectangle = false;
                        }
                    }

                    graphics2D.setPaint(Color.red);
                    currentMousePosition = e.getPoint();
                    paintSearchingArea();
                    repaint();
                }
            }
        });
    }

    private boolean noPointAreIntersected(Point p) {
        if(points == null)
            return true;
        for(int i = 0; i < points.size(); i++) {
            if(isInsideTheArea(p, points.get(i), 2 * PAINT_RADIUS, 2 * PAINT_RADIUS))
                return false;
        }
        return true;
    }

    private void repaintSetOfPoints() {
        if(points == null)
            return;
        graphics2D.setPaint(Color.black);
        for(int i = 0; i < points.size(); i++) {
            graphics2D.fillOval(points.get(i).x, points.get(i).y, PAINT_RADIUS, PAINT_RADIUS);
            graphics2D.drawString(new Integer(i).toString(), points.get(i).x, points.get(i).y);
        }
        repaint();
    }

    private void paintSearchingArea() {
        if(currentMousePosition.getY() > fixedRectanglePoint.getY()) {
            if(currentMousePosition.getX() > fixedRectanglePoint.getX()) {
                graphics2D.drawRect(fixedRectanglePoint.x,
                        fixedRectanglePoint.y,
                        currentMousePosition.x - fixedRectanglePoint.x,
                        currentMousePosition.y - fixedRectanglePoint.y);
            } else {
                graphics2D.drawRect(currentMousePosition.x,
                        fixedRectanglePoint.y,
                        - currentMousePosition.x + fixedRectanglePoint.x,
                        currentMousePosition.y - fixedRectanglePoint.y);
            }
        } else {
            if(currentMousePosition.getX() < fixedRectanglePoint.getX()) {
                graphics2D.drawRect(currentMousePosition.x,
                        currentMousePosition.y,
                        -currentMousePosition.x + fixedRectanglePoint.x,
                        -currentMousePosition.y + fixedRectanglePoint.y);
            } else {
                graphics2D.drawRect(fixedRectanglePoint.x,
                        currentMousePosition.y,
                        currentMousePosition.x - fixedRectanglePoint.x,
                        - currentMousePosition.y + fixedRectanglePoint.y);
            }
        }
    }

    private void addPoint(Point p) {
        int i = 0;
        while ((i < points.size()) && (points.get(i).getX() < p.getX()))
            i++;
        if(i == points.size())
            points.add(p);
        else points.add(i, p);
    }

    private TwoDTree build2DTree(ArrayList<Point> points, TwoDTree parent) {
        if(points.isEmpty()) {
            return null;
        }
        TwoDTree res;
        int n = points.size() / 2;
        if(parent == null) {
            res = new TwoDTree(parent, true);
        } else {
            res = new TwoDTree(parent, !parent.isVerticalSplitting());
        }

        ArrayList<Point> rightPoints;
        ArrayList<Point> leftPoints;

        if(! res.isVerticalSplitting()) {
            ArrayList<Point> sortedByYValue = sortByYValue(points);

            rightPoints = new ArrayList<>(n);
            leftPoints = new ArrayList<>(sortedByYValue.size() - n);

            Point splittingPoint = sortedByYValue.get(n);
            for(int i = 0; i < this.points.size(); i++) {
                if (this.points.get(i).equals(splittingPoint)) {
                    res.setValue(i);
                    break;
                }
            }

            for (int i = 0; i < n; i++) {
                leftPoints.add(sortedByYValue.get(i));
                if(i + n + 1 < sortedByYValue.size())
                    rightPoints.add(sortedByYValue.get(i + n + 1));
            }

        } else {
            //sort by X value
            leftPoints = new ArrayList<>(n);
            rightPoints = new ArrayList<>(points.size() - n);

            ArrayList<Point> sortedByXValue = sortByXValue(points);

            Point splittingPoint = sortedByXValue.get(n);
            for(int i = 0; i < this.points.size(); i++) {
                if (this.points.get(i).equals(splittingPoint)) {
                    res.setValue(i);
                    break;
                }
            }
            for (int i = 0; i < n; i++) {
                leftPoints.add(sortedByXValue.get(i));
                if(i + n + 1 < sortedByXValue.size())
                    rightPoints.add(sortedByXValue.get(i + n + 1));
            }

        }
        res.left = build2DTree(leftPoints, res);
        res.right = build2DTree(rightPoints, res);

        return res;
    }

    private void searchPointsInArea(TwoDTree tree, Point center, int w, int h) {
        if(selectedPoints == null)
            selectedPoints = new LinkedList<>();
        if(tree == null)
            return;

        if(tree.isVerticalSplitting()) {
            splitVerticallyAndSearch(tree, center, w, h);
        } else {
            splitHorizontallyAndSearch(tree, center, w, h);
        }
    }

    private void splitVerticallyAndSearch(TwoDTree tree, Point center, int w, int h) {
        float delta = PAINT_RADIUS / 2;
        if( isInRange(points.get(tree.getValue()).getX() + delta, center.getX() - w, center.getX() + w)) {
            if(isInRange(points.get(tree.getValue()).getY() + delta, center.getY() - h, center.getY() + h)) {
                selectedPoints.add(tree.getValue());
            }
            searchPointsInArea(tree.left, center, w, h);
            searchPointsInArea(tree.right, center, w, h);
        } else {
            if(center.getX() - w < points.get(tree.getValue()).getX() + delta) {
                searchPointsInArea(tree.left, center, w, h);
            } else {
                searchPointsInArea(tree.right, center, w, h);
            }
        }
    }

    private void splitHorizontallyAndSearch(TwoDTree tree, Point center, int w, int h) {
        // needed to shift the point coordinates from top right angular to center
        float delta = PAINT_RADIUS / 2;
        if(isInRange(points.get(tree.getValue()).getY() + delta, center.getY() - h, center.getY() + h)) {
            if(isInRange(points.get(tree.getValue()).getX() + delta, center.getX() - w, center.getX() + w)) {
                selectedPoints.add(tree.getValue());
            }
            searchPointsInArea(tree.left, center, w, h);
            searchPointsInArea(tree.right, center, w, h);
        } else {
            if(center.getY() - h < points.get(tree.getValue()).getY() + delta) {
                searchPointsInArea(tree.left, center, w, h);
            } else {
                searchPointsInArea(tree.right, center, w, h);
            }
        }
    }

    private void clear() {
        graphics2D.setPaint(Color.white);
        graphics2D.fillRect(0, 0, getWidth(), getHeight());
        repaint();
    }

    private boolean isInsideTheArea(Point p, Point center, int width, int height) {
        float w = width / 2;
        float h = height / 2;
        if(isInRange(p.getX(), center.getX() - w, center.getX() + w) &&
                isInRange(p.getY(), center.getY() - h, center.getY() + h))
            return true;
        return false;
    }

    private boolean isInRange(double value, double from, double to) {
        if((from <= value) && (value <= to))
            return true;
        return false;
    }

    private ArrayList<Point> sortByXValue(ArrayList<Point> points) {
        ArrayList<Point> result = new ArrayList<>();
        result.add(points.get(0));
        for(int i = 1; i < points.size(); i++) {
            int k = 0;
            while ((k < result.size()) && (result.get(k).getX() < points.get(i).getX()))
                k++;
            if(k == result.size()) {
                result.add(points.get(i));
            } else {
                result.add(k, points.get(i));
            }
        }
        return result;
    }

    private ArrayList<Point> sortByYValue(ArrayList<Point> points) {
        ArrayList<Point> result = new ArrayList<>();
        result.add(points.get(0));
        for(int i = 1; i < points.size(); i++) {
            int k = 0;
            while ((k < result.size()) && (result.get(k).getY() < points.get(i).getY()))
                k++;
            if(k == result.size()) {
                result.add(points.get(i));
            } else {
                result.add(k, points.get(i));
            }
        }
        return result;
    }
}