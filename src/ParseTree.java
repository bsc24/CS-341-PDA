import java.util.LinkedList;
import java.util.Stack;

public class ParseTree {
	
	//private static int stringIndex;
	private Token tt;
	private ParseTree[] children;		// One case has five children
	//public static LinkedList<Token> createParseTree(String inputString) {
	public static ParseTree createParseTree(String inputString) {
		Stack<TokenType> pdaStack = new Stack<TokenType>();
		LinkedList<Token> listOfTokens = new LinkedList<Token>();
		int stringIndex = 0;
		
		
		if (inputString.length() == 0) {
			return new ParseTree(TokenType.ERR);
		}
		else if (inputString.length() == 1 && inputString.equals("$")) {
			return new ParseTree(new Token(TokenType.START), new ParseTree(new Token(TokenType.ERR, "No characters after initial $")));
		}
		
		
		while (stringIndex < inputString.length()) {
			Token nextToken = LexicalAnalyzer.getNextToken(inputString, stringIndex++);
			TokenType poppedSymbol = null;
			if (!pdaStack.isEmpty()) {
				poppedSymbol = pdaStack.pop();
			}
			

			boolean hadSymbol; 		// Used in SC and END
			switch(nextToken.getTokenType()) {
			case ICONST:
				if (poppedSymbol == TokenType.BEGIN || poppedSymbol == TokenType.LPAREN || poppedSymbol == TokenType.EQUAL) {
					pdaStack.push(poppedSymbol);
				}
				else if (poppedSymbol == TokenType.SC) {
					// Do nothing
				}
				else if (poppedSymbol != TokenType.OP && poppedSymbol != TokenType.ICONST) {
					nextToken = new Token(TokenType.ERR, nextToken.getLexeme());
				}
				break;
				
			case VAR:
				if (poppedSymbol == TokenType.BEGIN || poppedSymbol == TokenType.LPAREN || poppedSymbol == TokenType.EQUAL) {
					pdaStack.push(poppedSymbol);
				}
				else if (poppedSymbol == TokenType.SC) {
					// Do nothing
				}
				else if (poppedSymbol != TokenType.OP && poppedSymbol != TokenType.VAR) {
					nextToken = new Token(TokenType.ERR, nextToken.getLexeme());
				}
				break;
				
			case EQUAL:
				if (poppedSymbol != TokenType.VAR) {
					nextToken = new Token(TokenType.ERR, nextToken.getLexeme());
				}
				
			case OP:
				if (poppedSymbol != TokenType.VAR && poppedSymbol != TokenType.ICONST && poppedSymbol != TokenType.RPAREN) {
					nextToken = new Token(TokenType.ERR, nextToken.getLexeme());
				}
				break;
				
			case LPAREN:
				if (poppedSymbol == TokenType.BEGIN || poppedSymbol == TokenType.EQUAL || poppedSymbol == TokenType.LPAREN) {
					pdaStack.push(poppedSymbol);
				}
				else if (poppedSymbol == TokenType.SC) {
					// Do nothing
				}
				else if (poppedSymbol != TokenType.OP) {
					nextToken = new Token(TokenType.ERR, nextToken.getLexeme());
				}
				break;
				
			case RPAREN:
				if (poppedSymbol == TokenType.VAR || poppedSymbol == TokenType.ICONST || poppedSymbol == TokenType.RPAREN) {
					while (poppedSymbol == TokenType.VAR || poppedSymbol == TokenType.ICONST || poppedSymbol == TokenType.RPAREN) {
						poppedSymbol = pdaStack.pop();
					}
					
					if (poppedSymbol != TokenType.LPAREN) {
						nextToken = new Token(TokenType.ERR, ") occurred without an initial (");
					}
				}
				else {
					nextToken = new Token(TokenType.ERR, "Invalid symbol occurred before )");
				}
				break;
				
			case SC:
				hadSymbol = false;
				while (poppedSymbol == TokenType.VAR || poppedSymbol == TokenType.ICONST || poppedSymbol == TokenType.RPAREN) {
					poppedSymbol = pdaStack.pop();
					hadSymbol = true;
				}
				
				if (poppedSymbol != TokenType.EQUAL || !hadSymbol) {
					nextToken = new Token(TokenType.ERR, nextToken.getLexeme());
				}
				break;
				
			case SPACE:
				pdaStack.push(poppedSymbol);
				continue;
				
			case BEGIN:
				// Do nothing
				break;
				
			case END:
				hadSymbol = false;
				while (poppedSymbol == TokenType.VAR || poppedSymbol == TokenType.ICONST || poppedSymbol == TokenType.RPAREN) {
					poppedSymbol = pdaStack.pop();
					hadSymbol = true;
				}
				
				
				if (poppedSymbol != TokenType.BEGIN || !hadSymbol) {
					if (poppedSymbol == TokenType.LPAREN) {
						nextToken = new Token(TokenType.ERR, "End of input reached without a closing parenthesis");
					}
					else {
						nextToken = new Token(TokenType.ERR, nextToken.getLexeme());
					}
				}
				break;
				
			case ERR:
				System.out.println("Error occurred");
				break;
				
			default:
				System.out.println("Unaccounted for TokenType occurred.");
				break;
			}
			
			
			pdaStack.push(nextToken.getTokenType());
			
			listOfTokens.add(nextToken);
			
			if (nextToken.getTokenType() == TokenType.ERR) {
				break;
			}
			
		}	// End of while
		
		//return listOfTokens;
		return Prog(listOfTokens);
	}
	
	private static ParseTree Prog(LinkedList<Token> tokenList) {
		ParseTree prog = new ParseTree(TokenType.START);
		if (!tokenList.isEmpty())
			prog.children[0] = Dollar(tokenList);
		
		if (!tokenList.isEmpty())
			prog.children[1] = Base(tokenList);
		
		if (!tokenList.isEmpty())
			prog.children[2] = Dollar(tokenList);
		
		return prog;
	}
	
	private static ParseTree Dollar(LinkedList<Token> tokenList) {
		return new ParseTree(tokenList.remove());
	}
	
	private static ParseTree Base(LinkedList<Token> tokenList) {
		ParseTree base = new ParseTree(TokenType.BASE);
		if (tokenList.peek().getTokenType() == TokenType.VAR) {
			ParseTree nextTree = Variable(tokenList);
			if (tokenList.isEmpty()) {
				base.children[0] = nextTree;
				return base;
			}
			else if (tokenList.peek().getTokenType() == TokenType.EQUAL) {
				base.children[0] = nextTree;
				base.children[1] = new ParseTree(tokenList.remove());	// This will be an equal
				if (!tokenList.isEmpty())
					base.children[2] = Expression(tokenList);
				
				if (!tokenList.isEmpty())
					base.children[3] = new ParseTree(tokenList.remove());	// This *should* be a semicolon
				
				if (!tokenList.isEmpty())
					base.children[4] = Base(tokenList);
			}
			else {
				base.children[0] = new ParseTree(TokenType.EXPR);
				base.children[0].children[0] = new ParseTree(TokenType.TERM);
				base.children[0].children[0].children[0] = nextTree;
				
				switch (tokenList.peek().getTokenType()) {
				case OP:
					base.children[0].children[1] = new ParseTree(tokenList.remove());
					base.children[0].children[2] = Expression(tokenList);
					break;
					
				case END:
					break;
					
				default:
					System.out.println("Unaccounted for TokenType in ParseTree.Base(): " + tokenList.peek());
					System.exit(0);
				}
			}
		}
		else if (tokenList.peek().getTokenType() == TokenType.ERR) {
			base.tt = tokenList.pop();
		}
		else {
			base.children[0] = Expression(tokenList);
		}
		
		return base;
	}
	
	private static ParseTree Expression(LinkedList<Token> tokenList) {
		ParseTree expression = new ParseTree(TokenType.EXPR);
		if (tokenList.peek().getTokenType() == TokenType.ERR) {
			expression.children[0] = Error(tokenList);
			return expression;
		}
		expression.children[0] = Term(tokenList);
		if (!tokenList.isEmpty() && tokenList.peek().getTokenType() == TokenType.OP) {
			expression.children[1] = new ParseTree(tokenList.remove());
			expression.children[2] = Expression(tokenList);
		}
		/*
		else if (tokenList.peek().getTokenType() == TokenType.ERR) {
			expression.children[1] = Error(tokenList);
		}
		*/
		
		return expression;
	}
	
	private static ParseTree Term(LinkedList<Token> tokenList) {
		ParseTree term = null;
		switch(tokenList.peek().getTokenType()) {
		case ICONST:
			term = new ParseTree(new Token(TokenType.TERM), Number(tokenList));
			break;
			
		case VAR:
			term = new ParseTree(new Token(TokenType.TERM), Variable(tokenList));
			break;
			
		case LPAREN:
			term = new ParseTree(TokenType.TERM);

			if (!tokenList.isEmpty())
				term.children[0] = new ParseTree(tokenList.remove());

			if (!tokenList.isEmpty())
				term.children[1] = Expression(tokenList);

			if (!tokenList.isEmpty())
				term.children[2] = new ParseTree(tokenList.remove());
			
			break;
			
		case ERR:
			term = new ParseTree(new Token(TokenType.TERM), Error(tokenList));
			break;
			
		default:
			System.out.println("Unaccounted for TokenType in ParseTree.Term(): " + tokenList.peek());
			System.exit(0);
			break;
		}
		
		return term;
	}
	
	private static ParseTree Number(LinkedList<Token> tokenList) {
		ParseTree number = null;
		Token nextToken = tokenList.remove();
		if (tokenList.peek().getTokenType() == TokenType.ERR) {
			number = new ParseTree(nextToken, Error(tokenList));
		}
		else if (tokenList.peek().getTokenType() == TokenType.ICONST) {
			number = new ParseTree(nextToken, Number(tokenList));
		}
		else {
			number = new ParseTree(nextToken);
		}
		
		return number;
	}
	
	private static ParseTree Variable(LinkedList<Token> tokenList) {
		ParseTree variable = null;
		Token nextToken = tokenList.remove();
		if (tokenList.peek().getTokenType() == TokenType.ERR) {
			variable = new ParseTree(nextToken, Error(tokenList));
		}
		else if (tokenList.peek().getTokenType() == TokenType.VAR) {
			variable = new ParseTree(nextToken, Variable(tokenList));
		}
		else {
			variable = new ParseTree(nextToken);
		}
		
		return variable;
	}
	
	private static ParseTree Error(LinkedList<Token> tokenList) {
		return new ParseTree(tokenList.pop());
	}
	
	/*
	public ParseTree() {
		children[0] = null;
		children[1] = null;
		children[2] = null;
	}
	
	public void printDerivation() {
		System.out.println(tt);
	}
	
	public String toString() {
		String retString = "";
		
		switch(tt.getTokenType()) {
		case ICONST:
			retString = "N";
			break;
			
		case VAR:
			retString = "V";
			break;
			
		case EQUAL:
			retString = "=";
			break;
			
		case OP:
			retString = tt.getLexeme();
			break;
			
		case LPAREN:
			retString = "(";
			break;
			
		case RPAREN:
			retString = ")";
			break;
			
		case SC:
			retString = ";";
			break;
			
		case BASE:
			if (children[1].getToken().getTokenType() == TokenType.EQUAL) {
				retString = "V=E;";
			}
			
			retString += "B";
			break;
			
		case EXPR:
			retString = "T";
			if (children[1] != null) {
				retString += children[1].getToken().getLexeme();
				retString += "E";
			}
			
			break;
			
		case TERM:
			if (children[1] == null) {
				retString = children[0].getToken().toString();
			}
			else {
				retString = "(E)";
			}
			break;
			
		case ERR:
			retString = "\n|ERROR OCCURRED AT: " + tt.getLexeme() + "|\n";
			break;
			
		case START:
			retString = "$S$";
			break;

		case BEGIN:
		case END:
			retString = "$";
			break;
		
		default:
			retString = "Unlabeled TokenType";
		}
		
		
		
		return retString;
	}
	*/
	
	public Token getToken() {
		return tt;
	}
	
	public ParseTree[] getChildren() {	// This will ALYWAYS be an array of size 5
		return children;
	}
	
	
	// Constructors
	private ParseTree(TokenType type) {
		tt = new Token(type);
		children = new ParseTree[5];
		for (int i = 0; i < 5; i++)
			children[i] = null;
	}
	
	private ParseTree(Token inputToken) {
		tt = inputToken;
		children = new ParseTree[5];
		for (int i = 0; i < 5; i++)
			children[i] = null;
	}
	
	private ParseTree(Token inputToken, ParseTree firstChild) {
		tt = inputToken;
		children = new ParseTree[5];
		children[0] = firstChild;
		for (int i = 1; i < 5; i++)
			children[i] = null;
	}
	
	private ParseTree(Token inputToken, ParseTree firstChild, ParseTree secondChild) {
		tt = inputToken;
		children = new ParseTree[5];
		children[0] = firstChild;
		children[1] = secondChild;
		for (int i = 2; i < 5; i++)
			children[i] = null;
	}
	
	private ParseTree(Token inputToken, ParseTree firstChild, ParseTree secondChild, ParseTree thirdChild) {
		tt = inputToken;
		children = new ParseTree[5];
		children[0] = firstChild;
		children[1] = secondChild;
		children[2] = thirdChild;
		children[3] = null;
		children[4] = null;
	}
	
	private ParseTree(Token inputToken, ParseTree firstChild, ParseTree secondChild, ParseTree thirdChild, ParseTree fourthChild) {
		tt = inputToken;
		children = new ParseTree[5];
		children[0] = firstChild;
		children[1] = secondChild;
		children[2] = thirdChild;
		children[3] = fourthChild;
		children[4] = null;
	}
	
	private ParseTree(Token inputToken, ParseTree firstChild, ParseTree secondChild, ParseTree thirdChild, ParseTree fourthChild, ParseTree fifthChild) {
		tt = inputToken;
		children = new ParseTree[5];
		children[0] = firstChild;
		children[1] = secondChild;
		children[2] = thirdChild;
		children[3] = fourthChild;
		children[4] = fifthChild;
	}
	
}
