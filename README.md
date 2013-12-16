Chess playing program for Artificial Intelligence Fall 2013 class

To run the json processor Jackson must be used. It is included on this git repo. To enable it (in Eclipse) download it and right click on the project folder, and select build path -> add external archives. Then navigate to and select the jar file.
To run the program first open http://www.bencarle.com/chess/startgame enter values (our team is number 106). Then open Board.java and enter the game number and whether the agent is white in the main function, and run the program.

Evaluation function returns the total of the weighted point value of all pieces on board.

program runs a six-ply negamax search with alpha-beta pruning, although it will switch to four-ply if time gets low.

Right now the program runs and seems to play well, but occasionally, a bug that we have not been able to fix causes the program to try to play invalid moves and it stops working altogether. This seems to be less likely when the program is playing as white.