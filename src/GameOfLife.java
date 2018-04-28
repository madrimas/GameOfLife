import processing.core.PApplet;


public class GameOfLife extends PApplet {
    public static void main(String[] args) {
        PApplet.main("GameOfLife", args);
    }

    private int cellSize = 20; // min value = 2, max value = min value of size
    private int[][] cells;
    private int[][] cellsBuffer; // Buffer to record the state of the cells and use this while changing the others in the iterations
    private int lastRecordedTime = 0;
    private boolean pause = false;

    @Override
    public void settings() {
        size(1280, 720);
        noSmooth();
    }

    @Override
    public void setup() {
        cells = new int[width / cellSize][height / cellSize];
        cellsBuffer = new int[width / cellSize][height / cellSize];

        stroke(50); // This stroke will draw the background grid
        frameRate(60);
        initialization(); // Initialization of cells
        background(0); // Fill in black in case cells don't cover all the windows
    }

    @Override
    public void draw() {
        //Draw grid
        for (int x = 0; x < width / cellSize; x++) {
            for (int y = 0; y < height / cellSize; y++) {
                if (cells[x][y] == 1) {
                    fill(204, 102, 0); // If alive
                } else {
                    fill(0); // If dead
                }
                rect(x * cellSize, y * cellSize, cellSize, cellSize);
            }
        }

        int interval = 100;
        if (millis() - lastRecordedTime > interval) { // Iterate if timer ticks
            if (!pause) {
                iteration();
                lastRecordedTime = millis();
            }
        }

        // Create  new cells manually on pause
        if (pause && mousePressed) {
            // Map and avoid out of bound errors
            int xCellOver = (int) (map(mouseX, 0, width, 0, width / cellSize));
            xCellOver = constrain(xCellOver, 0, width / cellSize - 1);
            int yCellOver = (int) (map(mouseY, 0, height, 0, height / cellSize));
            yCellOver = constrain(yCellOver, 0, height / cellSize - 1);

            // Check against cells in buffer
            if (cellsBuffer[xCellOver][yCellOver] == 1) { // Cell is alive
                cells[xCellOver][yCellOver] = 0; // Kill
                fill(0); // Fill with kill color
            } else { // Cell is dead
                cells[xCellOver][yCellOver] = 1; // Make alive
                fill(204, 102, 0); // Fill alive color
            }
        } else if (pause && !mousePressed) { // And then save to buffer once mouse goes up
            // Save cells to buffer (so we operate with one array keeping the other intact)
            for (int x = 0; x < width / cellSize; x++) {
                System.arraycopy(cells[x], 0, cellsBuffer[x], 0, height / cellSize);
            }
        }
    }

    @Override
    public void keyPressed() {
        if (key == 'r' || key == 'R') {
            // Restart: reinitialization of cells
            initialization();
        }
        if (key == ' ') { // On/off of pause
            pause = !pause;
        }
        if (key == 'c' || key == 'C') { // Clear all
            for (int x = 0; x < width / cellSize; x++) {
                for (int y = 0; y < height / cellSize; y++) {
                    cells[x][y] = 0; // Save all to zero
                }
            }
        }
    }

    private void initialization() {
        for (int x = 0; x < width / cellSize; x++) {
            for (int y = 0; y < height / cellSize; y++) {
                float state = random(100);
                // How likely for a cell to be alive at start (in percentage)
                float probabilityOfAliveAtStart = 10;
                if (state > probabilityOfAliveAtStart) {
                    state = 0;
                } else {
                    state = 1;
                }
                cells[x][y] = (int) (state); // Save state of each cell
            }
        }
    }

    private void iteration() { // When the clock ticks
        // Save cells to buffer (so we operate with one array keeping the other intact)
        for (int x = 0; x < width / cellSize; x++) {
            System.arraycopy(cells[x], 0, cellsBuffer[x], 0, height / cellSize);
        }

        // Visit each cell:
        for (int x = 0; x < width / cellSize; x++) {
            for (int y = 0; y < height / cellSize; y++) {
                // And visit all the neighbours of each cell
                int neighbours = 0; // We'll count the neighbours
                for (int xx = x - 1; xx <= x + 1; xx++) {
                    for (int yy = y - 1; yy <= y + 1; yy++) {
                        if (((xx >= 0) && (xx < width / cellSize)) && ((yy >= 0) && (yy < height / cellSize))) { // Make sure you are not out of bounds
                            if (!((xx == x) && (yy == y))) { // Make sure to to check against self
                                if (cellsBuffer[xx][yy] == 1) {
                                    neighbours++; // Check alive neighbours and count them
                                }
                            } // End of if
                        } // End of if
                    } // End of yy loop
                } //End of xx loop
                // We've checked the neighbours: apply rules!
                if (cellsBuffer[x][y] == 1) { // The cell is alive: kill it if necessary
                    if (neighbours < 2 || neighbours > 3) {
                        cells[x][y] = 0; // Die unless it has 2 or 3 neighbours
                    }
                } else { // The cell is dead: make it live if necessary
                    if (neighbours == 3) {
                        cells[x][y] = 1; // Only if it has 3 neighbours
                    }
                } // End of if
            } // End of y loop
        } // End of x loop
    } // End of function
}
