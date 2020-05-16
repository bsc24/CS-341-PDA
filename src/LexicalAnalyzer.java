



public class LexicalAnalyzer {
	public static Token getNextToken(String input, int inputIndex) {
		
		
	    //String word = "";
	    //String rawInput;
	    TokenType type = TokenType.ERR;
	    //boolean firstChar = true;
	    //boolean inVar = false;
	    //boolean inInt = false;
	    //boolean inString = false;
	    //boolean inComment = false;
	    //boolean finished = false;
		String word = Character.toString(input.charAt(inputIndex));
		if (inputIndex == 0) {
        	if (!word.equals("$")) {
        		word = "Start of line was not $";
        		type = TokenType.ERR;
        	}
        	else {
        		type = TokenType.BEGIN;
        	}
        }
        else if(inputIndex == input.length() - 1) {
        	if (!word.equals("$")) {
        		word = "EOL reached without $";
        		type = TokenType.ERR;
        	}
        	else {
        		type = TokenType.END;
        	}
        }
        else if (word.equals("$")) {
        	//word = "$ occurred in the middle of the input";
        	type = TokenType.END;	//type = TokenType.ERR;
        }
        else if (word.equals("="))
        {
        	type = TokenType.EQUAL;
        }
        else if (word.equals("+") || word.equals("-") || word.equals("*") || word.equals("/"))    //OP
        {
            type = TokenType.OP;
        }
        else if (word.equals("("))    //LPAREN
        {
            type = TokenType.LPAREN;
        }
        else if (word.equals(")"))    //RPAREN
        {
            type = TokenType.RPAREN;
        }
        else if (word.equals(";"))    //SC
        {
            type = TokenType.SC;
        }
        else if (Character.isAlphabetic(word.charAt(0)))    //ID
        {
            type = TokenType.VAR;
        }
        else if (Character.isDigit(word.charAt(0)))    //ICONST
        {
            type = TokenType.ICONST;
        }
        else if (word.equals(" "))
        {
            type = TokenType.SPACE;
        }
        else
        {
            type = TokenType.ERR;
        }
		
        return new Token(type, word);
		
		
	}
}
