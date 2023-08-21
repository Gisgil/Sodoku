package puzzle.sodoku.Sodoku;


import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/**
 * @author Cyril Felicio Sodoku puzzle solver. Class uses the backtracking algorithm to find a solution for the provide
 * unsolved puzzle. It will check for validity of input (i.e letter, number of rows/columns) before solving the puzzle.
 * <p>
 * @original_author Jenny Gutierrez
 * @output file (solved_sodoku.csv) containing solve Sodoku puzzle. Both input and output file can be found at same
 * place.
 */
public class Main {

public static void main (String[] args) {
    if (args.length == 0) {
        System.out.println("Please provide file name to solve Sodoku puzzle");
        System.exit(1);
    }
    Sodoku sodoku = new Sodoku();
    UserInterface UI = new UserInterface();

    String inputFile = args[0];


    int[][] grid = getSodokuCSV(inputFile);

    sodoku.solveSodoku(inputFile, grid);
}

private static int[][] getSodokuCSV (String fileName) {
    BufferedReader br = null;
    String line = "";
    String csvSplitBy = ",";
    int[][] grid = new int[9][9];

    try {
        br = new BufferedReader(new FileReader(fileName));

        int lineNumber = 0;
        while ((line = br.readLine()) != null) {

            String[] sodokuLine = line.split(csvSplitBy);

            if (lineNumber >= 9 || sodokuLine.length > 9) {
                System.out.println("ERROR: Wrong input. Incorrect number of columns or rows.");
                System.out.println("Line Number " + (lineNumber + 1) + ". Column Number " + sodokuLine.length);
                System.exit(1);
            }

            //covert content to integers
            for (int i = 0; i < sodokuLine.length; i++) {
                try {
                    if (sodokuLine[i].matches("\\d+")) {
                        grid[i][lineNumber] = Integer.parseInt(sodokuLine[i]);
                    } else {
                        System.out.println("ERROR: Wrong Sodoku input:" + sodokuLine[i] + " Only digits [0..9] " +
                                "allowed.");
                        System.out.println("Line Number " + (lineNumber + 1) + ". Column: " + sodokuLine.length);
                        System.exit(1);
                    }
                } catch (NumberFormatException nfe) {}
                ;
            }

            lineNumber++;
        }
    } catch (IOException e) {
        System.out.println("Encountered Error");
    } finally {
        if (br != null) {
            try {
                br.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }//end of while

    return grid;
} //end of getSodokuCSV

}
