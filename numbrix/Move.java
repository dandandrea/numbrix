package numbrix;

public class Move implements Comparable<Move> {
    // Row, column, value
    private int row, column, value;

    // Constructor
    public Move(int row, int column, int value) {
        this.row = row;
        this.column = column;
        this.value = value;
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
}
