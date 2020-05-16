



public class Token {
	private TokenType tt;
	private String lexeme;
	
	
	public Token() {
		tt = TokenType.ERR;
		lexeme = "";
	}
	
	public Token(TokenType tt) {
		this.tt = tt;
		this.lexeme = "";
	}
	
	public Token(TokenType tt, String lexeme) {
		this.tt = tt;
		this.lexeme = lexeme;
	}
	
	
	public TokenType getTokenType() {
		return tt;
	}
	
	public String getLexeme() {
		return lexeme;
	}
	
	
	@Override
	public String toString() {
		String retString = "";
		
		switch(tt){
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
			retString = lexeme;
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
			retString = "B";
			break;
			
		case EXPR:
			retString = "E";
			break;
			
		case TERM:
			retString = "T";
			break;
			
		case ERR:
			retString = "\n|ERROR OCCURRED AT: " + lexeme + "|\n";
			break;
			
		case START:
			retString = "S";
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
	
	
}
