package algorithms.heuristicAlgorithms;

import algorithms.Algorithm;
import board.SudokuField;

import java.util.*;

public class XWing implements Algorithm {
    private final SudokuField sudoku;
    private int deleteValue = 0;
    Set<Integer> unDeleteRows = new HashSet<>();
    Set<Integer> deleteColumns = new HashSet<>();
    Map<Integer, List<Integer>> valueRows = new HashMap<>();
    Map<Integer, List<Set<Integer>>> valueColumns = new HashMap<>();

    public XWing(SudokuField sudoku) {
        this.sudoku = sudoku;
        solve();
    }

    @Override
    public void solve() {
        rowXWing();
    }

    private void rowXWing() {
        for (int i = 0; i < 9; ++i) {
            int[] digitQuantity = new int[10];
            for (int j = 0; j < 9; ++j) {
                for (int digit : sudoku.getCell(i * 9 + j).getPossibleValues()) {
                    digitQuantity[digit]++;
                }
            }
            mapsFilling(i, digitQuantity);
        }
    }

    private void mapsFilling(int i, int[] digitQuantity) {
        int forRows;
        for (int k = 0; k < 10; ++k) {
            if (digitQuantity[k] == 2) {
                Set<Integer> forColumns = new HashSet<>();
                forRows = i;
                for (int j = 0; j < 9; ++j) {
                    if (sudoku.getCell(i * 9 + j).getPossibleValues().contains(k)) {
                        forColumns.add(j);
                    }
                }
                if (valueColumns.containsKey(k)) {
                    List<Set<Integer>> columns = valueColumns.get(k);
                    boolean indic = false;
                    for (int m = 0; m < columns.size(); m++) {
                        if (columns.get(m).equals(forColumns)) {
                            deleteValue = k;
                            unDeleteRows.add(forRows);
                            unDeleteRows.add(valueRows.get(k).get(m));
                            deleteColumns.addAll(forColumns);
                            deleteCandidates();
                            unDeleteRows.clear();
                            deleteColumns.clear();
                            indic = true;
                        }
                    }
                    if (!indic) {
                        valueRows.get(k).add(forRows);
                        valueColumns.get(k).add(forColumns);
                    }
                } else {
                    valueRows.put(k, new ArrayList<>());
                    valueRows.get(k).add(forRows);
                    valueColumns.put(k, new ArrayList<>());
                    valueColumns.get(k).add(forColumns);
                }
            }
        }
    }

    private void deleteCandidates() {
        if (deleteValue != 0){
            for (var column: deleteColumns) {
                for(int i = 0; i < 9; ++i){
                    if (!unDeleteRows.contains(i)){
                        Set<Integer> candidates =
                                new HashSet<>(sudoku.getCell(i * 9 + column).getPossibleValues());
                        for(var digit: candidates){
                            if(digit.equals(deleteValue))
                                sudoku.getCell(i * 9 + column).removeValue(deleteValue);
                        }
                    }
                }
            }

        }
    }

    @Override
    public String tip() {
        return "you should use X-wing strategy.";
    }

    @Override
    public String strType() {
        return "X-wing:";
    }
}
