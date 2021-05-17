package board;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class Cell {
    private final Set<Integer> possibleValues;
    private int finalValue;
    private Set<Integer> influenceSphere;
    private final Set<Integer> valuesToRemove = new HashSet<>();

    public Cell() {
        possibleValues = new HashSet<>();
        for (int i = 1; i < 10; i++) {
            possibleValues.add(i);
        }
        finalValue = 0;
    }

    public Cell(int num) {
        possibleValues = new HashSet<>();
        possibleValues.add(num);
        finalValue = num;
    }

    public void addValuesToRemove(int value){
        valuesToRemove.add(value);
    }

    public Set<Integer> getRemoveValues(){
        return valuesToRemove;
    }

    public int getFinalValue() {
        return finalValue;
    }

    public Set<Integer> getPossibleValues() {
        return possibleValues;
    }

    public void setValue(int figure) {
        possibleValues.clear();
        //possibleValues.add(figure);
        finalValue = figure;
    }

    public void setValueInitial(int figure) {
        finalValue = figure;
    }

    public void removeValue(int num) {
        //if (possibleValues.size() > 1)
            possibleValues.remove(num);
        if (possibleValues.size() == 1) {
            Iterator<Integer> it = possibleValues.iterator();
            finalValue = it.next();
            possibleValues.clear();
        }
    }

    public Set<Integer> getInfluenceSphere() {
        return influenceSphere;
    }

    public void setInfluenceSphere(Set<Integer> influenceSphere) {
        this.influenceSphere = influenceSphere;
    }
}
