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

    // Set a board value
    public void setValue(int row, int column, int value) throws BoardException {
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
                moveList.add(new Move(row, column, value));

                // Decrement the placed count if there is a different, non-zero value at the target location
                if (rowList.get(row - 1).get(column - 1) != 0 && rowList.get(row - 1).get(column - 1) != value) {
                    placedCount--;
                }

                // Clear old location and set new location
                rowList.get(location.getRow() - 1).set(location.getColumn() - 1, 0);
                rowList.get(row - 1).set(column - 1, value);
            } else {
                // Add to move list
                moveList.add(new Move(row, column, value));

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
    public void addHint(int row, int column, int value) throws BoardException {
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
	public int getNextValue() {
	    // Concatenate move list and hint list
        List<Move> valueList = new ArrayList<Move>();
		valueList.addAll(moveList);
		valueList.addAll(hintList);

		// Sort list
		Collections.sort(valueList);

		// Find lowest value whose next value is missing from the list
		int lowestValue = -1;
		for (int i = 0; i < valueList.size(); i++) {
		    // This is the next lowest value
			lowestValue = valueList.get(i).getValue();

			// Is the next value in the list equal to our lowest value + 1?
			// If not then stop because we've found our next value
			if (i < valueList.size() - 1 && valueList.get(i + 1).getValue() != lowestValue + 1) {
			    // We have our value
				break;
			}
		}

        // Return next value
		return lowestValue + 1;
	}

    // Get the won status
    public boolean isWon() {
        return isWon;
    }

	// Get the board size
	public int getSize() {
	    return size;
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
    private void validateValue(int value) throws BoardException {
        // Determine if value is in bounds
        if (value < 0 || value > size * size) {
            throw new IllegalValueException("Value must be between 0 and " + (size * size));
        }

        // Determine if value is used by a hint
        for (int i = 0; i < hintList.size(); i++) {
            if (hintList.get(i).getValue() == value) {
                throw new IllegalPositionException("You cannot place a number which is already used by a hint");
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
