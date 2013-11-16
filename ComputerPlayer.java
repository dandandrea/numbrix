import java.io.*;
import java.util.*;

class ComputerPlayer {
    // The board
	private Board board;

    // Constructor
	public ComputerPlayer(Board board) throws BoardException {
	    // Set board
	    this.board = board;

        // Play the game
		play();
	}

	// Play the game
	private void play() throws BoardException {
	    // Display the board
	    System.out.println("");
	    System.out.println(board.toString());
	    System.out.println("");

        // Play forced moves
		while (true) {
		    // Play forced moves
			System.out.println("Playing forced moves");
	        System.out.println("");
		    boolean foundForcedMove = playForcedMoves();

            // Stop if we didn't find any forced moves to play
			if (foundForcedMove == false) {
			    System.out.println("Didn't find any forced moves");
			    break;
			}
		}
	}

	// Play forced moves
	private boolean playForcedMoves() throws BoardException {
		// Play T-cases
		boolean foundForcedMove = playForcedMovesTCases();

		// Return
	    return foundForcedMove;
	}

	// Find forced moves: T-cases
	private boolean playForcedMovesTCases() throws BoardException {
	    // Whether or not there was a move
		boolean foundForcedMove = false;

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

				// Determine if bordered on bottom
				boolean borderedBottom = false;
				if (row == 1 || board.getValueUnsafe(row - 1, column) != 0) {
				    borderedBottom = true;

					if (row != 1) {
					    borderingValueList.add(board.getValueUnsafe(row - 1, column));
					}
				}

				// Determine if bordered on top
				boolean borderedTop = false;
				if (row == board.getSize() || board.getValueUnsafe(row + 1, column) != 0) {
				    borderedTop = true;

					if (row != board.getSize()) {
					    borderingValueList.add(board.getValueUnsafe(row + 1, column));
					}
				}

				// Not a candidate if bordered on all sides
				if (borderedLeft && borderedRight && borderedTop && borderedBottom) {
				    continue;
				}

				// Not a candidate if already bordered by both n-1 and n+1
				if (borderingValueList.contains(board.getValueUnsafe(row, column) - 1) && borderingValueList.contains(board.getValueUnsafe(row, column) + 1)) {
				    continue;
				}

                // Did we find a T-case?
				if (borderedLeft && borderedRight && borderedTop) {
				    // Which number to place?
					if (borderingValueList.contains(board.getValueUnsafe(row, column) - 1) == false) {
					    board.setValue(row - 1, column, board.getValueUnsafe(row, column) - 1);
					} else {
					    board.setValue(row - 1, column, board.getValueUnsafe(row, column) + 1);
					}

					// We found a forced move
					foundForcedMove = true;
				} else if (borderedLeft && borderedRight && borderedBottom) {
				    // Which number to place?
					if (borderingValueList.contains(board.getValueUnsafe(row, column) - 1) == false) {
					    board.setValue(row + 1, column, board.getValueUnsafe(row, column) - 1);
					} else {
					    board.setValue(row + 1, column, board.getValueUnsafe(row, column) + 1);
					}

					// We found a forced move
					foundForcedMove = true;
				} else if (borderedLeft && borderedTop && borderedBottom) {
				    // Which number to place?
					if (borderingValueList.contains(board.getValueUnsafe(row, column) - 1) == false) {
					    board.setValue(row, column + 1, board.getValueUnsafe(row, column) - 1);
					} else {
					    board.setValue(row, column + 1, board.getValueUnsafe(row, column) + 1);
					}

					// We found a forced move
					foundForcedMove = true;
				} else if (borderedRight && borderedTop && borderedBottom) {
				    // Which number to place?
					if (borderingValueList.contains(board.getValueUnsafe(row, column) - 1) == false) {
					    board.setValue(row, column - 1, board.getValueUnsafe(row, column) - 1);
					} else {
					    board.setValue(row, column - 1, board.getValueUnsafe(row, column) + 1);
					}

					// We found a forced move
					foundForcedMove = true;

					// Display the game board
					System.out.println(board.toString());
					System.out.println("");
				}
		    }
		}

		// Return
		return foundForcedMove;
	}
}
