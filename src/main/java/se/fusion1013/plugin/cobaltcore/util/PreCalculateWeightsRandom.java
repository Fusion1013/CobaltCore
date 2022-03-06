package se.fusion1013.plugin.cobaltcore.util;

import java.util.*;

public class PreCalculateWeightsRandom<T> {

    int totalWeight = 0;
    List<T> itemList = new ArrayList<>();
    List<Integer> weightList = new ArrayList<>();
    List<Integer> trueWeightList = new ArrayList<>();
    Random r = new Random();

    public List<T> getItems() {
        return itemList;
    }

    public List<Integer> getWeights() {
        return weightList;
    }

    public List<Integer> getTrueWeights() {
        return trueWeightList;
    }

    public void addItem(T item, int weight) {
        itemList.add(item);
        totalWeight += weight;
        weightList.add(totalWeight);
        trueWeightList.add(weight);
    }

    public void removeItem(T item) {
        int index = itemList.indexOf(item);
        itemList.remove(index);
        int weight = weightList.get(index);
        weightList.remove(index);
        for (int i = index; i < weightList.size(); i++) {
            weightList.set(i, weightList.get(i) - weight);
        }
    }

    public T chooseOne() {
        if (itemList.size() <= 0) return null;

        int random = r.nextInt(0, itemList.size());
        return itemList.get(random);

        /* TODO:
        int random = r.nextInt(Math.max(totalWeight - 1, 1));
        int index = binarySearchBoundary(weightList, random);
        return itemList.get(index);
         */
    }

    int binarySearchBoundary(List<Integer> list, int value) {
        int l = 0;
        int r = list.size() - 1;

        while (l < r) {
            int mid = (l + r) / 2;
            if (list.get(mid) < value) l = mid + 1;
            else r = mid;
        }

        return r;
    }
}
