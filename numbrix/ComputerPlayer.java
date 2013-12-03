package numbrix;

import java.io.*;
import java.util.*;
import numbrix.exception.*;
import numbrix.forcedMove.*;

class ComputerPlayer {
    // The board
	private Board board;

	// The pending move stack
	private Deque<Move> pendingMoveStack;

    // Constructor
	public ComputerPlayer(Board board) throws BoardException, CannotUseHintException {
	    // Set board
	    this.board = board;

		// Instantiate pending move stack
		pendingMoveStack = new ArrayDeque<Move>();

		if (Numbrix.DEBUG) System.out.println("");

        // Play the game
		play();
	}

	// Play the game
	boolean skipToPlay = false;
	private void play() throws BoardException, CannotUseHintException {
	    // Display the board
	    if (Numbrix.DEBUG) System.out.println(board.toString());
	    if (Numbrix.DEBUG) System.out.println("");

		// Play the game
		while (true) {
	       if (Numbrix.DEBUG) System.out.println(">>> At top of play loop");
	       if (Numbrix.DEBUG) System.out.println("");

		    // Skip to play?
		    if (skipToPlay == false) {
				// Play forced moves
				playForcedMoves();

				// Did this put the board into an invalid state?
				boolean isInInvalidState = recoverFromInvalidState();
			    if (isInInvalidState == true) {
			        if (Numbrix.DEBUG) System.out.println("");
			        skipToPlay = true;
					continue;
			    }

				// Have we won?
				if (board.isWon() == true) {
					// We won
					break;
				}

				// Push next non-forced moves
				if (Numbrix.DEBUG) System.out.println("Pushing moves onto stack");
				if (Numbrix.DEBUG) System.out.println("");
				pushNextMoves();
				if (Numbrix.DEBUG) System.out.println("");
				dumpStack();
			} else {
			    // Reset skip to play flag
				skipToPlay = false;
			}

			// Is there a next move? If not then break
			if (pendingMoveStack.size() == 0) {
			    // No more moves
			    if (Numbrix.DEBUG) System.out.println("");
				if (Numbrix.DEBUG) System.out.println("No more moves");
			    if (Numbrix.DEBUG) System.out.println("");
			    break;
			}

			// Make a non-forced move
	        if (Numbrix.DEBUG) System.out.println("Playing move from next move stack");
			if (Numbrix.DEBUG) System.out.println("");
			playNonforcedMove();

		    // Did this put the board into an invalid state?
			boolean isInInvalidState = recoverFromInvalidState();
			if (isInInvalidState == true) {
			    if (Numbrix.DEBUG) System.out.println("");
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
	private boolean recoverFromInvalidState() throws BoardException {
	    // Are we in an invalid state?
		boolean isInInvalidState = board.getIsInInvalidState();

        // Perform recovery if in invalid state
	    if (isInInvalidState == true) {
			// Undo last forced moves and last non-forced move
	        if (Numbrix.DEBUG) System.out.println("Board is in an invalid state, undoing move(s)");

            // Dump stack
			if (Numbrix.DEBUG) System.out.println("");
			dumpStack();

            // Undo
			board.undo();

			// Get the next move
			Move nextMove = pendingMoveStack.peek();

			// Undo moves until the next move can be played
			while (true) {
			    // Already in play?
			    Location location = board.findValue(nextMove.getValue());
				if (location != null) {
					// This value is already in play
					// Remove the item from the pending move stack
					if (Numbrix.DEBUG) System.out.println("Next value to play is already in play, doing undo");
					board.undo();
				} else {
				    break;
				}
			}
	    }

		// Return whether or not we were in an invalid state
		return isInInvalidState;
	}

	// Push next available moves
	private void pushNextMoves() throws BoardException {
	    // Get next value to place
	    int nextValue = board.getNextValue();
	    if (Numbrix.DEBUG) System.out.println("Next value to place: " + nextValue);
		if (Numbrix.DEBUG) System.out.println("");

		// Did we get the value one lower than the lowest value in play?
		// Or did we get the value one higher than the lowest value in play?
		// If we got the value one lower then find the location of n + 1
		// If we got the value one higher then find the location of n - 1
		// We know that it is the former case if n + 1 is in play
		// And we know that it is the latter case if n - 1 is in play
		Location locationOfPreviousValue = board.findValue(nextValue + 1);
		int previousValue = nextValue + 1;
		if (locationOfPreviousValue == null) {
		    locationOfPreviousValue = board.findValue(nextValue - 1);
		    previousValue = nextValue - 1;
		}

		// We'll add our moves to this list and then sort it by distance
		// just before putting it on the next move stack
		List<MoveWithDistance> nextMoveList = new ArrayList<MoveWithDistance>();

		// Get available locations for placing the next value
		List<Location> availableLocationList = board.getAvailableLocationList(locationOfPreviousValue);

		// List of available location list indices already used (for skipping them later)
		List<Integer> availableLocationListIndexSkipList = new ArrayList<Integer>();

        // The number which we are placing is n
		// Are any of the available locations adjacent to n - 1 or n + 1?
		for (int i = 0; i < availableLocationList.size(); i++) {
		    // Get adjacent values
            List<Integer> adjacentValueList = board.getAdjacentValueList(availableLocationList.get(i));

			// Are any of the available locations adjacent to n - 1 or n + 1?
			// Ignore the "previous value" as an n - 1 / n + 1 candidate since this will always be present
			for (int j = 0; j < adjacentValueList.size(); j++) {
			    // Is this our previous value? If so then skip it
				if (adjacentValueList.get(j) == previousValue) {
				    // Skip
				    continue;
				}

				// Is this value equal to n - 1?
				if (adjacentValueList.get(j) == nextValue - 1) {
				    // This is a priority location (distance = 0)
					if (Numbrix.DEBUG) System.out.println("Adding preferred location " + availableLocationList.get(i));
					nextMoveList.add(new MoveWithDistance(availableLocationList.get(i).getRow(), availableLocationList.get(i).getColumn(), nextValue, 0));

					// Add this available location list index to the skip list
					availableLocationListIndexSkipList.add(i);

					// Stop searching
					break;
				}

				// Is this value equal to n + 1?
				if (adjacentValueList.get(j) == nextValue + 1) {
				    // This is a priority location (distance = 0)
					if (Numbrix.DEBUG) System.out.println("Adding preferred location " + availableLocationList.get(i));
					nextMoveList.add(new MoveWithDistance(availableLocationList.get(i).getRow(), availableLocationList.get(i).getColumn(), nextValue, 0));

					// Add this available location list index to the skip list
					availableLocationListIndexSkipList.add(i);

					// Stop searching
					break;
				}
			}
		}

		// Put the rest of the available locations in the (intermediary) next move list
		for (int i = 0; i < availableLocationList.size(); i++) {
		    // Skip items marked for skipping
			if (availableLocationListIndexSkipList.contains(i) == true) {
			    // Skip
				continue;
			}

			// Add to the next move list (with distance = 1, for now)
			if (Numbrix.DEBUG) System.out.println("Adding non-preferred location " + availableLocationList.get(i));
			nextMoveList.add(new MoveWithDistance(availableLocationList.get(i).getRow(), availableLocationList.get(i).getColumn(), nextValue, 1));
		}

		// Sort the (intermediary) next move list by distance
		Collections.sort(nextMoveList);

		// Add the next moves to the next move stack
		for (int i = 0; i < nextMoveList.size(); i++) {
			pendingMoveStack.push(new Move(nextMoveList.get(i).getRow(), nextMoveList.get(i).getColumn(), nextMoveList.get(i).getValue()));
		}
	}

	// Play non-forced moved
	private boolean playNonforcedMove() throws BoardException, CannotUseHintException {
	    // Is there a move on the stack?
		if (pendingMoveStack.size() == 0) {
		    // Nothing to play
		    return false;
		}

	    // Pop the next move
	    Move nextMove = pendingMoveStack.pop();

		// Is this value already in play? If so then undo and don't play
		if (board.findValue(nextMove.getValue()) != null) {
		    // This value is already in play, undo
			if (Numbrix.DEBUG) System.out.println("Move " + nextMove + " is already in play, doing undo");
			board.undo();
		}

		// If no more moves then return false
		if (nextMove == null) {
		    return false;
		}

		// Place the next move
		if (Numbrix.DEBUG) System.out.println("Playing next move");
		if (Numbrix.DEBUG) System.out.println("");
	    board.setValue(nextMove.getRow(), nextMove.getColumn(), nextMove.getValue());

		// Made it here then there was another non-forced move
		return true;
	}

    // Dump stack contents
	private void dumpStack() {
	    // Dump stack depth
		if (Numbrix.DEBUG) System.out.println(">>> Pending move stack depth: " + pendingMoveStack.size());
		if (Numbrix.DEBUG) System.out.println("");

        // Dump stack contents
		Iterator<Move> iterator = pendingMoveStack.iterator();
		while (iterator.hasNext() == true) {
		    Move move = iterator.next();
		    if (Numbrix.DEBUG) System.out.println(move.getRow() + " " + move.getColumn() + " " + move.getValue());
		}

		if (Numbrix.DEBUG) System.out.println("");
    }

	// Play forced moves
	private void playForcedMoves() throws BoardException {
	    // Keep playing forced moves until they no longer
		// result in more forced moves
		while (true) {
			// Play forced moves
			if (Numbrix.DEBUG) System.out.println("Playing forced moves");
			if (Numbrix.DEBUG) System.out.println("");

	        // Play bookend cases
		    boolean foundBookendCase = BookendCase.play(board);

		    // Play T-cases
		    boolean foundTCase = TCase.play(board);

			// Stop if we didn't find any forced moves to play
			if (foundBookendCase == false || foundTCase == false) {
				if (Numbrix.DEBUG) System.out.println("No more forced moves");
			    if (Numbrix.DEBUG) System.out.println("");
				break;
			}
		}
	}
}
