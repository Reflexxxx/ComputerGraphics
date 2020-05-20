// Дуга параболи
public class Arc extends ArcKey {
    private final Voronoi v;
    public BreakPoint left, right;
    public final Point point;

    public Arc(BreakPoint left, BreakPoint right, Voronoi v) {
        this.v = v;
        if (left == null && right == null)
            throw new RuntimeException("Неможливо створити дугу без брейкпоінтів");
        this.left = left;
        this.right = right;
        this.point = left != null ? left.s2 : right.s1;
    }

    // Створення першої дуги для задачі
    public Arc(Point point, Voronoi v) {
        this.v = v;
        this.left = null;
        this.right = null;
        this.point = point;
    }

    // Отримати точку справа
    protected Point getRight() {
        if (right != null)
            return right.getPoint();
        return new Point(Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY);
    }

    // Отримати точку зліва
    protected Point getLeft() {
        if (left != null)
            return left.getPoint();
        return new Point(Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY);
    }

    // Малювання дуги за параболою
    public void draw() {
        var l = getLeft();
        var r = getRight();

        var par = new Parabola(point, v.getSweepingStraight());
        double min = l.x == Double.NEGATIVE_INFINITY ? Voronoi.MIN_DRAW_DIM : l.x;
        double max = r.x == Double.POSITIVE_INFINITY ? Voronoi.MAX_DRAW_DIM : r.x;
        par.draw(min, max);
    }

    public String toString() {
        var l = getLeft();
        var r = getRight();
        return String.format("{%.4f, %.4f}", l.x, r.x);
    }

    // Перевірка на наявність кола
    public Point checkCircle() {
        if (this.left == null || this.right == null)
            return null;
        if (Point.triangle_area_2(this.left.s1, this.point, this.right.s2) != -1)
            return null;
        return this.left.getEdge().intersection(this.right.getEdge());
    }
}