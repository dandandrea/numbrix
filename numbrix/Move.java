package numbrix;

public class Move implements Comparable<Move> {
    // Row, column, value
    private int row, column, value;

	// Is forced
	private boolean isForced;

    // Constructor
    public Move(int row, int column, int value) {
        this.row = row;
        this.column = column;
        this.value = value;
		this.isForced = false;
    }

    // Constructor
    public Move(int row, int column, int value, boolean isForced) {
        this.row = row;
        this.column = column;
        this.value = value;
		this.isForced = isForced;
    }

    // Getters
    public int getRow() {
        return row;
    }
    public int getColumn() {
        return column;
    }
    public int getValue() {
        return value;
    }
	public boolean getIsForced() {
	    return isForced;
	}

	// For Comparable interface
	public int compareTo(Move otherMove) {
	    if (otherMove.getValue() == value) {
		    return 0;
		} else if (otherMove.getValue() > value) {
		    return -1;
		} else {
		    return 1;
		}
	}

	// toString()
	public String toString() {
	    return "row " + row + ", column " + column + ", value " + value + ", isForced " + isForced;
	}
}
