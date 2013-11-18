package numbrix;

public class Location {
    private int row;
    private int column;
    private int value;

    public Location(int row, int column) {
        this.row = row;
        this.column = column;
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
}
