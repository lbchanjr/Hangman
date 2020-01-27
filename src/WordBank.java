// Student name: Louise Chan
// Student ID: 101296435
public abstract class WordBank {
	private static final String[][] words= {{"AFGHANISTAN", "CANADA", "PHILIPPINES", "UNITED STATES OF AMERICA", "SWITZERLAND", "KUWAIT", "AUSTRALIA", "CHINA", "GREAT BRITAIN", "ARGENTINA"}, 
			{"AARDVARK", "ELEPHANT", "CROCODILE", "LEOPARD", "PIRANHA", "PLATYPUS", "CHEETAH", "RHINOCEROS", "GIRAFFE", "CHICKEN"},
			{"FERRARI", "SUBARU", "LAMBORGHINI", "MERCEDES BENZ", "VOLKSWAGEN", "ALFA ROMEO", "HONDA", "TESLA", "MITSUBISHI", "TOYOTA"}
	};
	
	private static final String[] categories = {"Countries", "Animals", "Car Companies"};

	public static String[][] getWords() {
		return words;
	}

	public static String[] getCategories() {
		return categories;
	}
	
	public static int getNumberOfCategories() {
		return categories.length;
	}

	
}
