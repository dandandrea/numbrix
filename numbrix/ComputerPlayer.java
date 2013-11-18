package numbrix;

import java.io.*;
import java.util.*;
import numbrix.exception.*;
import numbrix.forcedMove.*;

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
			playNonforcedMove(board);

			// Break here for now
			break;
		}
	}

	// Play non-forced moved
	private void playNonforcedMove(Board board) {
	    // Do nothing for now
		System.out.println("STUB: Playing a non-forced move");
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
