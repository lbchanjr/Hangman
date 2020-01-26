// Student name: Louise Chan
// Student ID: 101296435

import java.util.Arrays;

public class StringBank {
	// Set to true to enable debug messages
	private final static boolean DEBUG_ON = false; 
	private String strInput;
	private StringBuilder strOutput;
	private StringBuilder charWithdrawalHistory;
	private char placeHolder = '-';
	
	// Note: This constructor is used when a new game is started.
	public StringBank(char placeHolder, String strInput) {
		char[] strOutVal = new char[strInput.length()];
		Arrays.fill(strOutVal, placeHolder);
						
		this.strInput = strInput;
		this.placeHolder = placeHolder;
		strOutput = new StringBuilder(new String(strOutVal));	
		
		int startIndex = 0;
		while((startIndex = strInput.indexOf(' ', startIndex)) != -1 && startIndex < strInput.length()) {
			strOutput.deleteCharAt(startIndex);
			strOutput.insert(startIndex, ' ');
			startIndex++;			
		}
		
		charWithdrawalHistory = new StringBuilder();
	}
	
	// Note: The following constructor will be used when the game is loaded from a saved file.
	public StringBank(char placeHolder, String strInput, StringBuilder revealedStr, StringBuilder withdrawnStr) {
		char[] strOutVal = new char[strInput.length()];
		Arrays.fill(strOutVal, placeHolder);
						
		this.strInput = strInput;
		this.placeHolder = placeHolder;
		
		strOutput = revealedStr;			
		charWithdrawalHistory = withdrawnStr;
	}
	
	// Returns: 0 if character is not found
	//			-1 if character has already been withdrawn before
	//			Otherwise, the number of character "c"'s that were withdrawn 
	//             from the string input
	public int withdrawChar(char c) {
		c = Character.toUpperCase(c);
		// Check if character has been previously withdrawn.
		if(charWithdrawalHistory.indexOf(Character.toString(c)) != -1) 
		{
			// Character has been previously withdrawn.
			return -1;
		}
		else {
			// Character has not yet been withdrawn from bank.
			// Add to withdrawal history.
			charWithdrawalHistory.append(c);
			
			// Check if character exists within the string input.
			if(strInput.contains(Character.toString(c))) {
				
				int startIndex = 0;
				int foundCharCount = 0;
				
				while((startIndex = strInput.indexOf(c, startIndex)) != -1 && startIndex < strInput.length()) {	
					// WARNING: DEBUG CODE!!! 
					//          Make sure to set DEBUG_ON to false before submitting project.
					if(DEBUG_ON) {
						System.out.println("DEBUG: current withdraw index: " + startIndex);
					}
					
					// Update indices of string output with the specified character 
					// using the string input as reference.
					strOutput.deleteCharAt(startIndex);	
					strOutput.insert(startIndex, c);
		
					// Point to the next index in the input string
					startIndex++;
					// Increment number of characters that match character c
					foundCharCount++;
				} 
				
				return foundCharCount;
			}
			else {
				return 0;
			}
		}
		
	}
	
	// Returns the characters from the string bank input that were withdrawn
	public String getRevealedCharsFromInput() {

		return strOutput.toString();
	
	}
	
	// Returns the characters that were not withdrawn from the string bank input
	public String getRemainingCharsFromInput() {
		
		StringBuilder outString = new StringBuilder();
		
		for(int i = 0; i < strOutput.length(); i++) {
			if(strOutput.charAt(i) == placeHolder) {
				// Display characters that were not yet revealed.
				outString.append(strInput.charAt(i));
			}
			else {
				if(strOutput.charAt(i) != ' ') {
					// Replace non-space chars. with placeholders
					outString.append(placeHolder);
				}
				else {
					outString.append(' ');
				}
			}
		}
		
		return outString.toString();
		
	}	
	
	// Returns the number of inputted characters for this String bank instance
	public int getNumberOfWithdrawnChars() {
		return charWithdrawalHistory.length();
	}
	
	// Parameter: sortOutput set to 'true' if output needs to be sorted alphabetically.
	//						 set to 'false' if output is returned according to order of 
	//							character withdrawal.
	// Returns the withdrawn characters that did not match any of the characters in the
	// 		input string
	public String getNonMatchingWithdrawnChars(boolean sortOutput) {
		String revealedStr = getRevealedCharsFromInput();
		StringBuilder nonMatchString = new StringBuilder();
		
		// Find the inputted characters that did not match any of the revealed characters
		// in the word the is being guessed
		for (char c : charWithdrawalHistory.toString().toCharArray()) {
			if(!revealedStr.contains(Character.toString(c))) {
				nonMatchString.append(c);
			}
		}
		
		// Check if result needs to be sorted.
		if(sortOutput == false) {
			// No need for sorting
			return nonMatchString.toString();			
		}
		else {
			// Sort the result in alphabetical order before returning it to the caller
			char[] outArray = nonMatchString.toString().toCharArray();
			Arrays.parallelSort(outArray);
			return new String(outArray);
		}
		
	}
	
	// Resets the fields of the object to its initial state.
	public void resetStringBank() {
		char[] strOutVal = new char[strInput.length()];
		Arrays.fill(strOutVal, placeHolder);
						
		strOutput.replace(0, strInput.length(), new String(strOutVal));	
		
		int startIndex = 0;
		while((startIndex = strInput.indexOf(' ', startIndex)) != -1 && startIndex < strInput.length()) {
			strOutput.deleteCharAt(startIndex);
			strOutput.insert(startIndex, ' ');
			startIndex++;			
		}
		
		// clear withdrawal history
		charWithdrawalHistory = new StringBuilder();
	}
	
	// Returns the original string that needs to be guessed
	public String getInputString() {
		return this.strInput;
	}
	
	// Returns all the letters that were inputted by the user to guess the word
	public String getAllWithdrawnLetters() {
		return this.charWithdrawalHistory.toString();
	}
	
	// Returns the string bank's current string statuses.
	@Override
	public String toString() {
		return "[Initial=\""+this.strInput+"\", Remaining=\"" + getRemainingCharsFromInput() + "\", Revealed=\"" + getRevealedCharsFromInput() + "\", Withdrawn=\"" + this.charWithdrawalHistory + "\"]";	
	}

}
