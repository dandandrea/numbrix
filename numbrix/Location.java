package numbrix;

public class Location {
    private int row;
    private int column;
    private int value;

    public Location(int row, int column) {
        this.row = row;
        this.column = column;
		this.value = 0;
    }

    public Location(int row, int column, int value) {
        this.row = row;
        this.column = column;
		this.value = value;
    }

    public int getRow() {
        return row;
    }

    public int getColumn() {
        return column;
    }

	public int getValue() {
	    return value;
	}

    public String toString() {
        return "row " + row + ", column " + column + (value != 0 ? ", value " + value : "");
    }
}
