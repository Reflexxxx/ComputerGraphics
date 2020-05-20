import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.Set;

//Створює і відображає триангуляцію Делоне (DT) і діаграму Вороного (VoD).
@SuppressWarnings("deprecation")
public class Runner extends javax.swing.JApplet implements Runnable {

    //Як рекомендується, фактичне використання компонентів Swing відбувається в потоці диспетчеризації подій.
    public void init() {
        try {
            SwingUtilities.invokeAndWait(this);
        } catch (Exception e) {
            System.err.println("Initialization failure");
        }
    }

    //Налаштувати графічний інтерфейс. Як рекомендовано, метод init виконує це в потоці диспетчеризації подій.
    public void run() {
        setLayout(new BorderLayout());

        var clearButton = new JButton("Очистити поле");
        clearButton.setActionCommand("clear");

        var buttonPanel = new JPanel();
        buttonPanel.add(clearButton);
        this.add(buttonPanel, "North");

        // Побудуємо графічну панель
        var graphicsPanel = new Panel();
        graphicsPanel.setBackground(Color.WHITE);
        this.add(graphicsPanel, "Center");

        // Зарегеструємо слухачів
        clearButton.addActionListener(graphicsPanel);
        graphicsPanel.addMouseListener(graphicsPanel);

    }

    public static void main(String[] args) {
        var applet = new Runner();
        applet.init();
        var dWindow = new JFrame();
        dWindow.setSize(1200, 650);
        dWindow.setTitle("Voronoi/Delaunay");
        dWindow.setLayout(new BorderLayout());
        dWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        dWindow.add(applet, "Center");
        dWindow.setVisible(true);
    }
}

// Графічна панель для Runner.
class Panel extends JPanel implements ActionListener, MouseListener {
    private DelaunayTriangulation dt;
    private Simplex<Point> initialTriangle;

    private Color voronoiColor = Color.LIGHT_GRAY;
    private Color delaunayColor = Color.BLUE;
    private Graphics g;                   // Зберігається графічний контекст

    Panel() {
        // Керує розміром початкового трикутника
        int initialSize = 10000;
        initialTriangle = new Simplex<>(new Point(-initialSize, -initialSize),
                new Point(initialSize, -initialSize),
                new Point(0, initialSize));
        dt = new DelaunayTriangulation(initialTriangle);
    }

    /* Події */


    //події по натиску на кнопки
    public void actionPerformed(ActionEvent e) {
        if(e.getActionCommand() == "clear")
            dt = new DelaunayTriangulation(initialTriangle);
        repaint();
    }

    //Обробка натиску мишки.
    public void mousePressed(MouseEvent e) {
        if (e.getComponent() != this) return;
        Point point = new Point(e.getX(), e.getY());
        dt.delaunayPlace(point);
        repaint();
    }

    public void mouseEntered(MouseEvent e) {    }

    public void mouseExited(MouseEvent e) {
    }

    public void mouseClicked(MouseEvent e) { }

    public void mouseReleased(MouseEvent e) { }

    /* Базові методи для малювання */

    //Малювання точки
    private void draw(Point point) {
        int r = 3;
        int x = (int) point.coord(0);
        int y = (int) point.coord(1);
        g.fillOval(x - r, y - r, r + r, r + r);
    }

    //Малює відрізок
    private void draw(Point endA, Point endB) {
        g.drawLine((int) endA.coord(0), (int) endA.coord(1),
                (int) endB.coord(0), (int) endB.coord(1));
    }

    //Малює коло
    private void draw(Point center, double radius) {
        int x = (int) center.coord(0);
        int y = (int) center.coord(1);
        int r = (int) radius;
        g.drawOval(x - r, y - r, r + r, r + r);
    }

    /* Методи малювання вищого рівня */

    //Обробляє малювання всього вмісту DelaunayPanel.
    //Викликається автоматично; запитується через call to repaint().
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        this.g = g;

        g.setColor(this.getBackground());
        g.fillRect(0, 0, this.getWidth(), this.getHeight());

        g.setColor(voronoiColor);
        drawAllVoronoi();
        g.setColor(Color.BLACK);
        drawAllSites();
        g.setColor(delaunayColor);
        drawAllDelaunay();
        g.setColor(Color.PINK);
        drawAllCircles();

    }

    //Побудова триангуляції Делоне
    private void drawAllDelaunay() {
        for (Simplex<Point> triangle : dt)
            for (Set<Point> edge : triangle.facets()) {
                var endpoint = edge.toArray(new Point[2]);
                draw(endpoint[0], endpoint[1]);
            }
    }

    //Побудова діаграми Вороного
    private void drawAllVoronoi() {
        for (Simplex<Point> triangle : dt)
            for (Simplex<Point> other : dt.neighbors(triangle)) {
                Point p = Point.circumcenter(triangle.toArray(new Point[0]));
                Point q = Point.circumcenter(other.toArray(new Point[0]));
                draw(p, q);
            }
    }

    //Побудова вхідних точок - вершин триангуляції Делоне
    private void drawAllSites() {
        for (Simplex<Point> triangle : dt)
            for (Point site : triangle)
                draw(site);
    }


    //Малює всі пусті кола (по одному на кожний трикутник) для триангуляції Дедоне
    private void drawAllCircles() {
        loop:
        for (Simplex<Point> triangle : dt) {
            for (Point p : initialTriangle)  // Skip circles invoving initialTriangle
                if (triangle.contains(p)) continue loop;
            Point c = Point.circumcenter(triangle.toArray(new Point[0]));
            double radius = c.subtract(triangle.iterator().next()).magnitude();
            draw(c, radius);
        }
    }


}
 