package numbrix;

import java.util.*;
import java.lang.*;
import numbrix.exception.*;

public class Board {
    // Board size
    private int size;
 
    // Row list
    private List<List<Integer>> rowList;

    // Move list
    private List<Move> moveList;

    // Hint list
    private List<Move> hintList;

    // Placed number count
    private int placedCount;

    // Whether or not the game has been won
    private boolean isWon;

	// To track the total number of moves made
	private int moveCount;

    // Constructor
    public Board(int size) {
        // Set board size
        this.size = size;

        // Instantiate row list
        rowList = new ArrayList<List<Integer>>();

        // Instantiate move list
        moveList = new ArrayList<Move>();

        // Instantiate hint list
        hintList = new ArrayList<Move>();

        // Initialize placed number count
        placedCount = 0;

        // Initialize game won status
        isWon = false;

		// Initialize move count
		moveCount = 0;

        // Instantiate column lists
        for (int row = 0; row < size; row++) {
            rowList.add(new ArrayList<Integer>());
        }

        // Initialize column values
        for (int row = 0; row < size; row++) {
            for (int column = 0; column < size; column++) {
                rowList.get(row).add(0);
            }
        }
    }

	// Allow setting a board value without the isForcedMove flag
	public void setValue(int row, int column, int value) throws BoardException, CannotUseHintException {
	    // Call other method to set a board value
		setValue(row, column, value, false);
	}

    // Set a board value
    public void setValue(int row, int column, int value, boolean isForcedMove) throws BoardException, CannotUseHintException {
	    // Increment the move count, regardless of outcome
		moveCount++;

        // Validate board coordinates and value
        validateCoordinates(row, column);
        validateValue(value);

        // The following logic is used in the block below
        // Set?   Exists?   PC++   PC--   History   Set   Clear
        //  T       T        -      -        X       X      X
        //  T       F        X      -        X       X      -
        //  F       T        -      X        -       -      X

        // In other words:
        // If moving an existing number: add a history item, clear the existing location, and add to new location
        // If setting a new number: add a history item, add to new location, and increment the placed count
        // If Clearing an existing number: clear the existing location and decrement the placed count

        // Is this a setting or clearing move?
        if (value > 0) {
            // Get the location of the number which we are searching for
            // This is null if the value is not on the board
            Location location = getLocationOfValue(value);

            // Is this number already in play?
            if (location != null) {
                // Add to move list
                moveList.add(new Move(row, column, value, isForcedMove));

                // Decrement the placed count if there is a different, non-zero value at the target location
                if (rowList.get(row - 1).get(column - 1) != 0 && rowList.get(row - 1).get(column - 1) != value) {
                    placedCount--;
                }

                // Clear old location and set new location
                rowList.get(location.getRow() - 1).set(location.getColumn() - 1, 0);
                rowList.get(row - 1).set(column - 1, value);
            } else {
                // Add to move list
                moveList.add(new Move(row, column, value, isForcedMove));

                // Increment the placed count if there isn't already a number at this location
                if (rowList.get(row - 1).get(column - 1) == 0) {
                    placedCount++;
                }

                // Set the board value
                rowList.get(row - 1).set(column - 1, value);
            }
        } else {
            // Is there a value at this location?
            if (rowList.get(row - 1).get(column - 1) != 0) {
                // Clear the board value
                rowList.get(row - 1).set(column - 1, 0);

                // Decrement the placed count
                placedCount--;
            }
        }

        // Is the board full now?
        // If so, has the game been won?
        if (placedCount == size * size && isGameComplete() == true) {
            isWon = true;
        } else {
            isWon = false;
        }
    }

    // Get a board value
    public int getValue(int row, int column) throws BoardException {
        // Validate board coordinates
        validateCoordinates(row, column);

        // Return the board value
        return rowList.get(row - 1).get(column - 1);
    }

	// Get a board value, bypass coordinate validation and hint checking
	public int getValueUnsafe(int row, int column) {
        return rowList.get(row - 1).get(column - 1);
	}

    // Override toString() in order to display the board
    public String toString() {
        // Get a StringBuilder
        StringBuilder stringBuilder = new StringBuilder();

        // Iterate the rows
        for (int row = size - 1; row >= 0; row--) {
            // Iterate the columns
            for (int column = 0; column < size; column++) {
                // Display a "-" if the value is 0, otherwise display the value
                if (rowList.get(row).get(column) == 0) {
                    stringBuilder.append("-   ");
                } else {
                    stringBuilder.append(String.format("%-3d ", rowList.get(row).get(column)));
                }
            }

            // New line if there is another row
            if (row != 0) {
                stringBuilder.append(System.getProperty("line.separator"));
            }
        }

        // Return the StringBuilder
        return stringBuilder.toString();
    }

    // Display the move list
    public void displayMoveList() {
        for (int move = 0; move < moveList.size(); move++) {
            System.out.println((move + 1) + ": (" + moveList.get(move).getRow() + ", " + moveList.get(move).getColumn() + ") = " + moveList.get(move).getValue());
        }
    }

    // Add a hint
    public void addHint(int row, int column, int value) throws BoardException, CannotUseHintException {
        // Cannot add a hint if a move has already been made
        if (moveList.size() > 0) {
            throw new BoardException("Hints cannot be added after a move has been made");
        }

        // Validate board coordinates and value
        validateCoordinates(row, column);
        validateValue(value);

        // Add the hint to the hint list
        hintList.add(new Move(row, column, value));

        // Add the hint to the board
        rowList.get(row - 1).set(column - 1, value);

        // Increment the placed tile count
        placedCount++;
    }

	// Determine whether or not a given value is a hint
	public boolean getIsHint(int value) {
	    // Search the hint list
		for (int i = 0; i < hintList.size(); i++) {
		    // Does this hint contain our value?
			if (hintList.get(i).getValue() == value) {
			    // Our value is a hint
			    return true;
			}
		}

		// Made it here then our value is not a hint
		return false;
	}

	// Undo the last move
	public void undo() {
	    // Remove forced moves until we encounter either
		// a non-forced move or the end of the move list
		while (true) {
		    // Are there moves on the move list? If not then break
			if (moveList.size() == 0) {
			    break;
			}

		    // Get the last move
	        Move lastMove = moveList.get(moveList.size() - 1);

			// Is this a forced move?
			if (lastMove.getIsForced() == true) {
		        // Remove the move from the move list
			    if (Numbrix.DEBUG) System.out.println("Removing forced move " + lastMove);
	            moveList.remove(moveList.size() - 1);

		        // Remove the move from the board
				try {
                    setValue(lastMove.getRow(), lastMove.getColumn(), 0);
				}
				catch (BoardException e) {
				    System.out.println("Caught BoardException: " + e.getMessage());
				}
				catch (CannotUseHintException e) {
				    System.out.println("ERROR: Unexpected state: Caught CannotUseHintException but should not ever get this exception (1)");
				}
			} else {
			    // No more forced moves
				break;
			}
		}

		// Remove the next non-forced move, if any
		if (moveList.size() > 0) {
		    // Get the last move
	        Move lastMove = moveList.get(moveList.size() - 1);

		    // Remove the move from the board
			try {
                setValue(lastMove.getRow(), lastMove.getColumn(), 0);
			}
			catch (BoardException e) {
			    System.out.println("Caught BoardException: " + e.getMessage());
			}
			catch (CannotUseHintException e) {
			    System.out.println("ERROR: Unexpected state: Caught CannotUseHintException but should not ever get this exception (2)");
			}

		    // Remove the move from the move list
			if (Numbrix.DEBUG) System.out.println("Removing non-forced move " + lastMove);
			if (Numbrix.DEBUG) System.out.println("");
	        moveList.remove(moveList.size() - 1);
		}

		// Display board
		if (Numbrix.DEBUG) System.out.println("Board after undo:");
		if (Numbrix.DEBUG) System.out.println("");
		if (Numbrix.DEBUG) System.out.println(toString());
		if (Numbrix.DEBUG) System.out.println("");
	}

    // Get the distance between two values
    private int getDistance(int valueOne, int valueTwo) throws BoardException {
        // Get the locations of the two values
        Location locationOne = findValue(valueOne);
        Location locationTwo = findValue(valueTwo);

        // Throw exception if either value not in play
        if (locationOne == null) {
            throw new BoardException("Value " + valueOne + " is not in play");
        }
        if (locationTwo == null) {
            throw new BoardException("Value " + valueTwo + " is not in play");
        }

        // Return the distance
        return Math.abs(locationOne.getRow() - locationTwo.getRow()) + Math.abs(locationOne.getColumn() - locationTwo.getColumn());
    }

	// Determine if the board is in an invalid state
	public boolean getIsInInvalidState() {
	    // Board is in an invalid state if for any placed value,
		// all 4 adjacent tiles are filled and n - 1 and n + 1 are not among the filled-in values

		// Display board
		if (Numbrix.DEBUG) System.out.println("Is the board in an invalid state?");
		if (Numbrix.DEBUG) System.out.println("");
		if (Numbrix.DEBUG) System.out.println(toString());
		if (Numbrix.DEBUG) System.out.println("");

		// Iterate all locations
		// If there is a value at a given location, check to see if there are any available locations
		// If there are not, check to see if n - 1 and n + 1 are among the adjacent values
		// If not, board is in an invalid state
		for (int row = 1; row <= size; row++) {
		    for (int column = 1; column <= size; column++) {
			    // The value at this row and column
				int value = getValueUnsafe(row, column);

			    // Is there a value here?
				if (value == 0) {
				    // No value here
					continue;
				}

				// We'll need this list for the following checks
				List<Integer> adjacentValueList = getAdjacentValueList(new Location(row, column));

				// Is n - 1 in play? If so, is it adjacent?
				Location nMinusOneLocation = findValue(value - 1);
				if (value > 1 && nMinusOneLocation != null) {
				    // This value must be adjacent to our value
					if (adjacentValueList.contains(value - 1) == false) {
					    // Invalid state
				        if (Numbrix.DEBUG) System.out.println("Row " + row + ", column " + column + " is invalid (not adjacent to n - 1))");
					    if (Numbrix.DEBUG) System.out.println("");
					    return true;
					}
				}

				// Is n + 1 in play? If so, is it adjacent?
				Location nPlusOneLocation = findValue(value + 1);
				if (value > 1 && nPlusOneLocation != null) {
				    // This value must be adjacent to our value
					if (adjacentValueList.contains(value + 1) == false) {
					    // Invalid state
				        if (Numbrix.DEBUG) System.out.println("Row " + row + ", column " + column + " is invalid (not adjacent to n + 1)");
					    if (Numbrix.DEBUG) System.out.println("");
					    return true;
					}
				}

				// How many available locations?
				// Don't perform the following check if the value isn't bordered on all 4 sides
				List<Location> availableLocationList = getAvailableLocationList(new Location(row, column));
				if (availableLocationList.size() > 0) {
				    // There are available locations
					continue;
				}

				// Values which are bordered on all 4 sides *must* be adjacent to n - 1 and n + 1
				boolean nMinusOnePresent = false;
				boolean nPlusOnePresent = false;
				for (int i = 0; i < adjacentValueList.size(); i++) {
				    // Is this adjacent value equal to n - 1?
				    // Is this adjacent value equal to n + 1?
				    if (adjacentValueList.get(i) == value - 1) {
					    // Adjacent value is equal to n - 1
						nMinusOnePresent = true;
					} else if (adjacentValueList.get(i) == value + 1) {
					    // Adjacent value is equal to n - 1
						nPlusOnePresent = true;
					}
				}

				// Is this the first number or the last number?
				// if so then only one required adjacent value is needed
				if (value == 1 || value == size * size) {
				    if (nMinusOnePresent == false && nPlusOnePresent == false) {
				        // Invalid state
				        if (Numbrix.DEBUG) System.out.println("Row " + row + ", column " + column + " is invalid (not bordered by n - 1/n + 1)");
					    if (Numbrix.DEBUG) System.out.println("");
				        return true;
					} else {
					    // Valid state
					    continue;
					}
				}

				// If n - 1 and n + 1 are not present then the board is in an invalid state
				if (nMinusOnePresent == false || nPlusOnePresent == false) {
				    // Invalid state
				    if (Numbrix.DEBUG) System.out.println("Row " + row + ", column " + column + " is invalid (not bordered by n - 1 and n + 1)");
					if (Numbrix.DEBUG) System.out.println("");
				    return true;
				}
			}
		}

		// If we made it here then the board is in a valid state
	    if (Numbrix.DEBUG) System.out.println("");
		return false;
	}

	// Find the available (unfilled) locations adjacent to a given location
	public List<Location> getAvailableLocationList(Location location) {
	    // The available location list
		List<Location> availableLocationList = new ArrayList<Location>();

		// Is the above location available?
		if (location.getRow() != size && getValueUnsafe(location.getRow() + 1, location.getColumn()) == 0) {
		    availableLocationList.add(new Location(location.getRow() + 1, location.getColumn()));
		}

		// Is the below location available?
		if (location.getRow() != 1 && getValueUnsafe(location.getRow() - 1, location.getColumn()) == 0) {
		    availableLocationList.add(new Location(location.getRow() - 1, location.getColumn()));
		}

		// Is the left location available?
		if (location.getColumn() != 1 && getValueUnsafe(location.getRow(), location.getColumn() - 1) == 0) {
		    availableLocationList.add(new Location(location.getRow(), location.getColumn() - 1));
		}

		// Is the left location available?
		if (location.getColumn() != size && getValueUnsafe(location.getRow(), location.getColumn() + 1) == 0) {
		    availableLocationList.add(new Location(location.getRow(), location.getColumn() + 1));
		}

        // Return the available location list
	    return availableLocationList;
	}

	// Find the location of a number
	public Location findValue(int value) {
	    // Search the board for this value
		for (int row = 1; row <= size; row++) {
		    for (int column = 1; column <= size; column++) {
                // Is this our value?
				if (getValueUnsafe(row, column) == value) {
				    return new Location(row, column);
				}
			}
		}

        // Didn't find the value
	    return null;
	}

	// Get the next value not in play
	public int getNextValue() throws BoardException {
	    // Throw an exception if the board is already full
		if (placedCount == size * size) {
		    System.out.println("ERROR: Trying to get next value when all values are already placed");
			throw new BoardException("Trying to get next value when all values are already placed");
		}

		// List of values
		List<Integer> valueList = new ArrayList<Integer>();
		for (int row = 0; row < size; row++) {
		    for (int column = 0; column < size; column++) {
			    // Skip board positions that are not in play
			    if (rowList.get(row).get(column) == 0) {
				    continue;
				}

                // Add the value
		        valueList.add(rowList.get(row).get(column));
		    }
		}

		// Sort list
		Collections.sort(valueList);

		// For each number:
		// Is n - 1 not in the list? Return n - 1
		// Else, is n + 1 not in the list? Return n + 1
		for (int i = 0; i < valueList.size(); i++) {
		    if (valueList.get(i) > 1 && ! valueList.contains(valueList.get(i) - 1)) {
                return valueList.get(i) - 1;
			} else if (! valueList.contains(valueList.get(i) + 1)) {
			    return valueList.get(i) + 1;
			}
		}

		// Should not make it here
		System.out.println("ERROR: Unexpected state: Should have found a next value");
		throw new BoardException("Unexpected state: Should have found a next value");
	}

    // Get the won status
    public boolean isWon() {
        return isWon;
    }

	// Get the board size
	public int getSize() {
	    return size;
	}

	// Get the move count
	public int getMoveCount() {
	    return moveCount;
	}

	// Get list of values in tiles adjacent to a given location
	public List<Integer> getAdjacentValueList(Location location) {
	    // The adjacent value list
		List<Integer> adjacentValueList = new ArrayList<Integer>();

		// Is there a value above?
		if (location.getRow() != 1 && getValueUnsafe(location.getRow() - 1, location.getColumn()) != 0) {
		    adjacentValueList.add(getValueUnsafe(location.getRow() - 1, location.getColumn()));
		}

		// Is there a value below?
		if (location.getRow() != size && getValueUnsafe(location.getRow() + 1, location.getColumn()) != 0) {
		    adjacentValueList.add(getValueUnsafe(location.getRow() + 1, location.getColumn()));
		}

		// Is there a value to the left?
		if (location.getColumn() != 1 && getValueUnsafe(location.getRow(), location.getColumn() - 1) != 0) {
		    adjacentValueList.add(getValueUnsafe(location.getRow(), location.getColumn() - 1));
		}

		// Is there a value to the right?
		if (location.getColumn() != size && getValueUnsafe(location.getRow(), location.getColumn() + 1) != 0) {
		    adjacentValueList.add(getValueUnsafe(location.getRow(), location.getColumn() + 1));
		}

		// Return the adjacent value list
		return adjacentValueList;
	}

    // Determine if the game is complete
    private boolean isGameComplete() {
        // For storing locations of adjacent tiles
        ArrayList<Location> adjacencyList = new ArrayList<Location>();

        // Try to build a chain (snake) from 1 to the last number
        for (int n = 1; n < size * size; n++) {
            // Get location of value
            Location location = getLocationOfValue(n);

            // Clear the adjacency list
            adjacencyList.clear();

            // Generate locations of adjacent tiles
            if (location.getRow() + 1 <= size) {
                adjacencyList.add(new Location(location.getRow() + 1, location.getColumn()));
            }
            if (location.getColumn() + 1 <= size) {
                adjacencyList.add(new Location(location.getRow(), location.getColumn() + 1));
            }
            if (location.getRow() - 1 > 0) {
                adjacencyList.add(new Location(location.getRow() - 1, location.getColumn()));
            }
            if (location.getColumn() - 1 > 0) {
                adjacencyList.add(new Location(location.getRow(), location.getColumn() - 1));
            }

            // Try to reach next number
            boolean reachable = false;
            for (int i = 0; i < adjacencyList.size(); i++) {
                if (rowList.get(adjacencyList.get(i).getRow() - 1).get(adjacencyList.get(i).getColumn() - 1) == n + 1) {
                    reachable = true;
                    break;
                }
            }

            // Is the next number reachable?
            if (reachable == false) {
                return false;
            }

            // Is the next number the last number?
            if (n + 1 == size * size) {
                return true;
            }
        }

        // Shouldn't make it here
        System.out.println("***** REACHED AN UNEXPECTED STATE *****");
        return false;
    }

    // Validate board coordinates
    private void validateCoordinates(int row, int column) throws BoardException {
        // Determine if row and column are in bounds
        if (row <= 0 || column <= 0 || row > size || column > size) {
            throw new IllegalPositionException("Row and column must be between 1 and " + size);
        }

        // Determine if row and column are a hint
        for (int i = 0; i < hintList.size(); i++) {
            if (hintList.get(i).getRow() == row && hintList.get(i).getColumn() == column) {
                throw new IllegalPositionException("You cannot place a move on a tile which contains a hint");
            }
        }
    }

    // Validate value
    private void validateValue(int value) throws BoardException, CannotUseHintException {
        // Determine if value is in bounds
        if (value < 0 || value > size * size) {
            throw new IllegalValueException("Value must be between 0 and " + (size * size));
        }

        // Determine if value is used by a hint
        for (int i = 0; i < hintList.size(); i++) {
            if (hintList.get(i).getValue() == value) {
                throw new CannotUseHintException("You cannot place a number which is already used by a hint");
            }
        }
    }

    // Find the location of a particular value
    private Location getLocationOfValue(int value) {
        // Search for value
        // Skip the search if the value is 0
        Location location = null;
        if (value > 0) {
            for (int row = 0; row < size; row++) {
                for (int column = 0; column < size; column++) {
                    if (rowList.get(row).get(column) == value) {
                        location = new Location(row + 1, column + 1);
                        break;
                    }
                }

                // Done searching?
                if (location != null) {
                    break;
                }
            }
        }

        // Return the location (null if not found)
        return location;
    }
}
