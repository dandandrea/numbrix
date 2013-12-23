This is a solver for the Numbrix game

Read more about Numbirx [here](http://entertainment.howstuffworks.com/puzzles/how-to-play-numbrix.htm)

Usage: java numbrix/Numbrix

Specify a configuration file when prompted and then choose either human play or automatic (computer) play

The configuration file holds the size of the board and the "hints" (or constraints) which are present

The format for the configuration file is as follows:

The first line indicates the size of the board (e.g. enter 5 for a 5x5 board)
The next line indicates the number of hints in this configuration file
The remaining lines contain the row and column each hint
See tournament1.txt in the project root for an example configuration file
