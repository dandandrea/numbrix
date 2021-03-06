package numbrix;

import java.io.*;
import java.util.*;
import numbrix.exception.*;

public class Numbrix {
    // Whether or not we are in debug mode
    public static boolean DEBUG = false;

    // The main entry point for Numbrix
    public static void main(String[] args) throws NumbrixException {
	    // Introductory message
        System.out.println("-------------------");
        System.out.println("Welcome to Numbrix!");
        System.out.println("-------------------");
		System.out.println("");

		// Was the board filename provided as a command-line argument?
		String filenameArgument = null;
		if (args != null && args.length == 1) {
		    filenameArgument = args[0];
		}

		// The board
		Board board = null;

        // Play another game loop
        while (true) {
            // Get board filename
            System.out.println("Please enter the name of a file containing board hints");
            Scanner scanner = new Scanner(System.in);
            while (true) {
			    // Use filename provided as command-line argument, if present
				String filename = null;
				if (filenameArgument != null) {
				    // Filename was provided as command-line argument
				    System.out.println(">>> Filename was provided as command-line argument");
				    filename = filenameArgument;
				} else {
                    // Get filename from user
                    scanner = new Scanner(System.in);
			        filename = scanner.next();
				}

				// Is this a valid filename?
				File file = new File(filename);
				if (file.exists() == false) {
				    System.out.println("ERROR: Invalid filename, please try again");

					// Nullify filename command-line argument
					filenameArgument = null;

					// Try again
					continue;
				}

				// Got a valid filename
				// Initialize the board (and validate the file format)
				try {
                    board = initializeBoard(file);
				    break;
				}
				catch (IllegalFileFormatException e) {
				    System.out.println("ERROR: " + e.getMessage() + ", please try again");
				}
            }

			// Get player type (human or computer)
			boolean isHumanPlayer = true;
			while (true) {
			    // Print out the choices
			    System.out.println("");
			    System.out.println("Will this be a human or computer player?");
			    System.out.println("Enter H for human or C for computer");

				// Get the choice and then validate it
			    String humanOrComputer = scanner.next();
				if (humanOrComputer == null || (humanOrComputer.toLowerCase().trim().equals("h") == false && humanOrComputer.toLowerCase().trim().equals("c") == false)) {
                    // Invalid input
					System.out.println("Incorrect choice, please enter either H or C");

				    // Ask again
				    continue;
				}

				// Valid input
				if (humanOrComputer.toLowerCase().trim().equals("c") == true) {
				    isHumanPlayer = false;
				}

				// Move on
				break;
			}

            // Play the game
            if (isHumanPlayer == true) {
			    humanPlay(board);
            } else {
			    try {
                    // Display the board
                    System.out.println("");
                    System.out.println("Here is the initial board state:");
                    System.out.println("");
                    System.out.println(board);

				    // Record start time
                    long startTime = System.nanoTime();

                    // Play the board
			        computerPlay(board);

                    // Record end time
                    long endTime = System.nanoTime();

					// Calculate the time in seconds and format it for display
					float seconds = (float)(endTime - startTime) / (float)1000 / (float)1000 / (float)1000;

                    // Display time taken to solve board
					System.out.println("");
                    System.out.printf(">>> Computer play took %.2f seconds\n", seconds);
                    System.out.printf(">>> Computer attempted %,d moves\n", board.getMoveCount());
				}
				catch (BoardException e) {
				    System.out.println("Error during computer play: " + e.getMessage());
				}
				catch (CannotUseHintException e) {
				    System.out.println("Error during computer play: " + e.getMessage());
				}
			}

            // Display the board
            System.out.println("");
            System.out.println("Here is the final board state:");
            System.out.println("");
            System.out.println(board);

            // Display the move list
            System.out.println("");
            System.out.println("Here is the move list:");
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
        System.out.println("See ya!");
        System.out.println("");
    }

	// Computer play
	private static void computerPlay(Board board) throws BoardException, CannotUseHintException {
	    // Instantiate the computer player
		ComputerPlayer computerPlayer = new ComputerPlayer(board);
	}

	// Human play
	private static void humanPlay(Board board) {
        // Display some basic instructions
        System.out.println("");
        System.out.println("-----------------------------------------------");
        System.out.println("Enter your move in row column value format");
        System.out.println("For example, enter 3 4 2 to place a 2 at (3, 4)");
        System.out.println("Place a zero to clear a position");
        System.out.println("Enter quit or exit to stop playing");
        System.out.println("-----------------------------------------------");

		while (true) {
			// Display the board
            System.out.println("");
			System.out.println(board);
			System.out.println("");

			// Get user input
			System.out.println("Enter your move:");
			System.out.println("");
			Scanner scanner = new Scanner(System.in);
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
			catch (CannotUseHintException e) {
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
    private static Board initializeBoard(File file) throws IllegalFileFormatException, BoardException {
	    // Get a Scanner for the file
		Scanner scanner = null;
		try {
		    scanner = new Scanner(file);
	    }
		catch (FileNotFoundException e) {
		    // This can't happen because we already checked for the existence of the file
		}

	    // Read board size from file
		int boardSize;
		try {
		    boardSize = scanner.nextInt();
		}
		catch (NoSuchElementException e) {
			throw new IllegalFileFormatException("File format is incorrect: could not get board size");
		}

        // Initialize board along with its size
        Board board = new Board(boardSize);

		// Determine how many hints there are
		int numberOfHints;
		try {
		    numberOfHints = scanner.nextInt();
	    }
		catch (NoSuchElementException e) {
			throw new IllegalFileFormatException("File format is incorrect: could not get number of hints");
		}

        // Add hints
		for (int i = 0; i < numberOfHints; i++) {
		    try {
			    // Get the hint details
				int row = scanner.nextInt();
				int column = scanner.nextInt();
			    int value = scanner.nextInt();

				// Add the hint
				try {
				    board.addHint(row, column, value);
				}
				catch (CannotUseHintException e) {
				    System.out.println("Unexpected state: Got CannotUseHintException: " + e.getMessage());
				}
		    }
			catch (NoSuchElementException e) {
			    throw new IllegalFileFormatException("File format is incorrect: error processing hint #" + (i + 1));
			}
			catch (IllegalPositionException e) {
			    throw new IllegalFileFormatException("File format is incorrect: error processing hint #" + (i + 1) + ": " + e.getMessage());
			}
			catch (IllegalValueException e) {
			    throw new IllegalFileFormatException("File format is incorrect: error processing hint #" + (i + 1) + ": " + e.getMessage());
			}
		}

       // Return the board
       return board;
    }
}
