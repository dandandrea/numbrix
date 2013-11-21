package numbrix;

public class AbstractMove {
    // Row, column, value
    protected int row, column, value;

	// Is forced
	protected boolean isForced;

    // Constructor
    public AbstractMove(int row, int column, int value) {
        this.row = row;
        this.column = column;
        this.value = value;
		this.isForced = false;
    }

    // Constructor
    public AbstractMove(int row, int column, int value, boolean isForced) {
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

	// toString()
	public String toString() {
	    return "row " + row + ", column " + column + ", value " + value + ", isForced " + isForced;
	}
}
