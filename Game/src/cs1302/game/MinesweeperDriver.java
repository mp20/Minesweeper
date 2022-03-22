package cs1302.game;

import java.util.Scanner;
import java.io.FileNotFoundException;

/**
 * The MinesweeperDriver class get a seed from the argumentline and passed that as well as the
 * scanner into MinesweeperGame. It does this through a MinesweeperGame constructor. It is also
 * responsible for starting the game.
 */
public class MinesweeperDriver {

    public static void main(String[] args) {
        if (args.length != 1) {
            System.err.println("\nUsage: MinesweeperDriver SEED_FILE_PATH");
            System.exit(1);
        }

        String seedPath = args[0];
        Scanner stdIn = new Scanner(System.in);
        MinesweeperGame game = new  MinesweeperGame(stdIn, seedPath);
        game.play();

    } //main
} //class
