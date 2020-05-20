/*
 Точки в евклідовому просторі, реалізовані як double[].
 Включає прості геометричні операції.
 Використовує матриці; матриця представлена у вигляді масиву точок.
 Використовує симплекси; симплекс представлений у вигляді масиву точок.
 */
public class Point {
    private double[] coordinates;          // координати точок

    Point(double... coords) {
        //Копіювання робиться тут для того, щоб не вдалося змінити координати точок.
        coordinates = new double[coords.length];
        System.arraycopy(coords, 0, coordinates, 0, coords.length);
    }

    //Переводить коодринати точкок в рядок
    public String toString() {
        if (coordinates.length == 0)
            return "()";
        var result = new StringBuilder("Points(" + coordinates[0]);
        for (int i = 1; i < coordinates.length; i++)
            result.append(",").append(coordinates[i]);
        result.append(")");
        return result.toString();
    }

    //Перевірка координат двої точок на рівність
    public boolean equals(Object other) {
        if (!(other instanceof Point))
            return false;
        var p = (Point) other;
        if (this.coordinates.length != p.coordinates.length)
            return false;
        for (int i = 0; i < this.coordinates.length; i++)
            if (this.coordinates[i] != p.coordinates[i])
                return false;
        return true;
    }

    //повертає хеш код для точки
    public int hashCode() {
        int hash = 0;
        for (double c : this.coordinates) {
            long bits = Double.doubleToLongBits(c);
            hash = (31 * hash) ^ (int) (bits ^ (bits >> 32));
        }
        return hash;
    }

    /* Точки як вектори */

    //повертає і-ту координату
    double coord(int i) {
        return this.coordinates[i];
    }

    //повертає кількість координат
    private int dimension() {
        return coordinates.length;
    }

    //перевірка чи збігаються розмірності
    private int dimCheck(Point p) {
        int len = this.coordinates.length;
        if (len != p.coordinates.length)
            throw new IllegalArgumentException("Dimension mismatch");
        return len;
    }

    //розширює Points за допомогою нових координат
    private Point extend(double... coords) {
        double[] result = new double[coordinates.length + coords.length];
        System.arraycopy(coordinates, 0, result, 0, coordinates.length);
        System.arraycopy(coords, 0, result, coordinates.length, coords.length);
        return new Point(result);
    }

    //скалярний добуток векторів
    private double dot(Point p) {
        int len = dimCheck(p);
        double sum = 0;
        for (int i = 0; i < len; i++)
            sum += this.coordinates[i] * p.coordinates[i];
        return sum;
    }

    //повертає евклідову довжину цього вектора
    double magnitude() {
        return Math.sqrt(this.dot(this));
    }

    //віднімання точок
    Point subtract(Point p) {
        int len = dimCheck(p);
        double[] coords = new double[len];
        for (int i = 0; i < len; i++)
            coords[i] = this.coordinates[i] - p.coordinates[i];
        return new Point(coords);
    }

    //додавання точок
    private Point add(Point p) {
        int len = dimCheck(p);
        double[] coords = new double[len];
        for (int i = 0; i < len; i++)
            coords[i] = this.coordinates[i] + p.coordinates[i];
        return new Point(coords);
    }

    /*
     * Перпендикулярна бісектриса двох точок.
     * Працює в будь-якому вимірі.
     * Коефіцієнти повертаються як наприклад, (A, B, C, D) для рівняння Ax + Від + Cz + D = 0).
     */
    private Point bisector(Point point) {
        var diff = this.subtract(point);
        var sum = this.add(point);
        double dot = diff.dot(sum);
        return diff.extend(-dot / 2);
    }

    //обчислення визначника матриці масиву точок
    private static double determinant(Point[] matrix) {
        if (matrix.length != matrix[0].dimension())
            throw new IllegalArgumentException("Matrix is not square");
        boolean[] columns = new boolean[matrix.length];
        for (int i = 0; i < matrix.length; i++)
            columns[i] = true;
        try {
            return determinant(matrix, 0, columns);
        } catch (ArrayIndexOutOfBoundsException e) {
            throw new IllegalArgumentException("Matrix is wrong shape");
        }
    }

    //Обчислення детермінанту підматриці, вказаної початковим рядком та "активними" стовпцями.
    private static double determinant(Point[] matrix, int row, boolean[] columns) {
        if (row == matrix.length)
            return 1;
        double sum = 0;
        int sign = 1;
        for (int col = 0; col < columns.length; col++) {
            if (!columns[col]) continue;
            columns[col] = false;
            sum += sign * matrix[row].coordinates[col] *
                    determinant(matrix, row + 1, columns);
            columns[col] = true;
            sign = -sign;
        }
        return sum;
    }

    /*
     *  Обчисли узагальнений поперечний добуток рядків матриці.
     *  Результат - перпендикуляр точок (як вектор) до кожного рядку матриці.
     */
    private static Point cross(Point[] matrix) {
        int len = matrix.length + 1;
        if (len != matrix[0].dimension())
            throw new IllegalArgumentException("Dimension mismatch");
        boolean[] columns = new boolean[len];
        for (int i = 0; i < len; i++)
            columns[i] = true;
        double[] result = new double[len];
        int sign = 1;
        try {
            for (int i = 0; i < len; i++) {
                columns[i] = false;
                result[i] = sign * determinant(matrix, 0, columns);
                columns[i] = true;
                sign = -sign;
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            throw new IllegalArgumentException("Matrix is wrong shape");
        }
        return new Point(result);
    }

    /* точки як симплекси */

    /**
     * Determine the signed content (i.e., area or volume, etc.) of a simplex.
     *
     * @param simplex the simplex (as an array of Pnts)
     * @return the signed content of the simplex
     */
    private static double content(Point[] simplex) {
        var matrix = new Point[simplex.length];
        for (int i = 0; i < matrix.length; i++)
            matrix[i] = simplex[i].extend(1);
        int fact = 1;
        for (int i = 1; i < matrix.length; i++)
            fact = fact * i;
        return determinant(matrix) / fact;
    }

    /**
     * Relation between this Pnt and a simplex (represented as an array of Pnts).
     * Result is an array of signs, one for each vertex of the simplex, indicating
     * the relation between the vertex, the vertex's opposite facet, and this
     * Pnt. <pre>
     *   -1 means Pnt is on same side of facet
     *    0 means Pnt is on the facet
     *   +1 means Pnt is on opposite side of facet</pre>
     *
     * @param simplex an array of Pnts representing a simplex
     * @return an array of signs showing relation between this Pnt and the simplex
     * @throws IllegalArgumentException if the simplex is degenerate
     */
    private int[] relation(Point[] simplex) {
        /* In 2D, we compute the cross of this matrix:
         *    1   1   1   1
         *    p0  a0  b0  c0
         *    p1  a1  b1  c1
         * where (a, b, c) is the simplex and p is this Pnt.  The result
         * is a vector in which the first coordinate is the signed area
         * (all signed areas are off by the same constant factor) of
         * the simplex and the remaining coordinates are the *negated*
         * signed areas for the simplices in which p is substituted for
         * each of the vertices. Analogous results occur in higher dimensions.
         */
        int dim = simplex.length - 1;
        if (this.dimension() != dim)
            throw new IllegalArgumentException("Dimension mismatch");

        /* Create and load the matrix */
        var matrix = new Point[dim + 1];
        /* First row */
        double[] coords = new double[dim + 2];
        for (int j = 0; j < coords.length; j++)
            coords[j] = 1;
        matrix[0] = new Point(coords);
        /* Other rows */
        for (int i = 0; i < dim; i++) {
            coords[0] = this.coordinates[i];
            for (int j = 0; j < simplex.length; j++)
                coords[j + 1] = simplex[j].coordinates[i];
            matrix[i + 1] = new Point(coords);
        }

        /* Compute and analyze the vector of areas/volumes/contents */
        var vector = cross(matrix);
        double content = vector.coordinates[0];
        int[] result = new int[dim + 1];
        for (int i = 0; i < result.length; i++) {
            double value = vector.coordinates[i + 1];
            if (Math.abs(value) <= 1.0e-6 * Math.abs(content))
                result[i] = 0;
            else if (value < 0) result[i] = -1;
            else result[i] = 1;
        }
        if (content < 0)
            for (int i = 0; i < result.length; i++)
                result[i] = -result[i];
        if (content == 0)
            for (int i = 0; i < result.length; i++)
                result[i] = Math.abs(result[i]);
        return result;
    }

    /**
     * Test if this Pnt is outside of simplex.
     *
     * @param simplex the simplex (an array of Pnts)
     * @return the simplex Pnt that "witnesses" outsideness (or null if not outside)
     */
    Point isOutside(Point[] simplex) {
        int[] result = this.relation(simplex);
        for (int i = 0; i < result.length; i++)
            if (result[i] > 0)
                return simplex[i];
        return null;
    }

    /**
     * Test relation between this Pnt and circumcircle of a simplex.
     *
     * @param simplex the simplex (as an array of Pnts)
     * @return -1, 0, or +1 for inside, on, or outside of circumcircle
     */
    int vsCircumcircle(Point[] simplex) {
        var matrix = new Point[simplex.length + 1];
        for (int i = 0; i < simplex.length; i++)
            matrix[i] = simplex[i].extend(1, simplex[i].dot(simplex[i]));
        matrix[simplex.length] = this.extend(1, this.dot(this));
        double d = determinant(matrix);
        int result = (d < 0) ? -1 : ((d > 0) ? +1 : 0);
        if (content(simplex) < 0)
            result = -result;
        return result;
    }

    //Круговий центр симплекса.
    static Point circumcenter(Point[] simplex) {
        int dim = simplex[0].dimension();
        if (simplex.length - 1 != dim)
            throw new IllegalArgumentException("Dimension mismatch");
        var matrix = new Point[dim];
        for (int i = 0; i < dim; i++)
            matrix[i] = simplex[i].bisector(simplex[i + 1]);
        var hCenter = cross(matrix);      // Центр в однорідних координатах
        double last = hCenter.coordinates[dim];
        double[] result = new double[dim];
        for (int i = 0; i < dim; i++)
            result[i] = hCenter.coordinates[i] / last;
        return new Point(result);
    }
}