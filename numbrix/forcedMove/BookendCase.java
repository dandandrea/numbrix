package numbrix.forcedMove;

import java.io.*;
import java.util.*;
import numbrix.*;
import numbrix.exception.*;

public class BookendCase {
	// Play the game
	public static boolean play(Board board) throws BoardException {
	    // Track whether or not we ever found a bookend case
		boolean everFoundBookendCase = false;

        // Keep playing bookend cases as long as we can find them
		// Playing a bookend case often creates a new bookend case opporuntity
		while (true) {
		    // Play bookend cases
			System.out.println("Playing forced moves: bookend cases");
	        System.out.println("");
		    boolean foundBookendCase = playBookendCases(board);

			// Did we find a bookend case
			if (foundBookendCase == true) {
			    everFoundBookendCase = true;
			}

            // Stop if we didn't find any bookend cases to play
			if (foundBookendCase == false) {
			    System.out.println("No more bookend cases");
	            System.out.println("");
			    break;
			}
		}

		// Return whether or not we ever found a bookend case
		return everFoundBookendCase;
	}

	// Play bookend cases
	private static boolean playBookendCases(Board board) throws BoardException {
	    // Whether or not there was a bookend case
		boolean foundBookendCase = false;

		// Iterate every position in the board and look for bookend cases
		for (int row = 1; row <= board.getSize(); row++) {
		    for (int column = 1; column <= board.getSize(); column++) {
			    // Only try this with positions that don't already have a value
				if (board.getValueUnsafe(row, column) != 0) {
				    // This position already has a value
					continue;
				}

				// Is the number to the left equal to the number to the right - 2?
				if (column != 1 && column != board.getSize() && board.getValueUnsafe(row, column - 1) != 0 && board.getValueUnsafe(row, column + 1) != 0 && board.getValueUnsafe(row, column - 1) == board.getValueUnsafe(row, column + 1) - 2) {
					// Make the move
					board.setValue(row, column, board.getValueUnsafe(row, column - 1) + 1, true);
				} else if (column != 1 && column != board.getSize() && board.getValueUnsafe(row, column - 1) != 0 && board.getValueUnsafe(row, column + 1) != 0 && board.getValueUnsafe(row, column - 1) == board.getValueUnsafe(row, column + 1) + 2) {
					// Make the move
					board.setValue(row, column, board.getValueUnsafe(row, column - 1) - 1, true);
				} else if (row != 1 && row != board.getSize() && board.getValueUnsafe(row - 1, column) != 0 && board.getValueUnsafe(row + 1, column) != 0 && board.getValueUnsafe(row + 1, column) == board.getValueUnsafe(row - 1, column) - 2) {
					// Make the move
					board.setValue(row, column, board.getValueUnsafe(row + 1, column) + 1, true);
				} else if (row != 1 && row != board.getSize() && board.getValueUnsafe(row - 1, column) != 0 && board.getValueUnsafe(row + 1, column) != 0 && board.getValueUnsafe(row + 1, column) == board.getValueUnsafe(row - 1, column) + 2) {
					// Make the move
					board.setValue(row, column, board.getValueUnsafe(row + 1, column) - 1, true);
				} else {
				    // Didn't found a bookend case
					continue;
				}

				// If we made it here then we found a bookend case
				System.out.println("Found bookend case at row " + row + ", column " + column);
				System.out.println("");
				foundBookendCase = true;

				// Display the game board
				System.out.println(board.toString());
				System.out.println("");
		    }
		}

		// Returm whether or not we found a bookend case
		return foundBookendCase;
	}
}
