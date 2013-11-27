In order to play Numbrix, please follow these three simple steps:

1. Compile Numbrix

(Do this from the directory where you untarred numbrix_final_dandrea.tar)

javac numbrix/*.java numbrix/exception/*.java numbrix/forcedMove/*.java

2. Run Numbrix

(Also do this from the directory where you untarred numbrix_final_dandrea.tar)

java numbrix/Numbrix

3. Follow the in-game instructions

First, enter the name of the board to play.  The tournament boards are all tournamentX.txt where X is a number between 1 and 17 and corresponding to the numbered tournament boards found in Tournament Data F13.pdf.

So, for example, enter tournament5.txt at the first in-game prompt in order to load the 5th tournament board.

Next, choose whether the game is to be played manually by you or automatically by the computer.  Enter H for human play or C for computer play.

If you choose human play then simply follow the in-game instructions which explain how to place moves and how to terminate gameplay early.

If you choose computer play then sit back and let the computer do its thing.  It will display the initial board state and then start trying to solve the board.  Now output will be created until the solution is found.  So, the solver might appear to be hung on more difficult boards but it is indeed not hung.  It will display the final board state and the moves made to achieve the final board state once it finds a solution.
