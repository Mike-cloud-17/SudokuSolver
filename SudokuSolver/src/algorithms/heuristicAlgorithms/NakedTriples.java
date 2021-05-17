package algorithms.heuristicAlgorithms;

import algorithms.Algorithm;
import board.SudokuField;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class NakedTriples implements Algorithm {
    private final SudokuField sudoku;

    public NakedTriples(SudokuField sudoku) {
        this.sudoku = sudoku;
        solve();
    }

    @Override
    public void solve() {
        nakedRowTriples();
        nakedColumnTriples();
        nakedSectionTriples();
    }

    private void nakedRowTriples() {
        for (int i = 0; i < 9; ++i) {
            List<Set<Integer>> globTriples = new ArrayList<>();
            Set<Set<Integer>> forKillTriples;
            Set<Integer> forKill = new HashSet<>();
            for (int j = 0; j < 9; ++j) {
                if (sudoku.getCell(i * 9 + j).getPossibleValues().size() == 2 ||
                        sudoku.getCell(i * 9 + j).getPossibleValues().size() == 3) {
                    forKill.addAll(sudoku.getCell(i * 9 + j).getPossibleValues());
                    globTriples.add(forKill);
                    forKill = new HashSet<>();
                }
            }
            forKillTriples = checkDuplicates(globTriples);
            killRowTriples(i, forKillTriples);
        }
    }

    private void killRowTriples(int i, Set<Set<Integer>> forKillTriples) {
        for (var triple : forKillTriples) {
            for (int j = 0; j < 9; ++j) {
                if (!triple.containsAll(sudoku.getCell(i * 9 + j).getPossibleValues())) {
                    for (int digit : triple) {
                        if (sudoku.getCell(i * 9 + j).getPossibleValues().contains(digit))
                            sudoku.getCell(i * 9 + j).removeValue(digit);
                    }
                }
            }
        }
    }

    private void nakedColumnTriples() {
        for (int i = 0; i < 9; ++i) {
            List<Set<Integer>> globTriples = new ArrayList<>();
            Set<Set<Integer>> forKillTriples;
            Set<Integer> forKill = new HashSet<>();
            for (int j = 0; j < 9; ++j) {
                if (sudoku.getCell(j * 9 + i).getPossibleValues().size() == 2 ||
                        sudoku.getCell(j * 9 + i).getPossibleValues().size() == 3) {
                    forKill.addAll(sudoku.getCell(j * 9 + i).getPossibleValues());
                    globTriples.add(forKill);
                    forKill = new HashSet<>();
                }
            }
            forKillTriples = checkDuplicates(globTriples);
            killColumnTriples(i, forKillTriples);
        }
    }

    private void killColumnTriples(int i, Set<Set<Integer>> forKillTriples) {
        for (var triple : forKillTriples) {
            for (int j = 0; j < 9; ++j) {
                if (!triple.containsAll(sudoku.getCell(j * 9 + i).getPossibleValues())) {
                    for (int digit : triple) {
                        if (sudoku.getCell(j * 9 + i).getPossibleValues().contains(digit))
                            sudoku.getCell(j * 9 + i).removeValue(digit);
                    }
                }
            }
        }
    }

    private void nakedSectionTriples() {
        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 3; ++j) {
                nakedSection(i * 3, i * 3 + 2, j * 3, j * 3 + 2);
            }
        }
    }

    private void nakedSection(int sHor, int eHor, int sVert, int eVert) {
        List<Set<Integer>> globTriples = new ArrayList<>();
        Set<Set<Integer>> forKillTriples;
        Set<Integer> forKill = new HashSet<>();
        for (int i = sHor; i < eHor + 1; ++i) {
            for (int j = sVert; j < eVert + 1; ++j) {
                if (sudoku.getCell(i * 9 + j).getPossibleValues().size() == 2 ||
                        sudoku.getCell(i * 9 + j).getPossibleValues().size() == 3) {
                    forKill.addAll(sudoku.getCell(i * 9 + j).getPossibleValues());
                    globTriples.add(forKill);
                    forKill = new HashSet<>();
                }
            }
        }
        forKillTriples = checkDuplicates(globTriples);
        killSectionTriples(sHor, eHor, sVert, eVert, forKillTriples);
    }

    private void killSectionTriples(int sHor, int eHor, int sVert, int eVert,
                                    Set<Set<Integer>> forKillTriples) {
        for (var triple : forKillTriples) {
            for (int i = sHor; i < eHor + 1; ++i) {
                for (int j = sVert; j < eVert + 1; ++j) {
                    if (!triple.containsAll(sudoku.getCell(i * 9 + j).getPossibleValues())) {
                        for (int digit : triple) {
                            if (sudoku.getCell(i * 9 + j).getPossibleValues().contains(digit))
                                sudoku.getCell(i * 9 + j).removeValue(digit);
                        }
                    }
                }
            }
        }
    }


    private Set<Set<Integer>> checkDuplicates(List<Set<Integer>> globPares) {
        Set<Set<Integer>> forKillTriples = new HashSet<>();
        for (Set<Integer> triple : globPares) {
            if (triple.size() == 3) {
                int count = 0;
                for (Set<Integer> triple1 : globPares) {
                    if (triple.equals(triple1))
                        ++count;
                }

                if (count >= 3)                                     //3.3.3
                    forKillTriples.add(triple);
                else if (count == 2) {                              //3.3.2
                    for (Set<Integer> triple1 : globPares) {
                        if (triple1.size() == 2) {
                            if (triple.containsAll(triple1)) {
                                forKillTriples.add(triple);
                            }
                        }
                    }
                } else if (count == 1) {                             //3.2.2
                    int num = 0;
                    for (Set<Integer> triple1 : globPares) {
                        if (triple1.size() == 2) {
                            if (triple.containsAll(triple1)) {
                                num++;
                            }
                        }
                    }
                    if (num == 2)
                        forKillTriples.add(triple);
                }
            } else {                                                    //2.2.2
                List<Set<Integer>> helpList = new ArrayList<>();
                for (Set<Integer> triple1 : globPares) {
                    if (triple1.size() == 2) {
                        for (var item : helpList) {
                            if (item.equals(triple1)) {
                                Set<Integer> fKSet = new HashSet<>(triple1);
                                fKSet.addAll(triple);
                                forKillTriples.add(fKSet);
                            }
                        }

                        int common = 0;
                        if (!triple1.containsAll(triple)) {
                            boolean indic = false;
                            for (var digit : triple1) {
                                if (triple.contains(digit)) {
                                    common = digit;
                                    indic = true;
                                    break;
                                }
                            }
                            if (indic) {
                                Set<Integer> forHelpList = new HashSet<>();
                                for (var item : triple) {
                                    if (!item.equals(common))
                                        forHelpList.add(item);
                                }
                                for (var item : triple1) {
                                    if (!item.equals(common))
                                        forHelpList.add(item);
                                }
                                helpList.add(forHelpList);
                            }
                        }
                    }
                }
            }

        }
        return forKillTriples;
    }

    @Override
    public String tip() {
        return "it is necessary to find a naked triple." +
                "You can find 3 cells with only 3/2 candidates in a special combination, " +
                "so all other candidates with those " +
                "numbers can be removed from whatever unit they have in common.";
    }

    @Override
    public String strType() {
        return "NakedTriples:";
    }
}
