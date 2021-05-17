package helpingFacilities;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Собственный класс исключений
 */
public class SudokuException extends Exception {

    private Set<Pair<Integer, Integer>> errors;

    private void deleteEqualErrors() {
        Set<Pair<Integer, Integer>> errors = new HashSet<>();
        Set<List<Integer>> checkPairs = new HashSet<>();
        for (var error: this.errors){
            List<Integer> forCheck = new ArrayList<>();
            forCheck.add(error.getKey());
            forCheck.add(error.getValue());
            checkPairs.add(forCheck);
        }
        int i = 0;
        for (var pair: checkPairs){
            int row = -1;
            int column = -1;
            for (var missInd: pair){
                if (i % 2 == 0)
                    row = missInd;
                else
                    column = missInd;
                i++;
            }
            errors.add(new Pair<>(row, column));
        }
        this.errors = errors;
    }

    public Set<Pair<Integer, Integer>> getErrors() {
        return errors;
    }

    public SudokuException(String message) {
        super(message);
    }

    public SudokuException(String message, Set<Pair<Integer, Integer>> errors) {
        super(message);
        this.errors = errors;
        deleteEqualErrors();
    }
}
