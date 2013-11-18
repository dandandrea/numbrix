package numbrix.forcedMove;

import java.io.*;
import java.util.*;
import numbrix.*;
import numbrix.exception.*;

public class TCase {
	// Play the game
	public static boolean play(Board board) throws BoardException {
	    // Track whether or not we ever found a T-case
		boolean everFoundTCase = false;

        // Keep playing T-cases as long as we can find them
		// Playing a T-case often creates a new T-case opporuntity
		while (true) {
		    // Play T-cases
			System.out.println("Playing forced moves: T-cases");
	        System.out.println("");
		    boolean foundTCase = playTCases(board);

			// Did we find a T-case?
			if (foundTCase == true) {
			    everFoundTCase = true;
			}

            // Stop if we didn't find any T-cases to play
			if (foundTCase == false) {
			    System.out.println("Didn't find any T-cases");
	            System.out.println("");
			    break;
			}
		}

		// Return whether or not we ever found a T-case
		return everFoundTCase;
	}

	// Play T-cases
	private static boolean playTCases(Board board) throws BoardException {
	    // Whether or not there was a T-case
		boolean foundTCase = false;
		boolean everFoundTCase = false;

		// Iterate every position in the board and look for T-cases
		for (int row = 1; row <= board.getSize(); row++) {
		    for (int column = 1; column <= board.getSize(); column++) {
				// Needs to be a value here
				if (board.getValueUnsafe(row, column) == 0) {
				    continue;
				}

				// List of values which form the borders
				List<Integer> borderingValueList = new ArrayList<Integer>();

				// Determine if bordered on left side
				boolean borderedLeft = false;
				if (column == 1 || board.getValueUnsafe(row, column - 1) != 0) {
				    borderedLeft = true;

					if (column != 1) {
					    borderingValueList.add(board.getValueUnsafe(row, column - 1));
					}
				}

				// Determine if bordered on right side
				boolean borderedRight = false;
				if (column == board.getSize() || board.getValueUnsafe(row, column + 1) != 0) {
				    borderedRight = true;

					if (column != board.getSize()) {
					    borderingValueList.add(board.getValueUnsafe(row, column + 1));
					}
				}

				// Determine if bordered below
				boolean borderedBelow = false;
				if (row == 1 || board.getValueUnsafe(row - 1, column) != 0) {
				    borderedBelow = true;

					if (row != 1) {
					    borderingValueList.add(board.getValueUnsafe(row - 1, column));
					}
				}

				// Determine if bordered above
				boolean borderedAbove = false;
				if (row == board.getSize() || board.getValueUnsafe(row + 1, column) != 0) {
				    borderedAbove = true;

					if (row != board.getSize()) {
					    borderingValueList.add(board.getValueUnsafe(row + 1, column));
					}
				}

				// Not a candidate if bordered on all sides
				if (borderedLeft && borderedRight && borderedAbove && borderedBelow) {
				    continue;
				}

				// Not a candidate if already bordered by both n-1 and n+1
				if (borderingValueList.contains(board.getValueUnsafe(row, column) - 1) && borderingValueList.contains(board.getValueUnsafe(row, column) + 1)) {
				    continue;
				}

				// Not a candidate if lowest number and next highest number already placed
				if (board.getValueUnsafe(row, column) == 1 && borderingValueList.contains(board.getValueUnsafe(row, column) + 1)) {
				    continue;
				}

				// Not a candidate if highest number and next lowest number already placed
				if (board.getValueUnsafe(row, column) == board.getSize() * board.getSize() && borderingValueList.contains(board.getValueUnsafe(row, column) - 1)) {
				    continue;
				}

                // Did we find a T-case?
				if (borderedLeft && borderedRight && borderedAbove) {
				    // Which number to place?
					if (borderingValueList.contains(board.getValueUnsafe(row, column) - 1) == false) {
					    board.setValue(row - 1, column, board.getValueUnsafe(row, column) - 1);
					} else {
					    board.setValue(row - 1, column, board.getValueUnsafe(row, column) + 1);
					}

                    // Found a T-case
				    foundTCase = true;
					everFoundTCase = true;
				} else if (borderedLeft && borderedRight && borderedBelow) {
				    // Which number to place?
					if (borderingValueList.contains(board.getValueUnsafe(row, column) - 1) == false) {
					    board.setValue(row + 1, column, board.getValueUnsafe(row, column) - 1);
					} else {
					    board.setValue(row + 1, column, board.getValueUnsafe(row, column) + 1);
					}

                    // Found a T-case
				    foundTCase = true;
					everFoundTCase = true;
				} else if (borderedLeft && borderedAbove && borderedBelow) {
				    // Which number to place?
					if (borderingValueList.contains(board.getValueUnsafe(row, column) - 1) == false) {
					    board.setValue(row, column + 1, board.getValueUnsafe(row, column) - 1);
					} else {
					    board.setValue(row, column + 1, board.getValueUnsafe(row, column) + 1);
					}

                    // Found a T-case
				    foundTCase = true;
					everFoundTCase = true;
				} else if (borderedRight && borderedAbove && borderedBelow) {
				    // Which number to place?
					if (borderingValueList.contains(board.getValueUnsafe(row, column) - 1) == false) {
					    board.setValue(row, column - 1, board.getValueUnsafe(row, column) - 1);
					} else {
					    board.setValue(row, column - 1, board.getValueUnsafe(row, column) + 1);
					}

                    // Found a T-case
				    foundTCase = true;
					everFoundTCase = true;
				}

                // Did we find a T-case?
				if (foundTCase == true) {
				    // Found a T-case
                    System.out.println("Found T-case at row " + row + ", column " + column);
                    System.out.println("");

				    // Display the game board
				    System.out.println(board.toString());
				    System.out.println("");
				}

				// Reset found T-case flag
				foundTCase = false;
		    }
		}

		// Returm whether or not we ever found a T-case
		return everFoundTCase;
	}
}
