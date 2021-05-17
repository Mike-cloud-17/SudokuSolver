package algorithms.heuristicAlgorithms;

import algorithms.Algorithm;
import board.SudokuField;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class NakedPares implements Algorithm {
    private final SudokuField sudoku;

    public NakedPares(SudokuField sudoku) {
        this.sudoku = sudoku;
        solve();
    }

    @Override
    public void solve() {
        nakedRowPares();
        nakedColumnPares();
        nakedSectionPares();
    }

    private void nakedRowPares() {
        for (int i = 0; i < 9; ++i) {
            List<Set<Integer>> globPares = new ArrayList<>();
            Set<Set<Integer>> forKillPares;
            Set<Integer> forKill = new HashSet<>();
            for (int j = 0; j < 9; ++j) {
                if (sudoku.getCell(i * 9 + j).getPossibleValues().size() == 2) {
                    forKill.addAll(sudoku.getCell(i * 9 + j).getPossibleValues());
                    globPares.add(forKill);
                    forKill = new HashSet<>();
                }
            }
            forKillPares = checkDuplicates(globPares);
            killRowPairs(i, forKillPares);
        }
    }

    private void killRowPairs(int i, Set<Set<Integer>> forKillPares) {
        for (var pair : forKillPares) {
            for (int j = 0; j < 9; ++j) {
                if (!sudoku.getCell(i * 9 + j).getPossibleValues().equals(pair)) { // Проверить работает ли
                    for (int digit : pair) {
                        if (sudoku.getCell(i * 9 + j).getPossibleValues().contains(digit))
                            sudoku.getCell(i * 9 + j).removeValue(digit);
                    }
                }
            }
        }
    }

    private void nakedColumnPares() {
        for (int i = 0; i < 9; ++i) {
            List<Set<Integer>> globPares = new ArrayList<>();
            Set<Set<Integer>> forKillPares;
            Set<Integer> forKill = new HashSet<>();
            for (int j = 0; j < 9; ++j) {
                if (sudoku.getCell(j * 9 + i).getPossibleValues().size() == 2) {
                    forKill.addAll(sudoku.getCell(j * 9 + i).getPossibleValues());
                    globPares.add(forKill);
                    forKill = new HashSet<>();
                }
            }
            forKillPares = checkDuplicates(globPares);
            killColumnPairs(i, forKillPares);
        }
    }

    private void killColumnPairs(int i, Set<Set<Integer>> forKillPares) {
        for (var pair : forKillPares) {
            for (int j = 0; j < 9; ++j) {
                if (!sudoku.getCell(j * 9 + i).getPossibleValues().equals(pair)) { // Проверить работает ли
                    for (int digit : pair) {
                        if (sudoku.getCell(j * 9 + i).getPossibleValues().contains(digit))
                            sudoku.getCell(j * 9 + i).removeValue(digit);
                    }
                }
            }
        }
    }

    private void nakedSectionPares() {
        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 3; ++j) {
                nakedSection(i * 3, i * 3 + 2, j * 3, j * 3 + 2);
            }
        }
    }

    private void nakedSection(int sHor, int eHor, int sVert, int eVert) {
        List<Set<Integer>> globPares = new ArrayList<>();
        Set<Set<Integer>> forKillPares;
        Set<Integer> forKill = new HashSet<>();
        for (int i = sHor; i < eHor + 1; ++i) {
            for (int j = sVert; j < eVert + 1; ++j) {
                if (sudoku.getCell(i * 9 + j).getPossibleValues().size() == 2) {
                    forKill.addAll(sudoku.getCell(i * 9 + j).getPossibleValues());
                    globPares.add(forKill);
                    forKill = new HashSet<>();
                }
            }
        }
        forKillPares = checkDuplicates(globPares);
        killSectionPairs(sHor, eHor, sVert, eVert, forKillPares);
    }

    private void killSectionPairs(int sHor, int eHor, int sVert, int eVert,
                                  Set<Set<Integer>> forKillPares) {
        for (var pair : forKillPares) {
            for (int i = sHor; i < eHor + 1; ++i) {
                for (int j = sVert; j < eVert + 1; ++j) {
                    if (!sudoku.getCell(i * 9 + j).getPossibleValues().equals(pair)) { // Проверить работает ли
                        for (int digit : pair) {
                            if (sudoku.getCell(i * 9 + j).getPossibleValues().contains(digit))
                                sudoku.getCell(i * 9 + j).removeValue(digit);
                        }
                    }
                }
            }
        }
    }


    private Set<Set<Integer>> checkDuplicates(List<Set<Integer>> globPares) {
        Set<Set<Integer>> forKillPares = new HashSet<>();
        for (Set<Integer> pair : globPares) {
            int count = 0;
            for (Set<Integer> pair1 : globPares) {
                if (pair.equals(pair1))
                    ++count;
            }
            if (count >= 2)
                forKillPares.add(pair);
        }
        return forKillPares;
    }

    @Override
    public String tip() {
        return "you have to find a naked pair. " +
                "You can find 2 cells with only 2 candidates, so all other candidates with those " +
                "numbers\ncan be removed from whatever unit they have in common.";
    }

    @Override
    public String strType() {
        return "NakedPares:";
    }
}
