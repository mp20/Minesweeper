package cs1302.game;

import java.io.FileNotFoundException;
import java.lang.IndexOutOfBoundsException;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.io.File;
import java.lang.ArrayIndexOutOfBoundsException;


/**
 * This is the MinesweeperGame class that handles the netire game of Minesweeper.
 * Once the user correctly passes down stdIn and seedPath the game will run. The game of
 * Minesweeper is about revealing squares and avoiding bombs until you manage to reveal all squares
 * that do not contain bombs while marking the spots where you are certain the bombs are.
 */
public class MinesweeperGame {

    private int rows;
    private int cols;
    private int numMines;
    private final Scanner stdIn;
    private String seedPath;
    //keep track of everything except bombs
    private char[][] characters = null;
    //keeps track of bomb with original scale
    private char[][] bomb = null;
    //keeps track of bomb with widened board
    private char[][] newBomb = null;
    private int rounds = 0;
    //keeps track of number of tiles revealed by the user
    private int tilesRevealed = 0;
    private double score;
    private boolean noFog = false;

    /**
     * creates a constructor for MinsweeperGame.
     * @param stdIn takes in the scanner named stdIN
     * @param seedPath takes in the seed that hold the game information named seedPath
     */
    public MinesweeperGame(Scanner stdIn, String seedPath) {
        this.stdIn = stdIn;
        this.seedPath = seedPath;
    }


    /**
     * Sets up the board by defining the number of mines, rows, cols, and taking care
     * of any errors or exceptions that need to be handled. The errors are all displayed to
     * System.err. It sets the number of rows, cols, and the number of mines from the seed file.
     * it also creates fills two arrays that hold the locations of the bombs. One of these arrays
     * has two extra rows and cols to help with counting adjacent mines.
     * catches FileNotFoundException, NoSuchElementException, and ArrayIndexOutOfBounds.
     */
    public void setboard() {
        try {
            File seed = new File(seedPath);
            Scanner seedScanner = new Scanner(seed);
            rows = seedScanner.nextInt();
            cols = seedScanner.nextInt();
            numMines = seedScanner.nextInt();

            if (numMines < 1 || numMines > (rows * cols) - 1) {
                System.err.println("\nSeed File Malformed Error: Invalid mine count");
                System.exit(3);
            }
            if (rows < 5 || rows > 10 || cols > 10 || cols < 5) {
                System.err.println("\nSeed File Malformed Error: Cannot create a mine field" +
                    " with that many rows and/or columns!");
                System.exit(3);
            }
            this.characters = new char[rows][cols];
            this.bomb = new char[rows + 2][cols + 2];
            this.newBomb = new char[rows][cols];
            //setting the bomb the expanded board
            for (int i = 0; i < numMines; i++) {
                int bombRow = seedScanner.nextInt();
                for (int j = 0; j < 1; j++) {
                    int bombCol = seedScanner.nextInt();
                    bomb[bombRow + 1][bombCol + 1] = 'b';
                    newBomb[bombRow][bombCol] = 'b';
                }
            } //for
        } catch (FileNotFoundException fnfe) {
            System.err.println("\nSeed File Not Found Error:" + fnfe.getMessage());
            System.exit(2);
        } catch (NoSuchElementException nsee) {
            System.err.println("\nSeed File Malformed Error: " + nsee.getMessage());
            System.exit(3);
        } catch (ArrayIndexOutOfBoundsException e) {
            System.err.println("\nSeed File Malformed Error: mine is out of bounds");
            System.exit(3);
        }
    } //setboard

/**
 * Returns the number of mines adjacent to the specified
 * square in the grid that was passed by the user. The square
 * must be in bounds.
 *
 * @param row the row index of the square
 * @param col the column index of the square
 * @return the number of adjacent mines
 */
    private int getNumAdjMines(int row, int col) {
        int counter = 0;
        if (isInBounds(row,col)) {
            row += 1;
            col += 1;
            for (int i = row - 1; i <= row + 1; i++) {
                for (int j = col - 1; j <= col + 1; j++) {
                    if (bomb[i][j] == 'b') {
                        counter += 1;
                    }
                }
            } //for
            for (int i = 0; i < rows; i++) {
                for (int j = 0; j < cols; j++) {
                    newBomb[i][j] = bomb[i + 1][j + 1];
                }
            } //for
            }
        return counter;
    } //getNumAdjMines

    /**
     * Indicates whether or not a particular square/tile is in the game grid
     * as the game.
     * @param row the row index of the square
     * @param col the column index of the square
     * @return true if the square is in the game grid; false otherwise
     */
    private boolean isInBounds(int row, int col) {
        boolean inBounds = true;
        if ((row < 0) || (row > rows - 1)) {
            inBounds = false;
        }
        if ((col < 0) || (col > cols - 1)) {
            inBounds = false;
        }
        return inBounds;
    } //isInBounds

    /**
     * Handles printing out the board and showing all the characters
     * that the user sees after performing commands. It handles printing different if
     * the boolean {@code noFog} has been turned on.
     */
    public void printMineField() {
        for (int i = 0; i < characters.length; i++) {
            System.out.print(" " + i + " " + "|");
            for (int j = 0; j < characters[i].length; j++) {
                if (noFog == true && newBomb[i][j] == 'b') {
                    System.out.print("<");
                    if (characters[i][j] == '\0') {
                        System.out.print(" ");
                    } else {
                        System.out.print(characters[i][j]);
                    }
                    System.out.print(">|");
                    continue;
                }
                System.out.print(" ");
                if (characters[i][j] == '\0') {
                    System.out.print(" ");
                } else {
                    System.out.print(characters[i][j]);
                }
                System.out.print(" |");
            } // for j
            System.out.println();
        } //for i
        System.out.print("     ");
        for (int i = 0; i < cols - 1; i++) {
            System.out.print(i + "   ");
        } //for
        System.out.println(cols - 1 + "  ");
        noFog = false;
    } //printMineField

    /**
     * Handles all of the commands that the user inputs. Also keeps track of the rounds.
     * It delegates the commands to their corresponding methods to be dealth with. Also
     * catched ArrayIndexOutOfBoundsException and NoSuchElementException. If the users command
     * cannot be recognized or is not entered correctly, a message will be prompted to the user.
     *
     */
    public void promptUser() {
        try {
            System.out.println();
            System.out.println(" Rounds Completed: " + rounds + "\n");
            printMineField();
            System.out.println();
            System.out.print("minesweeper-alpha: ");
            String fullCommand = stdIn.nextLine();
            String string = fullCommand;
            int counter = 0;
            //used to count the number of token in the user commands
            Scanner stringToken = new Scanner(string);
            //used to get the user input and row/cols if needed
            Scanner scanner = new Scanner(fullCommand);
            String input = scanner.next();
            //calculates the number of tokens in the full user command (i.e r 0 0)
            while (stringToken.hasNext()) {
                counter += 1;
                String test = stringToken.next();
            }
            if (input.equals("nofog") && counter == 1) {
                noFog();
                rounds -= 1;
            } else if ((input.equals("help") || input.equals("h")) &&  counter == 1) {
                help();
            } else if ((input.equals("quit") || input.equals("q")) && counter == 1) {
                System.out.println();
                System.out.println("Quitting the game...\nBye!");
                System.exit(0);
            } else if ((input.equals("r") || input.equals("reveal")) && counter == 3) {
                int row = scanner.nextInt();
                int col = scanner.nextInt();
                reveal(row, col);
            } else if ((input.equals("g") || input.equals("guess")) && counter == 3) {
                int row = scanner.nextInt();
                int col = scanner.nextInt();
                guess(row, col);
            } else if ((input.equals("m") || input.equals("mark")) && counter == 3) {
                int row = scanner.nextInt();
                int col = scanner.nextInt();
                mark(row, col);
            } else if ((input.equals("help") || input.equals("h") || input.equals("quit")
            || input.equals("q") || input.equals("nofog")) && counter > 1) {
                System.err.println();
                System.err.println("Input Error: command not recognized!");
            } else if ((input.equals("r") || input.equals("reveal") || input.equals("guess") ||
            input.equals("g")|| input.equals("m") || input.equals("mark")) && counter > 3) {
                System.err.println();
                System.err.println("Input Error: command not recognized!");
            } else {
                System.err.println();
                System.err.println("Invalid Command: command not recognized!");
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            System.err.println();
            System.err.println("Invalid Command: " + e.getMessage());
            rounds -= 1;
        } catch (NoSuchElementException e) {
            System.err.println();
            System.err.println("Invalid Command: command not recognized!");
        }
    } //promptUser

    /**
     * keeps track of the number of squares/tiles revealed except those that are bombs.
     * @param row takes in the given row for the cell that has been revealed
     * @param col takes in the given column for the column that has been revealed
     * @return tilesRevealed it returns the number of tiles that have been revealed (non bomb tiles)
     */
    public int isRevealed(int row, int col) {
        tilesRevealed += 1;
        return tilesRevealed;
    } //isRevealed

    /**
     * Handles revealing a square as well as calling a method that keeps
     * track of the number of tiles that have been revealed (i.e. isRevealed(int row, int col)).
     * @param row takes in the given row for the cell to be revealed
     * @param col takes in the given row for the cell to be revealed
     */
    public void reveal(int row, int col) {
        rounds += 1;
        if (newBomb[row][col] == 'b') {
            printLost();
        } else {
            isRevealed(row, col);
            int AdjMines = getNumAdjMines(row, col);
            //store the adj mines as a char so it can be stores in characters array
            characters[row][col] = (char) (AdjMines + '0');
        }
    } //reveal

    /**
     * Handles marking the squares that the user want to guess with a '?'.
     * It stores these characters in the {@code characters} array.
     * @param row takes in the given row for the cell to be guessed
     * @param col takes in the given row for the cell to be guessed
     */
    public void guess(int row, int col) {
        rounds += 1;
        characters[row][col] = '?';
    } //guess

    /**
     * Handles marking the squares that the user wants to flag with an F.
     * it stores these characters in the {@code characters} array.
     * @param row takes in the given row for the cell to be marked
     * @param col takes in the given column for the cell to be marked
     */
    public void mark(int row, int col) {
        rounds += 1;
        characters[row][col] = 'F';
    } //mark

    /**
     * Handles the procedures for nofog by turning {@code noFog} status into true
     * so that {@link printMineField()} knows what to print.
     */
    public void noFog() {
        rounds += 1;
        noFog = true;
    }

    /**
     * Handles the procedures of the help command
     * such as printing the available commands(excluding nofog).
     */
    public void help() {
        rounds += 1;
        System.out.println();
        System.out.println("Commands Available...");
        System.out.println(" - Reveal: r/reveal row col");
        System.out.println(" -   Mark: m/mark   row col");
        System.out.println(" -  Guess: g/guess  row col");
        System.out.println(" -   Help: h/help");
        System.out.println(" -   Quit: q/quit");
    } //help

    /**
     * Prints the welcome text from the file welcome.txt.
     */
    public void printWelcome() {
        //goes through the lines of the welcome.txt file and prints each line
        try {
            File configFile = new File("resources/welcome.txt");
            Scanner configScanner = new Scanner(configFile);
            while (configScanner.hasNextLine()) {
                System.out.println(configScanner.nextLine());
            }
        } catch (FileNotFoundException e) {
            System.err.println();
            System.err.println(e.getMessage());
        }
        System.out.println();
    } //printWelcome

    /**
     * Checks to see if the conditions for winning have been met. If so,
     * it turn the boolean won into true
     * @return won true meaning the user has won, false otherwise.
     */
    public boolean won() {
        //won keeps track of if the user has won or not
        boolean won = false;
        int counter = 0;
        for (int i = 0; i < newBomb.length; i++) {
            for (int j = 0; j < newBomb[i].length; j++) {
                if ((newBomb[i][j] == 'b') && (characters[i][j] == 'F')) {
                    counter += 1;
                }
            }
        } //for
        //calculates the total number of tiles (that are not bombs) in a grid
        int MaxNumTiles = (rows * cols) - numMines;
        //calculates the win condition
        if ((tilesRevealed == MaxNumTiles) && counter == numMines) {
            won = true;
        }
        return won;
    }

    /**
     * Prints the losing screen if the conditions have been met.
     */
    public void printLost() {
        //goes through the gameover.txt file and prints each line
        try {
            File configFile = new File("resources/gameover.txt");
            Scanner configScanner = new Scanner(configFile);
            System.out.println();
            while (configScanner.hasNextLine()) {
                System.out.println(configScanner.nextLine());
            }
        } catch (FileNotFoundException e) {
            System.err.println();
            System.err.println(e.getMessage());
        }
        System.out.println();
        System.exit(0);
    }

    /**
     * Prints the winning screen from a file and calculates the score.
     *
     */
    public void printWon() {
        System.out.println();
        int num = 0;
        int count = 0;
        //calculates score
        score = ((100.00 * rows * cols) / rounds);
        //formates the score to two decimal palces
        String formattedScore = String.format("%.02f", score);
        try {
            File file = new File("resources/gamewon.txt");
            Scanner scanner = new Scanner(file);
            while (scanner.hasNextLine()) {
                num += 1;
                String test = scanner.nextLine();
            }
            Scanner scanner1 = new Scanner(file);
            for (int i = 1; i < num; i++) {
                System.out.println(scanner1.nextLine());
            } //for
            System.out.println(scanner1.nextLine() + " " + formattedScore + "\n");
        } catch (FileNotFoundException e) {
            System.err.println();
            System.err.println(e.getMessage());
        }
    } //printWon

    /**
     * Is only called once from the driver and calls the methods
     * needed to run the game in a loop.
     */
    public void play() {
        setboard();
        printWelcome();
         while (!won()) {
            promptUser();
         }
         printWon();
    } //play
} //MineSweeperGame
