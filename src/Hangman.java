// Student name: Louise Chan
// Student ID: 101296435
import java.util.ArrayList;
import java.util.Random;

public class Hangman extends WordBank {
	public static final int DEFAULT_LEVELS = 5;
	public static final int DEFAULT_START_LEVEL = 1;
	public static final int DEFAULT_RETRIES = 10;
	
	private int startLevel;
	private int numLevels;
	private int retryChances;
	private int category;
	private int retryCount;
	private StringBank guessString;
	private StringBank alphaString = new StringBank(' ', "ABCDEFGHIJKLMNOPQRSTUVWXYZ");
	
	// Create a random object to be used later for guess word indexing.
	private Random rand = new Random();	
	// This array list will hold all the random numbers that were generated
	// The purpose of this array is to insure that no two random numbers of
	// the same value will be used during the guessing game.
	ArrayList<Integer> randomNums = new ArrayList<Integer>();


	public Hangman(int category) {
		startLevel = DEFAULT_START_LEVEL;
		numLevels = DEFAULT_LEVELS;
		retryChances = DEFAULT_RETRIES;
		this.category = category;
		retryCount = DEFAULT_RETRIES;
		
		// Create a guess string word bank based on the random word selected from the category array.
		guessString = new StringBank('-', generateRandomWord());

	}
	
	public Hangman(int startLevel, int retryChances, int category, StringBank guessWord) {
		this.startLevel = startLevel;
		this.category = category;
		this.retryChances = retryChances;
		this.guessString = guessWord;
		retryCount = retryChances;
		numLevels = DEFAULT_LEVELS;
		
		// Setup the letter bank so that letters that were used
		// in guess word bank is also taken out of the letter bank
		withdrawCharsFromLetterBank();
	}
	
	public Hangman(int startLevel, int numLevels, int retryChances, int category, StringBank guessWord) {
		this.startLevel = startLevel;
		this.numLevels = numLevels;
		this.retryChances = retryChances;
		this.category = category;
		retryCount = retryChances;

		// Setup the letter bank so that letters that were used
		// in guess word bank is also taken out of the letter bank
		withdrawCharsFromLetterBank();
	}

	// Returns 0 if new character inputted did not match any letters
	// 		        in the word that is being guessed.
	//		   > 0 (up to 999) character inputted is part of the word being guessed
	//	    		the number returned informs how many times the letter
	//				appeared in the word being guessed.
	//         -1 if character has previously been inputted.
	// 		   1000 change in level (i.e. guess word for current level has been solved)
	public int iterateGame(char ch) {
		int checkCode =	guessString.withdrawChar(ch);
		if(checkCode == 0) {
			// 0 means that an un-entered letter was inputted but did not occur in the guess word						
			retryChances--;
			alphaString.withdrawChar(ch);
		}
		else if(checkCode > 0) {
			// a positive number means that an un-entered letter exists within the word
			alphaString.withdrawChar(ch);

			// Check if all of the characters have been guessed.
			if(guessString.getRevealedCharsFromInput().indexOf('-') == -1) {
				// All of the letters have been revealed.
				// Tell caller about this by returning this code
				checkCode = 1000;
				
				// Reset retry counter
				retryChances = retryCount;

				// Elevate to next level and guess another random word.
				startLevel++; 

				// Are we done with all levels
				if(startLevel <= numLevels) {
					// Still have levels to complete.
					// Setup new word to guess.
					guessString = new StringBank('-', generateRandomWord());	

					// Reset letter bank
					alphaString.resetStringBank();
				}
			}
 
		}
		
		
		
		return checkCode;
	}
	
	public boolean chkGuessRetryAllowed() {
		if(retryChances > 0) {
			return true;
		}
		else {
			return false;
		}
	}
	
	public int getGuessRetriesLeft() {
		return retryChances;
	}
	 
	public StringBank getGuessString() {
		return guessString;
	}

	public void setGuessString(StringBank guessString) {
		this.guessString = guessString;
	}

	public StringBank getAlphaString() {
		return alphaString;
	}

	public void setAlphaString(StringBank alphaString) {
		this.alphaString = alphaString;
	} 
	
	public void resetGuessRetryCount() {
		retryChances = retryCount;
	}
	
	public boolean isGameOver() {
		if(startLevel > numLevels || retryChances <= 0) {
			return true;
		}
		else {
			return false;
		}
			
	}
	
	public int getCurrentLevel() {
		return startLevel;
	}
	
	private void withdrawCharsFromLetterBank() {
		String usedString = guessString.getAllWithdrawnLetters();
		// Update alphabet lookup string bank based on inputted letters.
		char[] usedChars = usedString.toCharArray();
		for ( char c : usedChars ) {
			alphaString.withdrawChar(c);
		}
		
	}
	
	private String generateRandomWord() {
		// Select a random number from 0 to size of word bank category array
		// to use as array index for the word to guess
		int randomWordIndex;
		String[][] wordBank = getWords();
		
		if(numLevels <= wordBank[category-1].length) {
			// Make sure that random number does not repeat.
			do {
				// Keep generating a random number until a unique value is generated.
				// The random number bounds used is based on the number of words available
				// for the selected category.
				randomWordIndex = rand.nextInt(wordBank[category-1].length);
			} while(randomNums.contains(Integer.valueOf(randomWordIndex)));
			
			// save the unique random number to the random number array
			randomNums.add(Integer.valueOf(randomWordIndex));
		}
		else {
			// No random number checking if number of levels for game
			// is more than the available words in the selected category.
			randomWordIndex = rand.nextInt(wordBank[category-1].length);
		}
		
		// Return a random word from the selected category
		return wordBank[category-1][randomWordIndex];
	}
	
}
