package algorithms.heuristicAlgorithms;

import algorithms.Algorithm;
import board.SudokuField;
import helpingFacilities.Pair;

import java.util.*;

public class YWing implements Algorithm {
    private final SudokuField sudoku;
    private int deleteValue = 0;
    Set<Integer> deleteIndexes = new HashSet<>();

    public YWing(SudokuField sudoku) {
        this.sudoku = sudoku;
        HashSet<Integer> s1 = new HashSet<>();
        solve();
    }

    @Override
    public void solve() {
        Set<Integer> appropriateIndexes = find2Values();
        Set<Set<Integer>> appropriateTriples = findTriples(appropriateIndexes);
        Set<Pair<Integer, Set<Integer>>> finalTriples = checkTriples(appropriateTriples);
        defineToDeleteItems(finalTriples);
        sudoku.deferredDeletion();
    }

    private Set<Integer> find2Values() {
        Set<Integer> appropriateIndexes = new HashSet<>();
        for (int i = 0; i < 9; ++i) {
            for (int j = 0; j < 9; ++j) {
                if (sudoku.getCell(i * 9 + j).getPossibleValues().size() == 2)
                    appropriateIndexes.add(i * 9 + j);
            }
        }
        return appropriateIndexes;
    }

    private Set<Set<Integer>> findTriples(Set<Integer> indexes) {
        Set<Set<Integer>> triples = new HashSet<>();
        for (var elem1 : indexes) {
            for (var elem2 : indexes) {
                for (var elem3 : indexes) {
                    Set<Integer> set1 = sudoku.getCell(elem1).getPossibleValues();
                    Set<Integer> set2 = sudoku.getCell(elem2).getPossibleValues();
                    Set<Integer> set3 = sudoku.getCell(elem3).getPossibleValues();
                    Set<Integer> appIndexes = new HashSet<>();
                    appIndexes.add(elem1);
                    appIndexes.add(elem2);
                    appIndexes.add(elem3);
                    if (check2vSets(set1, set2, set3)) {
                        triples.add(appIndexes);
                    }
                }
            }
        }
        return triples;
    }

    private boolean check2vSets(Set<Integer> set1, Set<Integer> set2, Set<Integer> set3) {
        Set<Integer> checkNotEq = new HashSet<>();
        checkNotEq.addAll(set1);
        checkNotEq.addAll(set2);
        checkNotEq.addAll(set3);
        if (checkNotEq.size() != 3)
            return false;
        checkNotEq.clear();
        checkNotEq.addAll(set1);
        checkNotEq.addAll(set2);
        if (checkNotEq.size() == 2)
            return false;
        checkNotEq.clear();
        checkNotEq.addAll(set1);
        checkNotEq.addAll(set3);
        if (checkNotEq.size() == 2)
            return false;
        checkNotEq.clear();
        checkNotEq.addAll(set2);
        checkNotEq.addAll(set3);
        return checkNotEq.size() != 2;
    }

    private Set<Pair<Integer, Set<Integer>>> checkTriples(Set<Set<Integer>> triples) {
        Set<Pair<Integer, Set<Integer>>> finalTriples = new HashSet<>();
        for (var triple : triples) {
            if (checkTriple(triple))
                finalTriples.add(makePair(triple));
        }
        return finalTriples;
    }

    private boolean checkTriple(Set<Integer> triple) {
        List<Integer> tripleList = new ArrayList<>(triple);
        if (sudoku.getCell(tripleList.get(1)).getInfluenceSphere().contains(tripleList.get(0)) &&
                sudoku.getCell(tripleList.get(2)).getInfluenceSphere().contains(tripleList.get(0))) {
            return true;
        } else if (sudoku.getCell(tripleList.get(1)).getInfluenceSphere().contains(tripleList.get(2)) &&
                sudoku.getCell(tripleList.get(0)).getInfluenceSphere().contains(tripleList.get(2))) {
            return true;
        } else if (sudoku.getCell(tripleList.get(2)).getInfluenceSphere().contains(tripleList.get(1)) &&
                sudoku.getCell(tripleList.get(0)).getInfluenceSphere().contains(tripleList.get(1))) {
            return true;
        } else
            return false;
    }

    private Pair<Integer, Set<Integer>> makePair(Set<Integer> triple) {
        List<Integer> tripleList = new ArrayList<>(triple);
        if (sudoku.getCell(tripleList.get(1)).getInfluenceSphere().contains(tripleList.get(0)) &&
                sudoku.getCell(tripleList.get(2)).getInfluenceSphere().contains(tripleList.get(0))) {
            Set<Integer> forPair = new HashSet<>();
            forPair.add(tripleList.get(1));
            forPair.add(tripleList.get(2));
            return (Pair<Integer, Set<Integer>>) new Pair(tripleList.get(0), forPair);
        } else if (sudoku.getCell(tripleList.get(1)).getInfluenceSphere().contains(tripleList.get(2)) &&
                sudoku.getCell(tripleList.get(0)).getInfluenceSphere().contains(tripleList.get(2))) {
            Set<Integer> forPair = new HashSet<>();
            forPair.add(tripleList.get(1));
            forPair.add(tripleList.get(0));
            return (Pair<Integer, Set<Integer>>) new Pair(tripleList.get(2), forPair);
        } else if (sudoku.getCell(tripleList.get(2)).getInfluenceSphere().contains(tripleList.get(1)) &&
                sudoku.getCell(tripleList.get(0)).getInfluenceSphere().contains(tripleList.get(1))) {
            Set<Integer> forPair = new HashSet<>();
            forPair.add(tripleList.get(0));
            forPair.add(tripleList.get(2));
            return (Pair<Integer, Set<Integer>>) new Pair(tripleList.get(1), forPair);
        } else
            return null;
    }

    private void defineToDeleteItems(Set<Pair<Integer, Set<Integer>>> triples) {
        for (Pair<Integer, Set<Integer>> pair : triples) {
            List<Integer> v2 = new ArrayList<>(pair.getValue());
            for (var digit : sudoku.getCell(v2.get(0)).getPossibleValues()) {
                if (!sudoku.getCell(pair.getKey()).getPossibleValues().contains(digit))
                    deleteValue = digit;
            }
            deleteIndexes.addAll(sudoku.getCell(v2.get(0)).getInfluenceSphere());
            deleteIndexes.retainAll(sudoku.getCell(v2.get(1)).getInfluenceSphere());
            deleteCandidates();
            deleteIndexes.clear();
            deleteValue = 0;
        }
    }

    private void deleteCandidates() {
        if (deleteIndexes.size() > 0) {
            for (var index : deleteIndexes) {
                sudoku.getCell(index).addValuesToRemove(deleteValue);
            }
        }
    }

    @Override
    public String tip() {
        return "you'll have to use Y-wing strategy.";
    }

    @Override
    public String strType() {
        return "Y-wing:";
    }
}
