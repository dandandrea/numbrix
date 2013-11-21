package numbrix;

public class Move extends AbstractMove implements Comparable<Move> {
    // Constructor
    public Move(int row, int column, int value) {
	    super(row, column, value);
    }

    // Constructor
    public Move(int row, int column, int value, boolean isForced) {
	    super(row, column, value, isForced);
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
