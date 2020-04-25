

public enum TokenType {
	// Integer constant and variables
	ICONST,
	VAR,
	
	// Operators, parenths, semicolon
	EQUAL,		// a =
	OP,			// any of the operators +-*/
	LPAREN,		// a (
	RPAREN,		// a )
	SC,			// a semicolon
	
	// Combined parts
	BASE,
	EXPR,
	TERM,
	
	
	// Etc 
	SPACE,
	START,		// Special tokenType 
	BEGIN,		// Returned iff $ is encountered as first symbol in the given string
	END,		// Returned iff $ is encountered as final symbol in the given string
	ERR			// any error returns this token
}
