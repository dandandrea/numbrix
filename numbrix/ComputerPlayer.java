package numbrix;

import java.io.*;
import java.util.*;
import numbrix.exception.*;
import numbrix.forcedMove.*;

class ComputerPlayer {
    // The board
	private Board board;

	// The pending move stack
	Deque<Move> pendingMoveStack;

	// The undo move stack
	Deque<Move> undoMoveStack;

    // Constructor
	public ComputerPlayer(Board board) throws BoardException {
	    // Set board
	    this.board = board;

		// Instantiate stacks
		pendingMoveStack = new ArrayDeque<Move>();
		undoMoveStack = new ArrayDeque<Move>();

        // Play the game
		play();
	}

	// Play the game
	boolean skipToPlay = false;
	private void play() throws BoardException {
	    // Display the board
	    System.out.println("");
	    System.out.println(board.toString());
	    System.out.println("");

		// Play the game
		while (true) {
            // Dump stacks
			dumpStacks();

		    // Skip to play?
		    if (skipToPlay == false) {
				// Play forced moves
				playForcedMoves();

				// Did this put the board into an invalid state?
				// If so then all forced moves on top of undo stack plus last non-forced move
				boolean isInInvalidState = recoverFromInvalidState();
			    if (isInInvalidState == true) {
			        System.out.println("");
			        skipToPlay = true;
					continue;
			    }

				// Have we won?
				if (board.isWon() == true) {
					// We won
					break;
				}

				// Push next non-forced moves
				System.out.println(">>> Pushing moves onto stack");
				System.out.println("");
				pushNextMoveList();
			} else {
			    // Reset skip to play flag
				skipToPlay = false;
			}

			// Is there a next move? If not then break
			if (pendingMoveStack.size() == 0) {
			    // No more moves
			    System.out.println("");
				System.out.println("No more moves");
			    System.out.println("");
			    break;
			}

			// Make a non-forced move
			playNonforcedMove();

		    // Did this put the board into an invalid state?
		    // If so then all forced moves on top of undo stack plus last non-forced move
			boolean isInInvalidState = recoverFromInvalidState();
			if (isInInvalidState == true) {
			    System.out.println("");
			    skipToPlay = true;
			}

			// Have we won?
			if (board.isWon() == true) {
				// We won
				break;
			}
		}
	}

	// Recover from invalid state
	private boolean recoverFromInvalidState() {
	    // Are we in an invalid state?
		boolean isInInvalidState = board.getIsInInvalidState();

        // Perform recovery if in invalid state
	    if (isInInvalidState == true) {
			// Undo last forced moves and last non-forced move
	        System.out.println("Board is in an invalid state, undoing move(s)");

            // Dump stacks
			System.out.println("");
			dumpStacks();

            // Undo
			board.undo();

			// Remove moves from pending move stack until the next move
			// is not for a value that is already in play
			while (true) {
			    // Get the next pending move
				Move nextMove = pendingMoveStack.peek();

				// Is there a next move? If not then break
				if (nextMove == null) {
				    break;
				}

				// Is this value already in play?
				if (board.findValue(nextMove.getValue()) != null) {
					// This value is already in play
					// Remove the item from the pending move stack
					System.out.println("Removing pending move since its value is already in play: " + nextMove);
					pendingMoveStack.remove();
				} else {
				    // Should be ready to go now
				    break;
				}
			}
	    }

		// Return whether or not we were in an invalid state
		return isInInvalidState;
	}

	// Push next available moves
	private void pushNextMoveList() throws BoardException {
	    // Get next value to place
	    int nextValue = board.getNextValue();
	    System.out.println("Next value to place: " + nextValue);

		// Did we get the value one lower than the lowest value in play?
		// Or did we get the value one higher than the lowest value in play?
		// If we got the value one lower then find the location of n + 1
		// If we got the value one higher then find the location of n - 1
		// We know that it is the former case if n + 1 is in play
		// And we know that it is the latter case if n - 1 is in play
		Location locationOfPreviousValue = board.findValue(nextValue + 1);
		if (locationOfPreviousValue == null) {
		    locationOfPreviousValue = board.findValue(nextValue - 1);
		}

        // Get location of previous value
	    System.out.println("Previous value is at " + locationOfPreviousValue);

		// Get available locations for placing the next value
		// Add them to the non-forced moves stack
		List<Location> availableLocationList = board.getAvailableLocationList(locationOfPreviousValue);
		for (int i = 0; i < availableLocationList.size(); i++) {
		    System.out.println("Row " + availableLocationList.get(i).getRow() + ", column " + availableLocationList.get(i).getColumn() + " is a candidate location");

			// Add to the pending moves stack
			pendingMoveStack.push(new Move(availableLocationList.get(i).getRow(), availableLocationList.get(i).getColumn(), nextValue));
		}
	}

	// Play non-forced moved
	private boolean playNonforcedMove() throws BoardException {
	    // Pop moves off of the pending move stack until
		// we find a move which won't conflict with the
		// current board state
		Move nextMove = null;
		while (true) {
		    // Is there a move on the stack?
			if (pendingMoveStack.size() == 0) {
			    nextMove = null;
			    break;
			}

		    // Pop the next move
		    nextMove = pendingMoveStack.pop();

			// Is this value already in play? If so then undo and pop again
			if (board.findValue(nextMove.getValue()) != null) {
			    // This value is already in play, undo
				System.out.println("Move " + nextMove + " is already in play, doing undo");
				board.undo();
			} else {
			    // This value is not yet in play
				// Therefore, we found our next move
			    break;
			}
		}

		// If no more moves then return false
		if (nextMove == null) {
		    return false;
		}

		// Place the next move
	    board.setValue(nextMove.getRow(), nextMove.getColumn(), nextMove.getValue());

		// Push this move onto the undo move stack
		undoMoveStack.push(nextMove);

		// Made it here then there was another non-forced move
		return true;
	}

    // Dump stack contents
	private void dumpStacks() {
		System.out.println(">>> Pending move stack depth: " + pendingMoveStack.size());
		Iterator<Move> iterator = pendingMoveStack.iterator();
		while (iterator.hasNext() == true) {
		    Move move = iterator.next();
		    System.out.println(move.getRow() + " " + move.getColumn() + " " + move.getValue());
		}

		System.out.println("");
		System.out.println(">>> Undo move stack depth: " + undoMoveStack.size());
		iterator = undoMoveStack.iterator();
		while (iterator.hasNext() == true) {
		    Move move = iterator.next();
		    System.out.println(move.getRow() + " " + move.getColumn() + " " + move.getValue());
		}

		System.out.println("");
    }

	// Play forced moves
	private void playForcedMoves() throws BoardException {
	    // Keep playing forced moves until they no longer
		// result in more forced moves
		while (true) {
			// Play forced moves
			System.out.println("Playing forced moves");
			System.out.println("");

	        // Play bookend cases
		    boolean foundBookendCase = BookendCase.play(board);

		    // Play T-cases
		    boolean foundTCase = TCase.play(board);

			// Stop if we didn't find any forced moves to play
			if (foundBookendCase == false || foundTCase == false) {
				System.out.println("No more forced moves");
			    System.out.println("");
				break;
			}
		}
	}
}
