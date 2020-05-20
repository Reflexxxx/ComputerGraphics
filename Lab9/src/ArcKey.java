// Точки, на яких закріплена дуга параболи
public abstract class ArcKey implements Comparable<ArcKey> {
    protected abstract Point getLeft();
    protected abstract Point getRight();

    // Порівняння точок дуги параболи
    @Override
    public int compareTo(ArcKey that) {
        var myLeft = this.getLeft();
        var myRight = this.getRight();
        var yourLeft = that.getLeft();
        var yourRight = that.getRight();

        // Перевірка дуг на рівність
        if ((that.getClass() == ArcQuery.class || this.getClass() == ArcQuery.class) &&
                (myLeft.x <= yourLeft.x && myRight.x >= yourRight.x ||
                        yourLeft.x <= myLeft.x && yourRight.x >= myRight.x))
            return 0;

        if (myLeft.x == yourLeft.x && myRight.x == yourRight.x)
            return 0;
        if (myLeft.x >= yourRight.x)
            return 1;
        if (myRight.x <= yourLeft.x)
            return -1;

        return Point.midpoint(myLeft, myRight).compareTo(Point.midpoint(yourLeft, yourRight));
    }
}