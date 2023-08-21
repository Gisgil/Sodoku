package puzzle.sodoku.Sodoku;

import java.io.*;

public class Sodoku {
final String RESET = "\u001b[0m";
final String YELLOW = "\u001b[33m";
final String PURPLE = "\u001b[35m";
final String BLUE = "\u001b[34m";
final String RED = "\u001b[31m";
final int coreNumberMask = 0b1111;

public Sodoku () {
}


/**
 * @param fileSodoku Output of solved Sodoku
 * @param grid       inform of 9x9 int matrix 0000 0000  0000 0000  000|0 0000  0000 | 0000 flags for potential numbers
 *                   number 9 to 1                       9 to 0
 */
void solveSodoku (String fileSodoku, int[][] grid) {
    if (!isInputValid(0, 0, grid)) {
        System.out.println("Invalid Input. Please check input puzzle in the file.");
        System.exit(1);
    }

    addNotes(grid);
    printSodoku(grid);

    System.out.println("Sodoku puzzle has been solve!");
    toFile(grid, getFilePath(fileSodoku));
}

String getFilePath (String fileSodoku) {
    //get file path to place solved sodoku file
    File inFile = new File(fileSodoku);
    String absoluteFilePath = inFile.getAbsolutePath();

    return absoluteFilePath.substring(0, absoluteFilePath.lastIndexOf(File.separator));
}

void addNotes (int[][] grid) {
    for (int row = 0; row < 9; row++) {
        for (int col = 0; col < 9; col++) {
            int number = grid[row][col];
            int coreNumber = number & coreNumberMask;
            grid[row][col] = coreNumber | (getNotes(row, col, grid) << 4);
        }
    }

}

int getNotes (int rowIndex, int colIndex, int[][] grid) {
    int mask = 0x1ff;

    for (int col = 0; col < 9; col++) {
        if (col == colIndex) {
            continue;
        }

        int number = grid[rowIndex][col];
        mask = getMask(mask, number);
    }

    for (int row = 0; row < 9; row++) {
        if (row == rowIndex) {
            continue;
        }

        int number = grid[row][colIndex];
        mask = getMask(mask, number);
    }

    return mask;
}

private int getMask (int mask, int number) {
    int coreNumber = number & coreNumberMask;

    if (coreNumber != 0) {
        mask &= 0x1ff ^ (1 << (coreNumber - 1));
    }
    return mask;
}

boolean solveSodoku (int col, int row, int[][] grid) {
    if (col == 9) {
        col = 0;
        if (++row == 9) {
            //end of grid
            return true;
        }
    }

    if (grid[col][row] != 0) {
        //cell is already filled go to next but what happens if the value is invalid?
        return solveSodoku(col + 1, row, grid);
    }

    //iterate from 1 to 9 to check for a valid number for that cell
    for (int value = 1; value <= 9; ++value) {
        //check if value is valid and has not been taken
        if (isValidNumber(col, row, value, grid)) {
            grid[col][row] = value;
            if (solveSodoku(col + 1, row, grid)) {
                return true;
            }
        }

    }

    grid[col][row] = 0;
    return false;
}//end of isSolved

boolean isInputValid (int col, int row, int[][] grid) {
    if (col == 9) {
        col = 0;
        if (++row == 9) {
            //end of grid
            return true;
        }
    }

    if (grid[col][row] == 0) {
        //if the value in the cell is 0 leave it to be solved later
        return isInputValid(col + 1, row, grid);
    }

    // check is the value provide in the cell is valid
    if (isValidNumber(col, row, grid[col][row], grid)) {
        if (isInputValid(col + 1, row, grid)) {
            return true;
        }
    }

    return false;
}//end of isSolved


void printSodoku (int[][] grid) {
    String horizontalLine = "----- ✚ ----- ✚ -----" + PURPLE + " ✚ ✚ " + RESET + "----- ✚ ----- ✚ -----" + PURPLE +
            " ✚ ✚ " + RESET + "----- ✚ ----- ✚ -----";
    String horizontalDoubleLine = PURPLE + "----- ✚ ----- ✚ ----- ✚ ✚ ----- ✚ ----- ✚ ----- ✚ ✚ ----- ✚ ----- ✚ " +
            "-----\n----- ✚ ----- ✚ ----- ✚ ✚ ----- ✚ ----- ✚ ----- ✚ ✚ ----- ✚ ----- ✚ -----" + RESET;
    int flagMask = 0b0001_1111_1111_0000;

    int lastColumn = 26;
    int lastRow = 26;


    for (int print_row = 0; print_row < 27; print_row++) {
        for (int print_col = 0; print_col < 27; print_col++) {
            int row = print_row / 3;
            int col = print_col / 3;

            int number = grid[row][col];
            int coreNumber = number & coreNumberMask;

            int tableIndex = (print_col % 3 + print_row % 3 * 3) + 1;
            int flag = ((number & flagMask) >> 4) & tableIndex;

            if (coreNumber != 0 && print_row % 3 == 1 && print_col % 3 == 1) {
                System.out.print(YELLOW + coreNumber + RESET);
            } else if (coreNumber == 0 && flag != 0) {
                System.out.print(RED + tableIndex + RESET);
            } else {
                System.out.print(" ");
            }


            System.out.print(" ");
            if (print_col == lastColumn) {
                //print nothing
            } else if (print_col % 9 == 8) {
                System.out.print(PURPLE + "| | " + RESET);
            } else if (print_col % 3 == 2) {
                System.out.print("| ");
            }
        }
        System.out.println();

        if (print_row == lastRow) {
            //print nothing
        } else if (print_row % 9 == 8) {
            System.out.println(horizontalDoubleLine);
        } else if (print_row % 3 == 2) {
            System.out.println(horizontalLine);
        }
    }
    System.out.println();
    System.out.println();
    System.out.println();
}

void toFile (int[][] grid, String filePath) {
    PrintWriter writer = null;
    try {
        writer = new PrintWriter(filePath + "/Sodoku/solved_sodoku.csv", "UTF-8");

        for (int j = 0; j < 9; j++) {
            for (int i = 0; i < 9; i++) {
                writer.print(grid[i][j]);
                if (i < 8) {
                    writer.print(",");
                }
            }
            writer.println();
        }
    } catch (IOException ex) {
        // report
    } finally {
        try {writer.close();} catch (Exception ex) {}
    }


}



boolean isValidNumber (int col, int row, int value, int[][] grid) {
    //check across the columns for that row
    for (int i = 0; i < 9; i++) {
        if (grid[i][row] == value && i != col) {
            return false;
        }
    }

    //check across the rows for that column
    for (int i = 0; i < 9; i++) {
        if (grid[col][i] == value && i != row) {
            return false;
        }
    }

    //check the 3/3 grid or other size if option is available
    int offsetRow = (row / 3) * 3;
    int offsetCol = (col / 3) * 3;

    for (int subGridRow = 0; subGridRow < 3; subGridRow++) {
        for (int subGridCol = 0; subGridCol < 3; subGridCol++) {
            if ((row != offsetRow + subGridRow) && col != (offsetCol + subGridCol))
                if (grid[offsetCol + subGridCol][offsetRow + subGridCol] == value) {
                    return false;
                }
        }
        return true;
    }

    return true;
}//end of isValidNumber

}//end of class
