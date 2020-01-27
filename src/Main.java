// Student name: Louise Chan
// Student ID: 101296435


import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.Scanner;

public class Main {

	public static final int GAME_TYPE_NEW = 1;
	public static final int GAME_TYPE_LOADED = 3;
	
	public static final int INPUT_VALID_CHAR = 0;
	public static final int INPUT_SAVE_AND_QUIT = 1;
	public static final int INPUT_QUIT_NO_SAVE = 2;
	public static final int INPUT_INVALID_CHAR = -1;
	
	// Set to true to enable debug messages
	public static final boolean DEBUG_ON = false;
	
	public static void main(String[] args) throws FileNotFoundException {
		Scanner input = new Scanner(System.in);

		int choiceInt = 0;
		
		while(true) {
			
			System.out.println("*****************************");
			System.out.println("      HANG MAN v1.0");
			System.out.println("*****************************");
			
			System.out.println("\n[1] Start the game");
			System.out.println("[2] View the rules of the game");
			System.out.println("[3] Load a previously saved game");

			System.out.print("\nEnter choice [1, 2, or 3]: ");
			String choice = input.nextLine();
			
			choiceInt = checkChoice(choice, 1, 3);
			
			if(choiceInt == 2) {
				printRules();
			}
			else if(choiceInt == 3) {
				if(loadSavedGame().equals("empty")) {
					System.out.println("\n*** No game was saved! Please choose option [1] instead to start a new game. ***\n");
					delay(500);
				}
				else {
					break;
				}
			}
			else if(choiceInt != -1) {
				break;
			}
			
		};
		
		int gameType = choiceInt;		
		Hangman hangman;

		// Check if a new game was started.
		if(gameType == GAME_TYPE_NEW) {	
			
			// New game was started... Allow user to choose categories.
			while(true) {
				System.out.println("\n*************************"
						+          "\n   Select a category: "
						+          "\n*************************");
				
				String[] categories = WordBank.getCategories();
				for (int i = 0; i < WordBank.getNumberOfCategories(); i++) {
					System.out.println("[" + (i+1) + "] " + categories[i]);
				}
				
				System.out.print("\nEnter category number: ");
				String choice = input.nextLine();
				choiceInt = checkChoice(choice, 1, categories.length);
	
				if(choiceInt != -1) {
					break;
				}
	
			}

			// Create a new hangman game instance
			hangman = new Hangman(choiceInt);
			
			// Reset saved file so that any saved information will be removed.
			writeEmptySavedGame(); 
									
		}
		else {
			// Game is loaded from savedgameinfo.txt
			
			// Loaded game format (comma-separated info):
			// 		<level number>,<category number>,<word being guessed>,<revealed letters>,<inputted letters>
			
			// Read saved game info and parse it by separating each information in a string array with comma as delimiter
			String savedInfo = loadSavedGame();
			String[] loadData = savedInfo.split(",");
			
			
			// Setup starting game level based on saved game data
			int startLevel = Integer.parseInt(loadData[0]);
			
			// load the category selected on the previous saved game.
			choiceInt = Integer.parseInt(loadData[1]);
			
			StringBank guessString;
			// Check if user has inputted any guesses for the loaded game
			if(loadData.length > 4) {
				// User has inputted a letter to guess the word, code may access the loadData array index 4
				// Create an instance of the guess word string bank based on saved data.
				guessString = new StringBank('-', loadData[2], new StringBuilder(loadData[3]), new StringBuilder(loadData[4]));
				
			}
			else {
				// 5th parameter of saved data is empty. Do not use access loadData[4] since this will cause
				// an array out of bounds exception.
				// Create an instance of the guess word string bank with the fourth stringbuilder set to empty.
				guessString = new StringBank('-', loadData[2], new StringBuilder(loadData[3]), new StringBuilder());

			}

			// Initialize chances count based on non-matching letters from previous saved game.
			int retryChances = 10 - guessString.getNonMatchingWithdrawnChars(false).length();
			
			hangman = new Hangman(startLevel, retryChances, choiceInt, guessString);
		}
		
		
		do {						
			// Print hangman data
			printHangmanData(hangman.getGuessString(), hangman.getAlphaString(), hangman.getCurrentLevel(), choiceInt);

			System.out.println("\n[Enter next letter to guess] -or- [Type \"quit\" to quit app without saving game] -or- [Type \"save\" to save game and quit the app]: ");
			// Ask for additional guess input
			String guess = input.nextLine();
			// Check if guess is a command (i.e. "save" or "quit")
			int checkCode = checkGuessInput(guess);
			if(checkCode == INPUT_VALID_CHAR) {
				checkCode = hangman.iterateGame(guess.charAt(0));
				if(checkCode == 0) {
					// 0 means that an un-entered letter was inputted but did not occur in the guess word						
					System.out.println("\n*** The letter that you entered is not part of the word that you are guessing.\n"
							+ "    You have " + hangman.getGuessRetriesLeft() + " chances left. ***\n");
					delay(1000);
				}
				else if(checkCode == -1) {
					// character has already been previously inputted. inform user about this error..
					System.out.println("\n*** The character '" + guess.toUpperCase() + "' has already been used! \n"
							+ "Try the remaining letters in the Letter Bank. ***\n");
					delay(1000);
				}
			}
			else if(checkCode == INPUT_SAVE_AND_QUIT) {
				// Save current game progress to file and quit
				saveGameInProgress(hangman.getCurrentLevel(), choiceInt, hangman.getGuessString().getInputString(), 
						hangman.getGuessString().getRevealedCharsFromInput(), hangman.getGuessString().getAllWithdrawnLetters());
				System.out.println("\n*** Game progress was successfully saved. ***\n");
				break;
			}
			else if(checkCode == INPUT_QUIT_NO_SAVE) {
				// Quit game without saving progress
				writeEmptySavedGame();
				break;
			}
			else {
				// Display if input guess contains invalid data.
				System.out.println("\n*** Input is invalid. "
						+ "Enter any of the remaining letters in the letter bank"
						+ " or type \"save\" \nto save game progress and quit or "
						+ "type \"quit\" to quit the game without saving. ***");
				delay(1500);
			}
							
		} while(!hangman.isGameOver());
		
		// Check if we have completed all 5 levels
		if(hangman.getCurrentLevel() > 5) {
			// Print hangman data one last time before exiting.
			printHangmanData(hangman.getGuessString(), hangman.getAlphaString(), hangman.getCurrentLevel(), choiceInt);

			System.out.println("\n****************************************");
			System.out.println("  CONGRATULATIONS!!! You won the game.");
			System.out.println("              GAME OVER");
			System.out.println("****************************************");
			
			// Game is finished, clear any save game info.
			writeEmptySavedGame();

		}
		// Check if we have run out of chances
		else if(!hangman.chkGuessRetryAllowed()) {
			System.out.println("\n*** Your highest level for this game is: Level " + hangman.getCurrentLevel() + " ***");
			showHangmanGraphic(hangman.getGuessRetriesLeft());
			System.out.println("\n**********************");
			System.out.println("  Sorry, you lost...");
			System.out.println("      GAME OVER");
			System.out.println("**********************");

			// Game is finished, clear any save game info.
			writeEmptySavedGame();
		}
		
		// Indicate that app is exited
		System.out.println("\nApplication exited.");
		
		// Close input scanner
		input.close();		
	}
	
	// Common function to display status data for every input character that the user enters
	public static void printHangmanData(StringBank gBank, StringBank lBank, int level, int category) {
		System.out.println("\n****************** Game Status ******************");
		int chances = 10-gBank.getNonMatchingWithdrawnChars(false).length();
		System.out.println(" Current level        : " + ((level > 5) ? "Completed all levels" : level) + "\n Chances left to guess: " +
								chances);
		if(chances < 10) {
			showHangmanGraphic(chances);
		}
		System.out.println("*************************************************");
		System.out.println("\n********************** Letter Bank **********************");
		System.out.println(" Remaining letters: " + lBank.getRemainingCharsFromInput());
		System.out.println(" Used letters     : " + lBank.getRevealedCharsFromInput());
		System.out.println("*********************************************************");
		System.out.println("\n************** Word to Guess ****************");
		System.out.println(" Category: " + WordBank.getCategories()[category-1] + "\n Word    : " + gBank.getRevealedCharsFromInput());
		System.out.println("*********************************************");
	}
	
	// Returns chosen number if number false between the specified range of startingChoice to endingChoice.
	//		Otherwise, -1 is returned.
	public static int checkChoice(String choice, int startingChoice, int endingChoice) {
		int choiceInt = -1;
		
		try {
			choiceInt = Integer.parseInt(choice);
		}
		catch(NumberFormatException e) {
			//e.printStackTrace();
			//System.out.println("\n*** ERROR: Input is not a number! ***");
		}
		
		if(choiceInt >= startingChoice && choiceInt <= endingChoice) {
			return choiceInt;
		}
		else {
			System.out.println("\n*** ERROR: Invalid input. Try again. ***\n");
			delay(500);
			return -1;
		}		
	}
	
	// Returns 0 if input is an un-entered character but without any match.
	//		   a positive number if an un-discovered match has been found, the positive
	//				number also denotes how many times the character occurs within the word.
	//		    1  if input is a valid "save" and quit command.
	//          2  if input is a "quit" without saving command.
	//		   -1 if input is an invalid input
	public static int checkGuessInput(String input) {
		int retval = 0;
		
		// WARNING: DEBUG CODE!!! 
		//          Make sure to set DEBUG_ON to false before submitting project.
		if(DEBUG_ON) {
			System.out.println("\nDEBUG: Inputted string: " + input + "\n");
		}
		if(input.equalsIgnoreCase("quit")) {
			retval = INPUT_QUIT_NO_SAVE;
		}
		else if(input.equalsIgnoreCase("save")) {
			retval = INPUT_SAVE_AND_QUIT;
		}
		else if(input.length() == 1) {
			char c = input.toUpperCase().charAt(0);
			if(c >= 'A' && c <= 'Z') {
				// user entered a valid letter add this to letter bank.
				retval = INPUT_VALID_CHAR;
			}
			else {
				retval = INPUT_INVALID_CHAR;
			}
		}
		else {
			// Indicate an invalid input.
			retval = INPUT_INVALID_CHAR;
		}
		
		// WARNING: DEBUG CODE!!! 
		//          Make sure to set DEBUG_ON to false before submitting project.
		if(DEBUG_ON) {
			System.out.println("\nDEBUG: Retval = " + retval + "\n");
		}
		
		return retval;
	}
	
	public static void printRules() throws FileNotFoundException {
		File file = new File("./rules.txt");
		Scanner filescanner = new Scanner(file);
		
		while(filescanner.hasNextLine()) {
			System.out.println(filescanner.nextLine());
		}
		
		filescanner.close();
	}
	
	public static void writeEmptySavedGame() throws FileNotFoundException {
		PrintWriter file = new PrintWriter("savedgameinfo.txt");
		file.println("empty");
		file.close();
	}
	
	// Saved game format (comma-separated info):
	// 		<level number>,<category number>,<word being guessed>,<revealed letters>,<inputted letters>
	public static void saveGameInProgress(int level, int category, String fullInputStr, 
			String revealedStr, String userInputtedStr) throws FileNotFoundException {
		PrintWriter file = new PrintWriter("savedgameinfo.txt");
		file.println(level + "," + category + "," + fullInputStr + "," +
				revealedStr + "," + userInputtedStr);
		file.close();
	}
	
	// Returns a string containing saved game info. If no games are saved, "empty" will be returned.
	public static String loadSavedGame() throws FileNotFoundException {
		File file = new File("savedgameinfo.txt");
		Scanner filescanner = new Scanner(file);
		
		String saveInfo = filescanner.nextLine();
		filescanner.close();
		
		return saveInfo;
	}
	
	// Pause execution for the amount specified in parameter (in millis)
	public static void delay(int ms){
        try
        {
            Thread.sleep(ms);
        }
        catch(InterruptedException ex)
        {
        	// Just display exception stack trace info if an exception occurs
            ex.printStackTrace();
        }
    }
	
	public static void showHangmanGraphic(int stage) {
		//HangManTextArt  = new HangManTextArt();
		String[] artFrame = HangManTextArt.getGraphicseq();
		System.out.println("\n" + artFrame[stage] + "\n");
	}
	

}
