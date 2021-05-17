package solver;

import algorithms.Algorithm;
import algorithms.dancingLink.SudokuDlx;
import algorithms.heuristicAlgorithms.*;
import board.Cell;
import board.SudokuField;
import helpingFacilities.*;

import java.util.*;

public class Solver {
    static Random rnd = new Random();
    private static int version;
    private static int difficulty = 0;
    private static List<Integer> for2VersionSudoku;
    private static final List<String> tips = new ArrayList<>();
    private static final String extraTip = "you have to take a close look at the board." +
            " Filled number will be the only available one to this cell.";

    public static void main(String[] args) {
        List<Integer> data = new ArrayList<>();
        List<Integer> sudoku;
        try {
            data = readData(args);
        } catch (IllegalArgumentException ex) {
            System.out.println();
            System.out.println(ex.getMessage());
            System.out.println("The program has finished. To start again you have to rerun it with right parameters.");
            System.exit(1);
        }
        sudoku = data.subList(0, data.size() - 1);
        version = data.get(data.size() - 1);
        try {
            List<List<Integer>> finalSudoku = solve(sudoku);
        } catch (SudokuException ex) {
            System.out.println(ex.getMessage());
            if (ex.getErrors() != null) {
                System.out.println("Indexes of wrong filled cells:");
                for (Pair<Integer, Integer> elem : ex.getErrors()) {
                    System.out.print(elem.getKey() + " " + elem.getValue() + "; ");
                }
            }
        }
    }

    /**
     * Метод для считывания информации из командной строки
     *
     * @param args Аргументы командной строки
     * @return Список считанных данных
     */
    private static List<Integer> readData(String[] args) {
        System.out.println("Input 81 sudoku values divided by gap and \"1\" in the end to get a full solution " +
                ", \"2\" for the next step or \"3\" for detailed full solution." + "\n");
        List<String> listData = new ArrayList<>(0);
        listData.addAll(Arrays.asList(args));
        if (args.length != 82 || !tryParseIntChecked(listData))
            throw new IllegalArgumentException("You have to input 81 integer numbers divided by gap.");
        List<Integer> listIntData = new ArrayList<>(0);
        for (int i = 0; i < 82; ++i) {
            listIntData.add(Integer.parseInt(listData.get(i)));
        }
        return listIntData;
    }

    /**
     * Метод для проверки принадлежности элементов списка к типу int.
     *
     * @param list Список со значнеиями
     * @return Результат проверки
     */
    private static boolean tryParseIntChecked(List<String> list) {
        try {
            for (String s : list) {
                Integer.parseInt(s);
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Метод для решения судоку
     *
     * @param data нерешенный судоку
     * @return решенный судоку
     * @throws SudokuException Собственное исключение, выбрасываемое в случае некорректного переданного судоку
     */
    private static List<List<Integer>> solve(List<Integer> data) throws SudokuException {
        List<Integer> zeroIndexes = new ArrayList<>(0);
        SudokuField sudokuField = new SudokuField(data);
        List<Integer> initialSudoku = new ArrayList<>(makeIntegers(sudokuField.getSudoku()));
        System.out.println("Initial sudoku:");
        printSudoku(sudokuField.toMatrix());
        System.out.println();

        Set<Pair<Integer, Integer>> errors = correctnessCheck(sudokuField.toMatrix());
        if (errors.size() != 0) {
            throw new SudokuException("Invalid sudoku was entered.", errors);
        }

        heuristicSolve(sudokuField);

        SudokuDlx dlxAlg = new SudokuDlx();
        dlxAlg.solve(sudokuField.toMatrix());
        int[][] solvedSudoku = dlxAlg.getRes();

        if (sudokuField.getNotNullCells() != 81) {
            if (version == 2) {
                if (for2VersionSudoku == null) {
                    tips.add("you'll have to work hard and simply sort through " +
                            "the options of the inserted numbers and see if you can solve sudoku.");
                    for (int i = 0; i < 9; ++i) {
                        for (int j = 0; j < 9; ++j) {
                            if (for2VersionSudoku == null)
                                for2VersionSudoku = new ArrayList<>(Collections.singletonList(solvedSudoku[i][j]));
                            else
                                for2VersionSudoku.add(solvedSudoku[i][j]);
                        }
                    }
                }
            }
            difficulty = 5;
        }

        if (solvedSudoku == null) {
            throw new SudokuException("Error: Invalid sudoku was entered.");
        }

        if (version == 1) {
            System.out.println("Final Sudoku:");
            printSudoku(solvedSudoku);
            System.out.println("\nDifficulty = " + difficultyEstimation() + ".");
            return convertToListList(solvedSudoku);
        } else if (version == 2) {
            System.out.println("Tip(s):");
            for (int i = 0; i < tips.size(); ++i) {
                if (i == 0)
                    System.out.println("Firstly, " + tips.get(i));
                else if (i == 1)
                    System.out.println("After that, " + tips.get(i));
                else if (i == tips.size() - 1)
                    System.out.println("Finally, " + tips.get(i));
                else if (i == 2)
                    System.out.println("Than " + tips.get(i));
                else if (i == 3)
                    System.out.println("Afterwords " + tips.get(i));
                else
                    System.out.println("After that " + tips.get(i));
            }
            for (int i = 0; i < for2VersionSudoku.size(); ++i) {
                // Если нет точного значения ячейки
                if (initialSudoku.get(i) == 0 &&
                        for2VersionSudoku.get(i) != 0) {
                    zeroIndexes.add(i);
                }
            }
            int randZerInd = zeroIndexes.get(rnd.nextInt(zeroIndexes.size()));
            initialSudoku.set(randZerInd, for2VersionSudoku.get(randZerInd));
            System.out.println("\nFinal Sudoku:");
            printSudoku(makeMatrix(initialSudoku));
            System.out.println("Filled cell: [" + (randZerInd / 9 + 1) + "; " + (randZerInd % 9 + 1) + "]");
            System.out.println("\nDifficulty = " + difficultyEstimation() + ".");
            return convertToListList(sudokuField.toMatrix());
        }
        return convertToListList(sudokuField.toMatrix());
    }

    /**
     * Метод для получения сложности введенного судоку
     * @return Сложность судоку
     */
    private static int difficultyEstimation() {
        return difficulty;
    }

    /**
     * Метод для решения судоку с помощью эвристических алгоритмов
     *
     * @param sudokuField Судоку
     */
    private static void heuristicSolve(SudokuField sudokuField) {
        while (true) {
            int possValues = sudokuField.getPossibleValuesNum();
            int notNull = sudokuField.getNotNullCells();
            Algorithm rcss = new RowColSecSolver(sudokuField);
            if (sudokuField.getPossibleValuesNum() != possValues) {
                if (version == 2) {
                    if (for2VersionSudoku == null) {
                        tips.add(rcss.tip());
                    }
                }
            }
            if (sudokuField.getNotNullCells() != notNull) {
                if (version == 2) {
                    if (for2VersionSudoku == null) {
                        for2VersionSudoku = new ArrayList<>(makeIntegers(sudokuField.getSudoku()));
                        tips.add(extraTip);
                    }
                } else if (version == 3) {
                    System.out.println(rcss.strType());
                    System.out.println(sudokuField);
                }
            }
            if (sudokuField.getNotNullCells() == 81) {
                if (difficulty < 1)
                    difficulty = 1;
                break;
            }

            notNull = sudokuField.getNotNullCells();
            Algorithm hs = new HiddenSingles(sudokuField);
            if (sudokuField.getNotNullCells() != notNull) {
                if (version == 2) {
                    if (for2VersionSudoku == null) {
                        tips.add(hs.tip());
                        for2VersionSudoku = new ArrayList<>(makeIntegers(sudokuField.getSudoku()));
                    }
                } else if (version == 3) {
                    System.out.println(hs.strType());
                    System.out.println(sudokuField);
                }
                if (difficulty < 2)
                    difficulty = 2;
                if (sudokuField.getNotNullCells() == 0)
                    break;
                continue;
            }


            int possVQ = sudokuField.getPossibleValuesNum();
            String possVBefore = sudokuField.showPossibleValues();
            Algorithm np = new NakedPares(sudokuField);
            if (sudokuField.getPossibleValuesNum() != possVQ) {
                if (version == 3) {
                    System.out.println(np.strType());
                    System.out.println("Candidates before algorithm:" + "\n" + possVBefore);
                    System.out.println("Candidates after algorithm:" + "\n" + sudokuField.showPossibleValues());
                    System.out.println(sudokuField);
                } else if (version == 2) {
                    if (for2VersionSudoku == null)
                        tips.add(np.tip());
                }
                if (difficulty < 3)
                    difficulty = 3;
                continue;
            }

            possVQ = sudokuField.getPossibleValuesNum();
            possVBefore = sudokuField.showPossibleValues();
            Algorithm nt = new NakedTriples(sudokuField);
            if (sudokuField.getPossibleValuesNum() != possVQ) {
                if (version == 3) {
                    System.out.println(nt.strType());
                    System.out.println("Candidates before algorithm:" + "\n" + possVBefore);
                    System.out.println("Candidates after algorithm:" + "\n" + sudokuField.showPossibleValues());
                    System.out.println(sudokuField);
                } else if (version == 2) {
                    if (for2VersionSudoku == null)
                        tips.add(nt.tip());
                }
                if (difficulty < 3)
                    difficulty = 3;
                continue;
            }

            possVQ = sudokuField.getPossibleValuesNum();
            possVBefore = sudokuField.showPossibleValues();
            Algorithm hp = new HiddenPares(sudokuField);
            if (sudokuField.getPossibleValuesNum() != possVQ) {
                if (version == 3) {
                    System.out.println(hp.strType());
                    System.out.println("Candidates before algorithm:" + "\n" + possVBefore);
                    System.out.println("Candidates after algorithm:" + "\n" + sudokuField.showPossibleValues());
                    System.out.println(sudokuField);
                } else if (version == 2) {
                    if (for2VersionSudoku == null)
                        tips.add(hp.tip());
                }
                if (difficulty < 3)
                    difficulty = 3;
                continue;
            }

            possVQ = sudokuField.getPossibleValuesNum();
            possVBefore = sudokuField.showPossibleValues();
            Algorithm xw = new XWing(sudokuField);
            if (sudokuField.getPossibleValuesNum() != possVQ) {
                if (version == 3) {
                    System.out.println(xw.strType());
                    System.out.println("Candidates before algorithm:" + "\n" + possVBefore);
                    System.out.println("Candidates after algorithm:" + "\n" + sudokuField.showPossibleValues());
                    System.out.println(sudokuField);
                } else if (version == 2) {
                    if (for2VersionSudoku == null)
                        tips.add(xw.tip());
                }
                if (difficulty < 4)
                    difficulty = 4;
                continue;
            }

            possVQ = sudokuField.getPossibleValuesNum();
            possVBefore = sudokuField.showPossibleValues();
            Algorithm yw = new YWing(sudokuField);
            if (sudokuField.getPossibleValuesNum() != possVQ) {
                if (version == 3) {
                    System.out.println(yw.strType());
                    System.out.println("Candidates before algorithm:" + "\n" + possVBefore);
                    System.out.println("Candidates after algorithm:" + "\n" + sudokuField.showPossibleValues());
                    System.out.println(sudokuField);
                } else if (version == 2) {
                    if (for2VersionSudoku == null)
                        tips.add(yw.tip());
                }
                if (difficulty < 4)
                    difficulty = 4;
                continue;
            }

            if (sudokuField.getPossibleValuesNum() == possValues)
                break;
        }
    }

    /**
     * Метод для проверки корректности переданного для решения судоку
     *
     * @param sudoku Судоку
     * @return Множество пар индексов некорректно стоящих цифр
     */
    private static Set<Pair<Integer, Integer>> correctnessCheck(int[][] sudoku) {
        Set<Pair<Integer, Integer>> errors = new HashSet<>();
        List<Pair<Integer, Integer>> forCheck = new ArrayList<>(); //1 param - value, 2 - index
        boolean indic = true;

        for (int i = 0; i < 9; ++i) {
            for (int j = 0; j < 9; ++j) {
                if (sudoku[i][j] != 0) {
                    for (Pair<Integer, Integer> elem : forCheck) {
                        if (elem.getKey().equals(sudoku[i][j])) { //сравнение ключа - цифры из судоку и элемента судоку
                            errors.add(new Pair<>(elem.getValue() / 9, elem.getValue() % 9));
                            errors.add(new Pair<>(i, j));
                            indic = false;
                        }
                    }
                    if (indic)
                        forCheck.add(new Pair<>(sudoku[i][j], i * 9 + j));
                    else
                        indic = true;
                }
            }
            forCheck.clear();
        }

        for (int i = 0; i < 9; ++i) {
            for (int j = 0; j < 9; ++j) {
                if (sudoku[j][i] != 0) {
                    for (Pair<Integer, Integer> elem : forCheck) {
                        if (elem.getKey().equals(sudoku[j][i])) { //сравнение ключа - цифры из судоку и элемента судоку
                            errors.add(new Pair<>(elem.getValue() / 9, elem.getValue() % 9));
                            errors.add(new Pair<>(j, i));
                            indic = false;
                        }
                    }
                    if (indic)
                        forCheck.add(new Pair<>(sudoku[j][i], j * 9 + i));
                    else
                        indic = true;
                }
            }
            forCheck.clear();
        }

        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 3; ++j) {
                errors.addAll(sectionCheck(i * 3, i * 3 + 2, j * 3, j * 3 + 2, sudoku));
            }
        }

        return errors;
    }

    /**
     * Метод для преобразования из List<Cell> в List<Integer>
     *
     * @param sudoku Список с клетками
     * @return Список с финальными зачениями клеток
     */
    private static List<Integer> makeIntegers(List<Cell> sudoku) {
        List<Integer> finalValues = new ArrayList<>();
        for (var cell : sudoku) {
            finalValues.add(cell.getFinalValue());
        }
        return finalValues;
    }

    /**
     * Метод для проверки корректности заолнения секции судоку
     *
     * @param sHor   Координата начала секции по горизонтали
     * @param eHor   Координата конца секции по горизонтали
     * @param sVert  Координата начала секции по вертикали
     * @param eVert  Координата конца секции по вертикали
     * @param sudoku Судоку
     * @return Множество пар индексов некорректно стоящих цифр
     */
    private static Set<Pair<Integer, Integer>> sectionCheck(int sHor, int eHor,
                                                           int sVert, int eVert, int[][] sudoku) {
        Set<Pair<Integer, Integer>> errors = new HashSet<>();
        List<Pair<Integer, Integer>> forCheck = new ArrayList<>(); //1 param - value, 2 - index
        boolean indic = true;

        for (int i = sHor; i < eHor + 1; ++i) {
            for (int j = sVert; j < eVert + 1; ++j) {
                if (sudoku[i][j] != 0) {
                    for (Pair<Integer, Integer> elem : forCheck) {
                        if (elem.getKey().equals(sudoku[i][j])) { //сравнение ключа и элемента судоку
                            errors.add(new Pair<>(elem.getValue() / 9, elem.getValue() % 9));
                            errors.add(new Pair<>(i, j));
                            indic = false;
                        }
                    }
                    if (indic)
                        forCheck.add(new Pair<>(sudoku[i][j], i * 9 + j));
                    else
                        indic = true;
                }
            }
        }

        return errors;
    }

    /**
     * Метод для преобразования в List<List<Integer>> из int[][]
     *
     * @param arr Судоку
     * @return Судоку
     */
    private static List<List<Integer>> convertToListList(int[][] arr) {
        List<List<Integer>> listList = new ArrayList<>();
        for (int[] ints : arr) {
            List<Integer> list = new ArrayList<>();
            for (int anInt : ints) {
                list.add(anInt);
            }
            listList.add(list);
        }
        return listList;
    }

    /**
     * Метод для преобразования в int[][] из List<Integer>
     *
     * @param sudoku Судоку
     * @return Судоку
     */
    private static int[][] makeMatrix(List<Integer> sudoku) {
        int[][] matrixSudoku = new int[9][9];
        for (int i = 0; i < 9; ++i) {
            for (int g = 0; g < 9; ++g) {
                matrixSudoku[i][g] = sudoku.get(9 * i + g);
            }
        }
        return matrixSudoku;
    }

    /**
     * Метод для вывода судоку
     *
     * @param board Судоку
     */
    private static void printSudoku(int[][] board) {
        for (int row = 0; row < 9; row++) {
            for (int column = 0; column < 9; column++) {
                System.out.print(board[row][column] + " ");
            }
            System.out.println();
        }
    }
}

//diff 0 (for mistakes) = 1 0 0 1 0 5 0 0 0 1 4 0 0 0 0 6 7 0 0 8 0 0 0 2 4 0 0 0 6 3 0 7 0 0 1
// 0 9 0 6 0 0 0 0 0 3 0 1 0 0 9 0 5 2 0 0 0 7 2 0 0 0 8 0 0 2 6 0 0 0 0 3 5 0 0 0 4 0 9 0 0 4 3

//diff 1 = 0 0 0 1 0 5 0 0 0 1 4 0 0 0 0 6 7 0 0 8 0 0 0 2 4 0 0 0 6 3 0 7 0 0 1 0 9 0 0 0 0 0
// 0 0 3 0 1 0 0 9 0 5 2 0 0 0 7 2 0 0 0 8 0 0 2 6 0 0 0 0 3 5 0 0 0 4 0 9 2 0 0 1

//diff 2 = 0 0 0 0 0 4 0 2 8 4 0 6 0 0 0 0 0 5 1 0 0 0 3 0 6 0 0 0 0 0 3 0 1 0 0 0 0 8 7 0 0 0
// 1 4 0 0 0 0 7 0 9 0 0 0 0 0 2 0 1 0 0 0 3 9 0 0 0 0 0 5 0 7 6 7 0 4 0 0 0 0 0 1

//diff 3 = 7 2 0 0 9 6 0 0 3 0 0 0 2 0 5 0 0 0 0 8 0 0 0 4 0 2 0 0 0 0 0 0 0 0 6 0 1 0 6 5 0 3
// 8 0 7 0 4 0 0 0 0 0 0 0 0 3 0 8 0 0 0 9 0 0 0 0 7 0 2 0 0 0 2 0 0 4 3 0 0 1 8 1

//diff 4 = 3 0 9 0 0 0 4 0 0 2 0 0 7 0 9 0 0 0 0 8 7 0 0 0 0 0 0 7 5 0 0 6 0 2 3 0 6 0 0 9 0 4
// 0 0 8 0 2 8 0 5 0 0 4 1 0 0 0 0 0 0 5 9 0 0 0 0 1 0 6 0 0 7 0 0 6 0 0 0 1 0 4 1

//diff 4 (XY-wing-2version) = 3 0 9 0 0 0 4 7 0 2 0 5 7 0 9 8 0 3 0 8 7 0 3 0 9 0 0 7 5 4 8 6 1 2 3 9 6 0
// 0 9 2 4 7 5 8 9 2 8 3 5 7 6 4 1 0 0 0 0 0 0 5 9 6 5 0 2 1 0 6 3 8 7 8 0 6 5 0 3 1 2 4 2

//diff 5 = 0 0 0 7 0 4 0 0 5 0 2 0 0 1 0 0 7 0 0 0 0 0 8 0 0 0 2 0 9 0 0 0 6 2 5 0 6 0 0 0 7 0
// 0 0 8 0 5 3 2 0 0 0 1 0 4 0 0 0 9 0 0 0 0 0 3 0 0 6 0 0 9 0 2 0 0 4 0 7 0 0 0 1

//diff 5 (2version) = 0 0 0 7 2 4 0 0 5 0 2 0 0 1 0 0 7 0 0 0 0 0 8 0 0 0 2 0 9 0 0 3 6 2 5 0 6
// 0 2 0 7 0 0 0 8 0 5 3 2 4 0 0 1 0 4 0 0 3 9 0 0 2 0 0 3 0 0 6 2 0 9 0 2 0 9 4 5 7 0 0 0 2

