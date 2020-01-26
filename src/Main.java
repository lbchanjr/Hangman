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

	public static final String[][] words= {{"AFGHANISTAN", "CANADA", "PHILIPPINES", "UNITED STATES OF AMERICA", "SWITZERLAND", "KUWAIT", "AUSTRALIA", "CHINA", "GREAT BRITAIN", "ARGENTINA"}, 
			{"AARDVARK", "ELEPHANT", "CROCODILE", "LEOPARD", "PIRANHA", "PLATYPUS", "CHEETAH", "RHINOCEROS", "GIRAFFE", "CHICKEN"},
			{"FERRARI", "SUBARU", "LAMBORGHINI", "MERCEDES BENZ", "VOLKSWAGEN", "ALFA ROMEO", "HONDA", "TESLA", "MITSUBISHI", "TOYOTA"}
	};
	
	public static final String[] categories = {"Countries", "Animals", "Car Companies"};
	public static final int GAME_TYPE_NEW = 1;
	public static final int GAME_TYPE_LOADED = 3;
	public static final int GAME_TYPE_ONGOING = 2;
	
	public static final int INPUT_VALID_CHAR = 0;
	public static final int INPUT_SAVE_AND_QUIT = 1;
	public static final int INPUT_QUIT_NO_SAVE = 2;
	public static final int INPUT_INVALID_CHAR = -1;
	
	// Set to true to enable debug messages
	public static final boolean DEBUG_ON = false;
	
	public static void main(String[] args) throws FileNotFoundException {
		Scanner input = new Scanner(System.in);

		int choiceInt = 0;

		// WARNING: DEBUG CODE!!! 
		//          Make sure to set DEBUG_ON to false before submitting project.
		if(DEBUG_ON) {
			System.out.println(Arrays.toString(words[0]));
			System.out.println(Arrays.toString(words[1]));
			System.out.println(Arrays.toString(words[2]));
		}
		
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
		
		// Create a random object to be used later for guess word indexing.
		Random rand = new Random();			

		int startLevel = 1;
		int gameType = choiceInt;
		StringBank guessString;
		StringBank alphaString = new StringBank(' ', "ABCDEFGHIJKLMNOPQRSTUVWXYZ"); 
		int retryChances = 10;
		// This array list will hold all the random numbers that were generated
		// The purpose of this array is to insure that no two random numbers of
		// the same value will be used during the guessing game.
		ArrayList<Integer> randomNums = new ArrayList<Integer>();
		do {			
			// Check if a new game was started.
			if(gameType == GAME_TYPE_NEW || gameType == GAME_TYPE_ONGOING) {	
				
				
				
				// Check if a new game has just started.
				if(gameType == GAME_TYPE_NEW) {
					// New game was started... Allow user to choose categories.
					while(true) {
						System.out.println("\n*************************"
								+          "\n   Select a category: "
								+          "\n*************************");
						System.out.println("[1] " + categories[0]);
						System.out.println("[2] " + categories[1]);
						System.out.println("[3] " + categories[2]);
						
						System.out.print("\nEnter choice [1, 2, or 3]: ");
						String choice = input.nextLine();
						choiceInt = checkChoice(choice, 1, 3);
			
						if(choiceInt != -1) {
							break;
						}
			
					}
					
					// Category has been selected, switch game type to "ongoing"
					gameType = GAME_TYPE_ONGOING;
				}
				
				// WARNING: DEBUG CODE!!! 
				//          Make sure to set DEBUG_ON to false before submitting project.
				if(DEBUG_ON) {
					System.out.println("DEBUG: Selection is " + choiceInt);
					System.out.println("Creating empty save file...");
				}
				
				// Reset saved file so that any saved information will be removed.
				writeEmptySavedGame(); 
				
				// Reinitialize retry count back to 10
				retryChances = 10;

				// Select a random number from 0 to size of word bank category array
				// to use as array index for the word to guess
				int randomWordIndex;
				do {
					// Keep generating a random number until a unique value is generated.
					randomWordIndex = rand.nextInt(words[choiceInt-1].length);
				} while(randomNums.contains(Integer.valueOf(randomWordIndex)));
				
				// save the unique random number to the random number array
				randomNums.add(Integer.valueOf(randomWordIndex));
				
			    
				// WARNING: DEBUG CODE!!! 
				//          Make sure to set DEBUG_ON to false before submitting project.
				if(DEBUG_ON) {
					//Thread.sleep(300);
					System.out.println("Size of array group " + (choiceInt-1) + " is " + words[choiceInt-1].length);
					System.out.println("Random word generated is: " + words[choiceInt-1][randomWordIndex]);
				}
				
				// Create a guess string word bank based on the random word selected from the category array.
				guessString = new StringBank('-', words[choiceInt-1][randomWordIndex]);
				
				// Reset letter bank
				alphaString.resetStringBank();
				
				// WARNING: DEBUG CODE!!! 
				//          Make sure to set DEBUG_ON to false before submitting project.
				if(DEBUG_ON) {
					System.out.println("Random word selected is: " + guessString.toString());
				}
										
			}
			else {
				// Game is loaded from savedgameinfo.txt
				
				// Loaded game format (comma-separated info):
				// 		<level number>,<category number>,<word being guessed>,<revealed letters>,<inputted letters>
				
				// Read saved game info and parse it by separating each information in a string array with comma as delimiter
				String savedInfo = loadSavedGame();
				String[] loadData = savedInfo.split(",");
				
				// WARNING: DEBUG CODE!!! 
				//          Make sure to set DEBUG_ON to false before submitting project.
				if(DEBUG_ON) {
					System.out.println("Parsed info: " + Arrays.toString(loadData));
				}
				
				// Setup starting game level based on saved game data
				startLevel = Integer.parseInt(loadData[0]);
				
				// load the category selected on the previous saved game.
				choiceInt = Integer.parseInt(loadData[1]);
				
				// Check if user has inputted any guesses for the loaded game
				if(loadData.length > 4) {
					// User has inputted a letter to guess the word, code may access the loadData array index 4
					// Create an instance of the guess word string bank based on saved data.
					guessString = new StringBank('-', loadData[2], new StringBuilder(loadData[3]), new StringBuilder(loadData[4]));
					
					// Update alphabet lookup string bank based on inputted letters.
					char[] usedChars = loadData[4].toCharArray();
					for ( char c : usedChars) {
						alphaString.withdrawChar(c);
					}
				}
				else {
					// 5th parameter of saved data is empty. Do not use access loadData[4] since this will cause
					// an array out of bounds exception.
					// Create an instance of the guess word string bank with the fourth stringbuilder set to empty.
					guessString = new StringBank('-', loadData[2], new StringBuilder(loadData[3]), new StringBuilder());

					// Note: there's no need to update the alphabet string bank's input history since loadData[4] is empty.
				}
				// Initialize chances count based on non-matching letters from previous saved game.
				retryChances = 10 - guessString.getNonMatchingWithdrawnChars(false).length();
				
				// Indicate normal so that we can start anew on the next game level.
				gameType = GAME_TYPE_ONGOING;
			}
			

			
			while (guessString.getRevealedCharsFromInput().indexOf('-') != -1 &&
						retryChances > 0) {
				// Print hangman data
				printHangmanData(guessString, alphaString, startLevel, choiceInt);

				System.out.println("\n[Enter next letter to guess] -or- [Type \"quit\" to quit app without saving game] -or- [Type \"save\" to save game and quit the app]: ");
				// Ask for additional guess input
				String guess = input.nextLine();
				// Check if guess is a command (i.e. "save" or "quit")
				int checkCode = checkGuessInput(guess);
				if(checkCode == INPUT_VALID_CHAR) {
					checkCode =	guessString.withdrawChar(guess.charAt(0));
					if(checkCode == 0) {
						// 0 means that an unentered letter was inputted but did not occur in the guess word						
						retryChances--;
						System.out.println("\n*** The letter that you entered is not part of the word that you are guessing.\n"
								+ "    You have " + retryChances + " chances left. ***\n");
						alphaString.withdrawChar(guess.charAt(0));
						delay(1000);
					}
					else if(checkCode > 0) {
						// a positive number means that an unentered letter exists within the word
						alphaString.withdrawChar(guess.charAt(0));
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
					saveGameInProgress(startLevel, choiceInt, guessString.getInputString(), 
							guessString.getRevealedCharsFromInput(), guessString.getAllWithdrawnLetters());
					retryChances = -1;  // dummy negative value to distinguish hasty exit from program
					System.out.println("\n*** Game progress was successfully saved. ***\n");
				}
				else if(checkCode == INPUT_QUIT_NO_SAVE) {
					// Quit game without saving progress
					writeEmptySavedGame();
					retryChances = -2;	// dummy negative value to distinguish hasty exit from program
				}
				else {
					// Display if input guess contains invalid data.
					System.out.println("\n*** Input is invalid. "
							+ "Enter any of the remaining letters in the letter bank"
							+ " or type \"save\" \nto save game progress and quit or "
							+ "type \"quit\" to quit the game without saving. ***");
					delay(1500);
				}
				
			}
			
			// WARNING: DEBUG CODE!!! 
			//          Make sure to set DEBUG_ON to false before submitting project.			
			if(DEBUG_ON) {
				System.out.println("guess word info: " + guessString.toString());
				System.out.println("reference alpha info: " + alphaString.toString());
				System.out.println("category selected: " + categories[choiceInt-1]);
				System.out.println("current level: " + startLevel);
				System.out.println("number of chances left: " + retryChances);
			}
			
			// Check if all of the characters have been guessed correctly
			if(guessString.getRevealedCharsFromInput().indexOf("-") == -1) {
				// Refresh status data before levelling up.
				if(startLevel < 5) {
					printHangmanData(guessString, alphaString, startLevel, choiceInt);
				}
				// Elevate to next level and guess another random word.
				startLevel++;
			}
			
		} while(startLevel <= 5 && retryChances > 0);
		
		// Check if we have completed all 5 levels
		if(startLevel > 5) {
			// Print hangman data one last time before exiting.
			printHangmanData(guessString, alphaString, startLevel, choiceInt);

			System.out.println("\n****************************************");
			System.out.println("  CONGRATULATIONS!!! You won the game.");
			System.out.println("              GAME OVER");
			System.out.println("****************************************");
			
			// Game is finished, clear any save game info.
			writeEmptySavedGame();

		}
		// Check if we have run out of chances
		else if(retryChances == 0) {
			System.out.println("\n*** Your highest level for this game is: Level " + startLevel + " ***");
			showHangmanGraphic(retryChances);
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
		System.out.println(" Category: " + categories[category-1] + "\n Word    : " + gBank.getRevealedCharsFromInput());
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
