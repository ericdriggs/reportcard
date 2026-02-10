package io.github.ericdriggs.reportcard.persist.tags;

/**
 * Recursive descent parser for tag expressions.
 *
 * <p>Parses boolean expressions with AND, OR, and parentheses:
 * <pre>
 * expr     := term (OR term)*
 * term     := factor (AND factor)*
 * factor   := TAG | ( expr )
 * TAG      := identifier | identifier=value
 * </pre>
 *
 * <p>Precedence: AND binds tighter than OR (standard boolean algebra).
 * <ul>
 *   <li>{@code a OR b AND c} parses as {@code a OR (b AND c)}</li>
 *   <li>Parentheses override: {@code (a OR b) AND c}</li>
 * </ul>
 *
 * <p>Usage:
 * <pre>
 * TagExpr expr = TagExpressionParser.parse("smoke AND env=prod");
 * </pre>
 *
 * @see TagExpr
 * @see SimpleTag
 * @see AndExpr
 * @see OrExpr
 */
public class TagExpressionParser {

    private final String input;
    private int pos;

    /**
     * Creates a parser for the given input.
     *
     * @param input the tag expression to parse
     */
    private TagExpressionParser(String input) {
        if (input == null) {
            throw new ParseException("Input cannot be null");
        }
        this.input = input.trim();
        this.pos = 0;
    }

    /**
     * Parses a tag expression string into an AST.
     *
     * @param input the tag expression (e.g., "smoke AND env=prod")
     * @return the parsed AST
     * @throws ParseException if the input is invalid
     */
    public static TagExpr parse(String input) {
        TagExpressionParser parser = new TagExpressionParser(input);
        if (parser.input.isEmpty()) {
            throw new ParseException("Empty expression", 0);
        }
        TagExpr result = parser.parseExpr();

        // Ensure all input was consumed
        parser.skipWhitespace();
        if (parser.pos < parser.input.length()) {
            throw new ParseException("Unexpected character '" + parser.input.charAt(parser.pos) + "'", parser.pos);
        }

        return result;
    }

    /**
     * Parses an expression: term (OR term)*
     */
    private TagExpr parseExpr() {
        TagExpr left = parseTerm();

        while (matchKeyword("OR")) {
            TagExpr right = parseTerm();
            left = new OrExpr(left, right);
        }

        return left;
    }

    /**
     * Parses a term: factor (AND factor)*
     */
    private TagExpr parseTerm() {
        TagExpr left = parseFactor();

        while (matchKeyword("AND")) {
            TagExpr right = parseFactor();
            left = new AndExpr(left, right);
        }

        return left;
    }

    /**
     * Parses a factor: TAG | ( expr )
     */
    private TagExpr parseFactor() {
        skipWhitespace();

        if (pos >= input.length()) {
            throw new ParseException("Unexpected end of expression", pos);
        }

        if (match('(')) {
            skipWhitespace();

            // Check for empty parentheses
            if (pos < input.length() && input.charAt(pos) == ')') {
                throw new ParseException("Empty parentheses", pos);
            }

            TagExpr expr = parseExpr();
            expect(')');
            return expr;
        }

        return parseTag();
    }

    /**
     * Parses a tag: identifier or identifier=value
     * <p>
     * Tags consist of alphanumeric characters, underscores, hyphens, and equals signs.
     * Tags stop at whitespace, parentheses, or end of input.
     */
    private SimpleTag parseTag() {
        skipWhitespace();

        int start = pos;

        // Parse tag characters: letters, digits, underscore, hyphen, equals, colons
        while (pos < input.length()) {
            char c = input.charAt(pos);
            if (Character.isLetterOrDigit(c) || c == '_' || c == '-' || c == '=' || c == ':' || c == '.') {
                pos++;
            } else {
                break;
            }
        }

        if (pos == start) {
            // No tag characters found - check what we have
            if (pos < input.length()) {
                char c = input.charAt(pos);
                if (c == ')') {
                    throw new ParseException("Unexpected ')'", pos);
                }
                throw new ParseException("Expected tag, found '" + c + "'", pos);
            }
            throw new ParseException("Expected tag", pos);
        }

        String tag = input.substring(start, pos);

        // Check if the tag is actually an operator (would be invalid here)
        if (tag.equals("AND") || tag.equals("OR")) {
            throw new ParseException("Unexpected operator '" + tag + "'", start);
        }

        return new SimpleTag(tag);
    }

    /**
     * Skips whitespace characters.
     */
    private void skipWhitespace() {
        while (pos < input.length() && Character.isWhitespace(input.charAt(pos))) {
            pos++;
        }
    }

    /**
     * Matches and consumes a single character.
     *
     * @param c the character to match
     * @return true if matched and consumed
     */
    private boolean match(char c) {
        skipWhitespace();
        if (pos < input.length() && input.charAt(pos) == c) {
            pos++;
            return true;
        }
        return false;
    }

    /**
     * Matches and consumes a keyword (AND or OR).
     * <p>
     * Keywords are case-sensitive and must be followed by whitespace or end of input.
     *
     * @param keyword the keyword to match (AND or OR)
     * @return true if matched and consumed
     */
    private boolean matchKeyword(String keyword) {
        skipWhitespace();
        int savedPos = pos;

        // Check if keyword matches
        if (pos + keyword.length() <= input.length() &&
            input.substring(pos, pos + keyword.length()).equals(keyword)) {

            // Check that it's not part of a longer identifier
            int afterKeyword = pos + keyword.length();
            if (afterKeyword >= input.length() ||
                !Character.isLetterOrDigit(input.charAt(afterKeyword)) &&
                input.charAt(afterKeyword) != '_' &&
                input.charAt(afterKeyword) != '-') {

                pos = afterKeyword;
                return true;
            }
        }

        pos = savedPos;
        return false;
    }

    /**
     * Expects and consumes a specific character.
     *
     * @param c the expected character
     * @throws ParseException if the character is not found
     */
    private void expect(char c) {
        skipWhitespace();
        if (pos >= input.length()) {
            throw new ParseException("Expected '" + c + "', found end of input", pos);
        }
        if (input.charAt(pos) != c) {
            throw new ParseException("Expected '" + c + "', found '" + input.charAt(pos) + "'", pos);
        }
        pos++;
    }
}
