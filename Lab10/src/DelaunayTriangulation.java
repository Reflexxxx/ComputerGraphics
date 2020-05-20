import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

//2D триангуляція Делоне (DT) інкрементрний алгоритм.
class DelaunayTriangulation extends Triangulation<Point> {

    private Simplex<Point> mostRecent;  // Most recently inserted triangle

    //Всі точки повинні входити в початкову триангуляцію
    DelaunayTriangulation(Simplex<Point> triangle) {
        super(triangle);
        mostRecent = triangle;
    }

    //Знайдіть трикутник з точкою всередині (або на).
    private Simplex<Point> locate(Point point) {
        Simplex<Point> triangle = mostRecent;
        if (!this.contains(triangle))
            triangle = null;

        Set<Simplex<Point>> visited = new HashSet<>();
        while (triangle != null) {
            if (visited.contains(triangle))
                break;
            visited.add(triangle);
            // Кут навпроти точки
            Point corner = point.isOutside(triangle.toArray(new Point[0]));
            if (corner == null)
                return triangle;
            triangle = this.neighborOpposite(corner, triangle);
        }
        // Не вдалося; спробуємо перебір
        for (Simplex<Point> tri : this)
            if (point.isOutside(tri.toArray(new Point[0])) == null)
                return tri;
        // Такого трикутника немає
        return null;
    }

    //Помістіть нову точкову ділянку в DT.
    void delaunayPlace(Point site) {
        Set<Simplex<Point>> newTriangles = new HashSet<>();
        Set<Simplex<Point>> oldTriangles = new HashSet<>();
        Set<Simplex<Point>> doneSet = new HashSet<>();
        Queue<Simplex<Point>> waitingQ = new LinkedList<>();

        // Розташування трикутника
        Simplex<Point> triangle = locate(site);

        // Відмовтеся, якщо трикутник не містить або точка уже в DT
        if (triangle == null || triangle.contains(site))
            return;

        // Знайдіть порожнину Делоне (ті трикутники з розташуванням в їх окружності)
        waitingQ.add(triangle);
        while (!waitingQ.isEmpty()) {
            triangle = waitingQ.remove();
            if (site.vsCircumcircle(triangle.toArray(new Point[0])) == 1)
                continue;
            oldTriangles.add(triangle);
            for (Simplex<Point> tri : this.neighbors(triangle)) {
                if (doneSet.contains(tri))
                    continue;
                doneSet.add(tri);
                waitingQ.add(tri);
            }
        }
        // Створіть нові трикутники
        for (Set<Point> facet : Simplex.boundary(oldTriangles)) {
            facet.add(site);
            newTriangles.add(new Simplex<>(facet));
        }
        // Замініть старі трикутники на нові трикутники
        this.update(oldTriangles, newTriangles);

        // Оновлення самого останнього трикутника
        if (!newTriangles.isEmpty()) mostRecent = newTriangles.iterator().next();
    }
}