import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/*
 * Тріангуляція вершин.
 * Тріангуляція - це набір Simplices (див. Simplex нижче).
 * Для ефективності ми стежимо за сусідами кожного Simplex.
 * Два симплеса є сусідами, якщо вони поділяють грань.
 */
public class Triangulation<V> implements Iterable<Simplex<V>> {
    private HashMap<Simplex<V>, HashSet<Simplex<V>>> neighbors;  // Maps Simplex to neighbors

    Triangulation(Simplex<V> simplex) {
        neighbors = new HashMap<>();
        neighbors.put(simplex, new HashSet<>());
    }

    // Показує кількість симплетів, які зараз перебувають у триангуляції.
    public String toString() {
        return "Triangulation (with " + neighbors.size() + " elements)";
    }

    //Правда, якщо симплекс є в цій триангуляції.
    boolean contains(Simplex<V> simplex) {
        return this.neighbors.containsKey(simplex);
    }

    //повертає ітератор для кожного симплекса в триангуляції
    public Iterator<Simplex<V>> iterator() {
        return Collections.unmodifiableSet(this.neighbors.keySet()).iterator();
    }

    /* Навігація */

    // Повідомляє сусіда навпроти заданої вершини симплексу.
    Simplex<V> neighborOpposite(Object vertex, Simplex<V> simplex) {
        if (!simplex.contains(vertex))
            throw new IllegalArgumentException("Bad vertex; not in simplex");
        SimplexLoop:
        for (var s : neighbors.get(simplex)) {
            for (V v : simplex) {
                if (v.equals(vertex))
                    continue;
                if (!s.contains(v))
                    continue SimplexLoop;
            }
            return s;
        }
        return null;
    }

    // Повідомляє про сусідів даного симплексу.
    Set<Simplex<V>> neighbors(Simplex<V> simplex) {
        return new HashSet<>(this.neighbors.get(simplex));
    }

    /* Модифікація */

    /* Оновлення, замінивши один набір Simplices іншим.
       Обидва набори симплетів повинні заповнити одну і ту ж «дірку» у Тріангуляції.*/
    void update(Set<? extends Simplex<V>> oldSet,
                Set<? extends Simplex<V>> newSet) {
        // Зберемо всі симплекси, сусідні зі oldSet
        var allNeighbors = new HashSet<Simplex<V>>();
        for (var simplex : oldSet)
            allNeighbors.addAll(neighbors.get(simplex));
        // видалення oldSet
        for (var simplex : oldSet) {
            for (var n : neighbors.get(simplex))
                neighbors.get(n).remove(simplex);
            neighbors.remove(simplex);
            allNeighbors.remove(simplex);
        }
        // Влючити newSet simplices як можливого сусіда
        allNeighbors.addAll(newSet);
        //Створіть записи для симплетів у newEst
        for (var s : newSet)
            neighbors.put(s, new HashSet<Simplex<V>>());
        // оновити всю інформацію про сусідів
        for (var s1 : newSet)
            for (var s2 : allNeighbors) {
                if (!s1.isNeighbor(s2))
                    continue;
                neighbors.get(s1).add(s2);
                neighbors.get(s2).add(s1);
            }
    }
}