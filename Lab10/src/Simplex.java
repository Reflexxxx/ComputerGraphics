import java.util.AbstractSet;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

//Симплекс - це незмінний набір вершин
 public class Simplex<V> extends AbstractSet<V> implements Set<V> {
    private List<V> vertices;
    private long idNumber;
    private static long idGenerator = 0;    // використовується для створення idNumber

    Simplex(Collection<? extends V> collection) {
        this.vertices = List.copyOf(collection);
        this.idNumber = idGenerator++;
        var noDups = new HashSet<>(this);
        if (noDups.size() != this.vertices.size())
            throw new IllegalArgumentException("Duplicate vertices in Simplex");
    }

    @SafeVarargs
    Simplex(V... vertices) {
        this(Arrays.asList(vertices));
    }

    public String toString() {
        return "Simplex" + idNumber;
    }

    // Повертає істику, коли симплекси є сусідами.
    // Два симплекси є сусідами, якщо вони однакового виміру і вони ділять грань
    boolean isNeighbor(Simplex<V> simplex) {
        var h = new HashSet<>(this);
        h.removeAll(simplex);
        return (this.size() == simplex.size()) && (h.size() == 1);
    }

    //повертає грані симплексу
    List<Set<V>> facets() {
        var theFacets = new LinkedList<Set<V>>();
        for (V v : this) {
            var facet = new HashSet<>(this);
            facet.remove(v);
            theFacets.add(facet);
        }
        return theFacets;
    }

    //Повідомляє про межу набору Simplices.
    //Межа - це сукупність граней, де кожна грань є набором вершин <V>.
    //Повертає ітератор для граней, що складають межу
    static <V> Set<Set<V>> boundary(Set<? extends Simplex<V>> simplexSet) {
        var theBoundary = new HashSet<Set<V>>();
        for (var simplex : simplexSet) {
            for (var facet : simplex.facets()) {
                if (theBoundary.contains(facet))
                    theBoundary.remove(facet);
                else theBoundary.add(facet);
            }
        }
        return theBoundary;
    }

    //ітератор для вершин симплексу
    public Iterator<V> iterator() {
        return this.vertices.iterator();
    }

    //кількість вершин в симплексі
    public int size() {
        return this.vertices.size();
    }

    //хеш код симплексу
    public int hashCode() {
        return (int) (idNumber ^ (idNumber >>> 32));
    }

    //Ми хочемо дозволити різні симплекти, які мають однаковий набір вершин.
    public boolean equals(Object o) {
        return (this == o);
    }
}