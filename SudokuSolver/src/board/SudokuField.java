package board;

import java.util.*;

public class SudokuField {
    private final List<Cell> sudoku = new ArrayList<>(0);

    private void setInfluence() {
        for (int i = 0; i < 9; ++i) {
            for (int j = 0; j < 9; ++j) {
                Set<Integer> forInfluence = new HashSet<>();
                for (int k = 0; k < 9; ++k) {
                    forInfluence.add(i * 9 + k);
                    forInfluence.add(k * 9 + j);
                }
                forInfluence.addAll(defineSection(i * 9 + j));
                forInfluence.remove(i * 9 + j);
                getCell(i * 9 + j).setInfluenceSphere(forInfluence);
            }
        }
    }

    private Set<Integer> defineSection(int ind) {
        Set<Integer> forInfluence = new HashSet<>();
        Set<Integer> section1 = new HashSet<>(Arrays.asList(0, 1, 2, 9, 10, 11, 18, 19, 20));
        Set<Integer> section2 = new HashSet<>(Arrays.asList(3, 4, 5, 12, 13, 14, 21, 22, 23));
        Set<Integer> section3 = new HashSet<>(Arrays.asList(6, 7, 8, 15, 16, 17, 24, 25, 26));
        Set<Integer> section4 = new HashSet<>(Arrays.asList(27, 28, 29, 36, 37, 38, 45, 46, 47));
        Set<Integer> section5 = new HashSet<>(Arrays.asList(30, 31, 32, 39, 40, 41, 48, 49, 50));
        Set<Integer> section6 = new HashSet<>(Arrays.asList(33, 34, 35, 42, 43, 44, 51, 52, 53));
        Set<Integer> section7 = new HashSet<>(Arrays.asList(54, 55, 56, 63, 64, 65, 72, 73, 74));
        Set<Integer> section8 = new HashSet<>(Arrays.asList(57, 58, 59, 66, 67, 68, 75, 76, 77));
        Set<Integer> section9 = new HashSet<>(Arrays.asList(60, 61, 62, 69, 70, 71, 78, 79, 80));
        List<Set<Integer>> sections = new ArrayList<>(Arrays.asList(section1, section2, section3, section4,
                section5, section6, section7, section8, section9));
        for (int i = 0; i < 9; ++i) {
            if (sections.get(i).contains(ind))
                sections.get(i).forEach(elem -> {
                    if (elem != ind)
                        forInfluence.add(elem);
                });
        }
        return forInfluence;
    }

    public int size() {
        return sudoku.size();
    }

    public SudokuField(List<Integer> sudoku) {
        for (Integer figure : sudoku) {
            if (figure.equals(0))
                this.sudoku.add(new Cell());
            else
                this.sudoku.add(new Cell(figure));
        }
        setInfluence();
    }

    public Cell getCell(int ind) {
        return sudoku.get(ind);
    }

    public int[][] toMatrix() {
        int[][] matrixSudoku = new int[9][9];
        for (int i = 0; i < 9; ++i) {
            for (int g = 0; g < 9; ++g) {
                matrixSudoku[i][g] = sudoku.get(9 * i + g).getFinalValue();
            }
        }
        return matrixSudoku;
    }

    public List<Cell> getSudoku() {
        return sudoku;
    }

    public void setFigure(int ind, int num) {
        sudoku.get(ind).setValue(num);
    }

    public int getNotNullCells() {
        int count = 0;
        for (Cell cell : sudoku) {
            if (cell.getFinalValue() != 0) {
                count++;
            }
        }
        return count;
    }

    public int getPossibleValuesNum() {
        int count = 0;
        for (Cell cell : sudoku) {
            count += cell.getPossibleValues().size();
        }
        return count;
    }

    public String showPossibleValues() {
        StringBuilder information = new StringBuilder();
        for (int i = 0; i < 9; ++i) {
            for (int j = 0; j < 9; ++j) {
                information.append(sudoku.get(i * 9 + j).getPossibleValues()).append("\t");
            }
            information.append("\n");
        }
        return information.toString();
    }

    public void killExcess() {
        for (var cell : sudoku) {
            if (cell.getFinalValue() != 0 && cell.getPossibleValues().size() > 0)
                cell.setValue(cell.getFinalValue());
        }
    }

    public void deferredDeletion() {
//        for(int i = 0; i < sudoku.size(); ++i){
//            for (var digit: sudoku.get(i).getRemoveValues()){
//                System.out.println(i + " " + digit);
//                sudoku.get(i).removeValue(digit);
//            }
//        }
        for (var cell : sudoku){
            for (var digit: cell.getRemoveValues()){
                cell.removeValue(digit);
            }
        }
    }

    @Override
    public String toString() {
        StringBuilder board = new StringBuilder();
        for (int row = 0; row < 9; row++) {
            for (int column = 0; column < 9; column++) {
                board.append(toMatrix()[row][column]).append(" ");
            }
            board.append("\n");
        }
        return board.toString();
    }
}
