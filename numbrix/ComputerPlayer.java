package numbrix;

import java.io.*;
import java.util.*;
import numbrix.exception.*;
import numbrix.forcedMove.*;

class ComputerPlayer {
    // The board
	private Board board;

	// The non-forced move stack
	Deque<Move> nonForcedMoveStack = new ArrayDeque<Move>();

	// The forced move stack
	Deque<Move> forcedMoveStack = new ArrayDeque<Move>();

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

		// Play the game
		while (true) {
			// Play forced moves
			boolean foundForcedMove = false;
			while (true) {
				// Play forced moves
				System.out.println("Playing forced moves");
				System.out.println("");
				foundForcedMove = playForcedMoves();

				// Stop if we didn't find any forced moves to play
				if (foundForcedMove == false) {
					System.out.println("Didn't find any forced moves");
				    System.out.println("");
					break;
				}
			}

			// Have we won?
			if (board.isWon() == true) {
			    // We won
				break;
			}

			// Make a non-forced move
			boolean foundNonforcedMove = playNonforcedMove(board);

			// Break if no forced moves and no non-forced moves made
			if (foundForcedMove == false && foundNonforcedMove == false) {
				System.out.println("");
			    System.out.println("No more moves");
				System.out.println("");
			    break;
			}
		}
	}

	// Play non-forced moved
	private boolean playNonforcedMove(Board board) {
	    int nextValue = board.getNextValue();
	    System.out.println("Next value to place: " + nextValue);
		Location locationOfPreviousValue = board.findValue(nextValue - 1);
	    System.out.println("Previous value is at row " + locationOfPreviousValue.getRow() + ", column " + locationOfPreviousValue.getColumn());

		// Return true for now
		return false;
	}

	// Play forced moves
	private boolean playForcedMoves() throws BoardException {
	    // Play bookend cases
		boolean foundBookendCase = BookendCase.play(board);

		// Play T-cases
		boolean foundTCase = TCase.play(board);

		// Return whether or not we found any forced moves
	    return foundBookendCase || foundTCase;
	}
}
