import java.util.ArrayList;
import java.util.Scanner;
import java.util.InputMismatchException;

class Numbrix {
    public static void main(String[] args) throws NumbrixException {
        // Display some basic instructions
        System.out.println("");
        System.out.println("            Welcome to Numbrix!");
        System.out.println("-----------------------------------------------");
        System.out.println("Enter your move in row column value format");
        System.out.println("For example, enter 3 4 2 to place a 2 at (3, 4)");
        System.out.println("Place a zero to clear a position");
        System.out.println("Enter quit or exit to stop playing");
        System.out.println("-----------------------------------------------");
        System.out.println("");

        // Play another game loop
        while (true) {
            // Get board size
            System.out.println("Which board do you want?");
            System.out.println("Enter a number between 1 and 2, inclusive");
            int boardNumber = 0;
            Scanner scanner;
            while (true) {
                // Get user input
                try {
                    scanner = new Scanner(System.in);
                    boardNumber = scanner.nextInt();
                }
                catch (InputMismatchException e) {
                    // User did not enter a number
                    System.out.println("");
                    System.out.println("Please enter a number between 1 and 2, inclusive");

                    // Get input again
                    continue;
                }

                // Is the number out of range?
                if (boardNumber < 1 || boardNumber > 2) {
                    // Board size out of range
                    System.out.println("");
                    System.out.println("Board size must be between 1 and 2, inclusive");
                } else {
                    // Got a valid board number
                    break;
                }
            }

            // Initialize the board
            Board board = initializeBoard(boardNumber);

            // Main input loop
            scanner = new Scanner(System.in);
            while (true) {
                // Display the board
                System.out.println("");
                System.out.println(board);
                System.out.println("");

                // Get user input
                String input = scanner.nextLine();

                // Quit?
                if (input.toLowerCase().equals("quit") || input.toLowerCase().equals("exit")) {
                    break;
                }

                // Parse the input into a move
                Move move;
                try {
                    move = parseInput(input);
                }
                catch (IllegalInputFormatException e) {
                    System.out.println("");
                    System.out.println(e.getMessage());
                    continue;
                }

                // Make the move
                try {
                    board.setValue(move.getRow(), move.getColumn(), move.getValue());
                }
                catch (BoardException e) {
                    System.out.println("");
                    System.out.println(e.getMessage());
                }

                // Has the game been won?
                if (board.isWon() == true) {
                    System.out.println("");
                    System.out.println("You won!");
                    break;
                }
            }

            // Display the board
            System.out.println("");
            System.out.println("Here is the final board state:");
            System.out.println("");
            System.out.println(board);

            // Display the move list
            System.out.println("");
            System.out.println("Here is your move list:");
            System.out.println("");
            board.displayMoveList();

            // Prompt to play another game
            boolean playAnotherGame = false;
            while (true) {
                System.out.println("");
                System.out.println("Play another game? (y/n)");
                scanner = new Scanner(System.in);
                String input = scanner.nextLine();
                if (input.toLowerCase().equals("y") == true) {
                    playAnotherGame = true;
                    break;
                } else if (input.toLowerCase().equals("n") == true) {
                    break;
                }
            }

            // Play another game?
            if (playAnotherGame == false) {
                break;
            }

            // We're going to play another game
            System.out.println("");
        }

        System.out.println("");
        System.out.println("Later!");
        System.out.println("");
    }

    // Parse input
    private static Move parseInput(String input) throws IllegalInputFormatException {
        // Get the row
        int row;
        Scanner scanner = new Scanner(input);
        if (scanner.hasNextInt() == false) {
            throw new IllegalInputFormatException("Your input is not in the correct format");
        } else {
            row = scanner.nextInt();
        }

        // Get the column
        int column;
        if (scanner.hasNextInt() == false) {
            throw new IllegalInputFormatException("Your input is not in the correct format");
        } else {
            column = scanner.nextInt();
        }

        // Get the value
        int value;
        if (scanner.hasNextInt() == false) {
            throw new IllegalInputFormatException("Your input is not in the correct format");
        } else {
            value = scanner.nextInt();
        }

        // Return the move
        return new Move(row, column, value);
    }

    // Initialize board
    private static Board initializeBoard(int boardNumber) throws BoardException {
        // Initialize board along with its size
        Board board;
        if (boardNumber == 1) {
            board = new Board(3);
        } else if (boardNumber == 2) {
            board = new Board(8);
        } else {
            throw new BoardException("Invalid board number");
        }

        // Add hints
        if (boardNumber == 1) {
            board.addHint(3, 1, 1);
            board.addHint(3, 3, 3);
            board.addHint(1, 1, 7);
            board.addHint(1, 3, 9);

            /*
            board.addHint(3, 2, 2);
            board.addHint(2, 1, 6);
            board.addHint(2, 3, 4);
            board.addHint(1, 2, 8);

            board.addHint(3, 2, 2);
            board.addHint(1, 2, 4);
            board.addHint(2, 1, 5);
            board.addHint(2, 3, 6);
            */
        } else {
            board.addHint(8, 1, 45);
            board.addHint(8, 2, 44);
            board.addHint(8, 3, 39);
            board.addHint(8, 4, 38);
            board.addHint(8, 5, 23);
            board.addHint(8, 6, 22);
            board.addHint(8, 7, 19);
            board.addHint(8, 8, 18);
            board.addHint(1, 1, 58);
            board.addHint(1, 2, 57);
            board.addHint(1, 3, 56);
            board.addHint(1, 4, 55);
            board.addHint(1, 5, 8);
            board.addHint(1, 6, 7);
            board.addHint(1, 7, 6);
            board.addHint(1, 8, 5);
            board.addHint(7, 1, 46);
            board.addHint(6, 1, 47);
            board.addHint(5, 1, 48);
            board.addHint(4, 1, 63);
            board.addHint(3, 1, 64);
            board.addHint(2, 1, 59);
            board.addHint(7, 8, 17);
            board.addHint(6, 8, 16);
            board.addHint(5, 8, 15);
            board.addHint(4, 8, 14);
            board.addHint(3, 8, 3);
            board.addHint(2, 8, 4);
       }

       // Return the board
       return board;
    }
}
