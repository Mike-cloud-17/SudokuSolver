package algorithms.heuristicAlgorithms;

import algorithms.Algorithm;
import board.SudokuField;

import java.util.*;

public class HiddenPares implements Algorithm {
    private final SudokuField sudoku;

    public HiddenPares(SudokuField sudoku) {
        this.sudoku = sudoku;
        solve();
    }

    @Override
    public void solve() {
        hiddenRowPares();
        hiddenColumnPares();
        hiddenSectionPares();
    }

    private void hiddenRowPares() {
        for (int i = 0; i < 9; ++i) {
            Map<Integer, Set<Integer>> digitQuantity = new HashMap<>();
            for (int j = 0; j < 9; ++j) {
                for (int digit : sudoku.getCell(i * 9 + j).getPossibleValues()) {
                    Set<Integer> cells = digitQuantity.get(digit);
                    if (cells == null) {
                        cells = new HashSet<Integer>();
                        cells.add(i * 9 + j);
                        digitQuantity.put(digit, cells);
                    } else {
                        cells.add(i * 9 + j);
                        digitQuantity.put(digit, cells);
                    }
                }
            }

            Map<Set<Integer>, List<Integer>> forKill = makeForKill(digitQuantity);
            lookForInd(forKill);
        }
    }

    private void hiddenColumnPares() {
        for (int i = 0; i < 9; ++i) {
            Map<Integer, Set<Integer>> digitQuantity = new HashMap<>();
            for (int j = 0; j < 9; ++j) {
                for (int digit : sudoku.getCell(j * 9 + i).getPossibleValues()) {
                    Set<Integer> cells = digitQuantity.get(digit);
                    if (cells == null) {
                        cells = new HashSet<Integer>();
                        cells.add(j * 9 + i);
                        digitQuantity.put(digit, cells);
                    } else {
                        cells.add(j * 9 + i);
                        digitQuantity.put(digit, cells);
                    }
                }
            }

            Map<Set<Integer>, List<Integer>> forKill = makeForKill(digitQuantity);
            lookForInd(forKill);
        }
    }

    private void hiddenSectionPares() {
        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 3; ++j) {
                hiddenSection(i * 3, i * 3 + 2, j * 3, j * 3 + 2);
            }
        }
    }

    private void hiddenSection(int sHor, int eHor, int sVert, int eVert) {
        Map<Integer, Set<Integer>> digitQuantity = new HashMap<>();
        for (int i = sHor; i < eHor + 1; ++i) {
            for (int j = sVert; j < eVert + 1; ++j) {
                for (int digit : sudoku.getCell(i * 9 + j).getPossibleValues()) {
                    Set<Integer> cells = digitQuantity.get(digit);
                    if (cells == null) {
                        cells = new HashSet<Integer>();
                        cells.add(i * 9 + j);
                        digitQuantity.put(digit, cells);
                    } else {
                        cells.add(i * 9 + j);
                        digitQuantity.put(digit, cells);
                    }
                }
            }
        }

        Map<Set<Integer>, List<Integer>> forKill = makeForKill(digitQuantity);
        lookForInd(forKill);
    }

    private Map<Set<Integer>, List<Integer>> makeForKill(Map<Integer, Set<Integer>> digitQuantity) {
        Map<Set<Integer>, List<Integer>> forKill = new HashMap<>();
        for (var key : digitQuantity.keySet()) {
            var indexes = digitQuantity.get(key);
            if (indexes.size() == 2) {
                if (!forKill.containsKey(indexes)) {
                    forKill.put(indexes, new ArrayList<>());
                }
                forKill.get(indexes).add(key);
            }
        }

        Map<Set<Integer>, List<Integer>> finalForKill = new HashMap<>();
        for (var key : forKill.keySet()) {
            if (forKill.get(key).size() == 2)
                finalForKill.put(key, forKill.get(key));
        }
        return finalForKill;
    }

    private void lookForInd(Map<Set<Integer>, List<Integer>> forKill) {
        for (var indexSet : forKill.keySet()) {
            for (var index : indexSet) {
                Set<Integer> possVs = new HashSet<>(sudoku.getCell(index).getPossibleValues());
                for (var digit : possVs) {
                    if (!forKill.get(indexSet).contains(digit)) {
                        sudoku.getCell(index).removeValue(digit);
                    }
                }
            }
        }
    }

    @Override
    public String tip() {
        return "it is necessary to find a hidden pair." +
                "For some pair of numbers, there will be only 2 available cells in a row/column/box. " +
                "Therefore, all other candidates from these cells can be removed.";
    }

    @Override
    public String strType() {
        return "HiddenPares:";
    }
}
