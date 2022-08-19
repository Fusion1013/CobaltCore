package se.fusion1013.plugin.cobaltcore.util;

import java.util.*;

public class RandomCollection<T> {

    // ----- VARIABLES -----

    private final List<T> itemList = new ArrayList<>();
    private final Map<T, Double> trueWeightMap = new HashMap<>();
    private final Map<T, Double> valueKeyMap = new HashMap<>();
    private final NavigableMap<Double, T> map = new TreeMap<>();
    private final Random random;
    private double total;

    // ----- CONSTRUCTORS -----

    public RandomCollection() {
        this(new Random());
    }

    public RandomCollection(Random random) {
        this.random = random;
    }

    // ----- GETTERS / SETTERS -----

    public RandomCollection<T> addItem(double weight, T item) {
        if (weight <= 0) return this;
        total += weight;
        map.put(total, item);
        valueKeyMap.put(item, total);
        trueWeightMap.put(item, weight);
        itemList.add(item);
        return this;
    }

    public RandomCollection<T> removeItem(T item) {
        if (item == null) return this;
        double weight = valueKeyMap.get(item);
        map.remove(weight);
        valueKeyMap.remove(item);
        trueWeightMap.remove(item);
        itemList.remove(item);
        return this;
    }

    public T next() {
        double value = random.nextDouble() * total;
        Map.Entry<Double, T> item = map.higherEntry(value);
        if (item == null) return null;
        return item.getValue();
    }

    public List<T> getValues() {
        return itemList;
    }

    public Double[] getWeights() {
        return trueWeightMap.values().toArray(new Double[0]);
    }

}
