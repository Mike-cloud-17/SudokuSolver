package algorithms.heuristicAlgorithms;

import algorithms.Algorithm;
import board.SudokuField;

public class HiddenSingles implements Algorithm {
    private final SudokuField sudoku;

    public HiddenSingles(SudokuField sudoku) {
        this.sudoku = sudoku;
        solve();
    }

    @Override
    public void solve() {
        hiddenRowValues();
        hiddenColumnValues();
        hiddenSectionValues();
        sudoku.killExcess();
    }

    private void hiddenRowValues() {
        for (int i = 0; i < 9; ++i) {
            int[] digitQuantity = new int[10];
            for (int j = 0; j < 9; ++j) {
                for (int digit : sudoku.getCell(i * 9 + j).getPossibleValues()) {
                    digitQuantity[digit]++;
                }
            }
            for (int k = 1; k < 10; k++) {
                if (digitQuantity[k] == 1) {
                    lookForIndRow(k, i);
                }
            }
        }
    }

    private void lookForIndRow(int val, int i) {
        for (int j = 0; j < 9; ++j) {
            if (sudoku.getCell(i * 9 + j).getPossibleValues().contains(val)) {
                sudoku.getCell(i * 9 + j).setValueInitial(val);
            }
        }
    }

    private void hiddenColumnValues() {
        for (int i = 0; i < 9; ++i) {
            int[] digitQuantity = new int[10];
            for (int j = 0; j < 9; ++j) {
                for (int digit : sudoku.getCell(j * 9 + i).getPossibleValues()) {
                    digitQuantity[digit]++;
                }
            }
            for (int k = 1; k < 10; k++) {
                if (digitQuantity[k] == 1) {
                    lookForIndColumn(k, i);
                }
            }
        }
    }

    private void lookForIndColumn(int val, int i) {
        for (int j = 0; j < 9; ++j) {
            if (sudoku.getCell(j * 9 + i).getPossibleValues().contains(val)) {
                sudoku.getCell(j * 9 + i).setValueInitial(val);
            }
        }
    }

    private void hiddenSectionValues() {
        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 3; ++j) {
                findSection(i * 3, i * 3 + 2, j * 3, j * 3 + 2);
            }
        }
    }

    private void findSection(int sHor, int eHor, int sVert, int eVert) {
        int[] digitQuantity = new int[10];
        for (int i = sHor; i < eHor + 1; ++i) {
            for (int j = sVert; j < eVert + 1; ++j) {
                for (int digit : sudoku.getCell(i * 9 + j).getPossibleValues()) {
                    digitQuantity[digit]++;
                }
            }
        }

        for (int k = 1; k < 10; k++) {
            if (digitQuantity[k] == 1) {
                lookForIndSec(k, sHor, eHor, sVert, eVert);
            }
        }
    }

    private void lookForIndSec(int val, int sHor, int eHor, int sVert, int eVert) {
        for (int i = sHor; i < eHor + 1; ++i) {
            for (int j = sVert; j < eVert + 1; ++j) {
                if (sudoku.getCell(i * 9 + j).getPossibleValues().contains(val))
                    sudoku.getCell(i * 9 + j).setValueInitial(val);
            }
        }
    }

    @Override
    public String tip() {
        return "it is necessary to find a hidden digit. " +
                "A filled cell is the only possible one for an inserted digit in a row/column/box.";
    }

    @Override
    public String strType() {
        return "HiddenSingles:";
    }
}
