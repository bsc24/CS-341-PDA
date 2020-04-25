import java.util.LinkedList;
import java.util.Scanner;

/* 
 * Author: Brandon S. Chin
 * Some components of this project were taken from or inspired by a previous project of mine from CS 280
 * 
 */


public class Main {

	public static void main(String[] args) {

		Scanner input = new Scanner(System.in);
		
		while (true) {
			System.out.print("Enter a string to compute or ~ to end: ");
			String computeString = input.nextLine();
			
			if (computeString.equals("~"))
				System.exit(0);
			
			/*
			LinkedList<Token> pdaParse = ParseTree.createParseTree(computeString);
			while (!pdaParse.isEmpty()) {
				Token holder = pdaParse.remove();
				System.out.print("|" + holder);
			}
			*/
			
			
			ParseTree pdaParse = ParseTree.createParseTree(computeString);
			String startingParts = "";
			String lastString = "";
			LinkedList<ParseTree> parseStack = new LinkedList<ParseTree>();
			parseStack.push(pdaParse);
			
			while (!parseStack.isEmpty()) {
				boolean error = false;
				String printingString = startingParts;
				for (ParseTree value: parseStack) {
					printingString += value.getToken();
					if (value.getToken().getTokenType() == TokenType.ERR) {
						error = true;
						break;
					}
				}
				
				if (printingString.charAt(printingString.length() - 1) != '$' && printingString.length() != 1 && !error)
					printingString += "$";
				
				ParseTree nextTree = parseStack.pop();
				ParseTree[] children = nextTree.getChildren();
				
				for (int i = 4; i >= 0; i--) {
					if (children[i] != null)
						parseStack.push(children[i]);
				}
				
				startingParts += nextTree.getToken().getLexeme();
				
				if (!printingString.equals(lastString)) {
					System.out.println(printingString);
				}
				
				if (error) {
					break;
				}
				
				lastString = printingString;
			}
			
			
			System.out.println("DONE");
		}
		
	}

}
