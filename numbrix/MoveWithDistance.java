package numbrix;

public class MoveWithDistance extends AbstractMove implements Comparable<MoveWithDistance> {
    // Distance
    private int distance;

    // Constructor
    public MoveWithDistance(int row, int column, int value, int distance) {
	    super(row, column, value);
		this.distance = distance;
    }

    // Getters
	public int getDistance() {
	    return distance;
	}

	// For Comparable interface
	// Sorts in DESCENDING order so that moves with lower distances are
	// ultimately added to the top of the next move stack
	public int compareTo(MoveWithDistance otherMove) {
	    if (otherMove.getDistance() == distance) {
		    return 0;
		} else if (otherMove.getDistance() > distance) {
		    return 1;
		} else {
		    return -1;
		}
	}

	// toString()
	public String toString() {
	    return "row " + row + ", column " + column + ", value " + value + ", distance " + distance + ", isForced " + isForced;
	}
}
