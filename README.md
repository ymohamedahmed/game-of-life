# Game of Life
A program written to display the cellular automaton devised by the mathematician John Conway.
![](https://github.com/ymohamedahmed/game-of-life/blob/sound/res/gol-2.gif)
## Rules
* Each cell is either 'living' or 'dead'. Live cells are coloured black and dead cells are white.
* A live cell with fewer than two living neighbours dies - *underpopulation*.
* A live cell with two or three living neighbours lives on.
* A live cell with more than three living neighbours - *overpopulation*.
* A dead cell with exactly three neighbours comes alive - *reproduction*.
## Sound
I also added a feature whereby each row in the grid produces a frequency based on the number of living cells in that row. The frequencies for each row are then played to produce a sound for the grid. Warning it isn't the most sonically pleasing.
## Controls
* Click to set square to dead or alive
* Press enter to start simulation
* Press r to reset grid
* Press c to clear the grid
