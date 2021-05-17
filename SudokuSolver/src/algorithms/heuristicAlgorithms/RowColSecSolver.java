package algorithms.heuristicAlgorithms;

import algorithms.Algorithm;
import board.SudokuField;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class RowColSecSolver implements Algorithm {
    private final SudokuField sudoku;

    public RowColSecSolver(SudokuField sudoku) {
        this.sudoku = sudoku;
        solve();
    }

    @Override
    public void solve() {
        int changeNum = -1;
        int initChanges = sudoku.getPossibleValuesNum();
        while (changeNum > 0 || changeNum == -1) {
            killRowValues();
            killColumnValues();
            killSectionValues();
            changeNum = initChanges - sudoku.getPossibleValuesNum();
            initChanges = sudoku.getPossibleValuesNum();
        }
    }

    private void killRowValues() {
        for (int i = 0; i < 9; ++i) {
            Set<Integer> forKill = new HashSet<>();
            for (int j = 0; j < 9; ++j) {
                if (sudoku.getCell(i * 9 + j).getFinalValue() != 0)
                    forKill.add(sudoku.getCell(i * 9 + j).getFinalValue());
            }
            for (int j = 0; j < 9; ++j) {
                for (Iterator<Integer> it = forKill.iterator(); it.hasNext(); ) {
                    int eqNum = it.next();
                    sudoku.getCell(i * 9 + j).removeValue(eqNum);
                }
            }
        }
    }

    private void killColumnValues() {
        for (int i = 0; i < 9; ++i) {
            Set<Integer> forKill = new HashSet<>();
            for (int j = 0; j < 9; ++j) {
                if (sudoku.getCell(j * 9 + i).getFinalValue() != 0)
                    forKill.add(sudoku.getCell(j * 9 + i).getFinalValue());
            }
            for (int j = 0; j < 9; ++j) {
                for (Iterator<Integer> it = forKill.iterator(); it.hasNext(); ) {
                    int eqNum = it.next();
                    sudoku.getCell(j * 9 + i).removeValue(eqNum);
                }
            }
        }
    }

    private void killSectionValues() {
        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 3; ++j) {
                sectionKill(i * 3, i * 3 + 2, j * 3, j * 3 + 2);
            }
        }
    }

    private void sectionKill(int sHor, int eHor, int sVert, int eVert) {
        Set<Integer> forKill = new HashSet<>();
        for (int i = sHor; i < eHor + 1; ++i) {
            for (int j = sVert; j < eVert + 1; ++j) {
                if (sudoku.getCell(i * 9 + j).getFinalValue() != 0)
                    forKill.add(sudoku.getCell(i * 9 + j).getFinalValue());
            }
        }

        for (int i = sHor; i < eHor + 1; ++i) {
            for (int j = sVert; j < eVert + 1; ++j) {
                for (int eqNum : forKill) {
                    sudoku.getCell(i * 9 + j).removeValue(eqNum);
                }
            }
        }
    }

    @Override
    public String tip() {
        return "you should get rid of some candidates according to the fact that " +
                "equal numbers can't meet more than ones in the same row/column/box.";
    }

    @Override
    public String strType() {
        return "Row_Column_Section_Solver:";
    }
}
