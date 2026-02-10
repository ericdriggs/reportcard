package io.github.ericdriggs.reportcard.persist.tags;

/**
 * Exception thrown when parsing an invalid tag expression.
 *
 * <p>This is an unchecked exception as parse errors are typically programming
 * errors or invalid user input that should be validated before parsing.
 */
public class ParseException extends RuntimeException {

    private final int position;

    /**
     * Creates a new ParseException with a message and position.
     *
     * @param message the error message
     * @param position the position in the input where the error occurred
     */
    public ParseException(String message, int position) {
        super(message + " at position " + position);
        this.position = position;
    }

    /**
     * Creates a new ParseException with just a message.
     *
     * @param message the error message
     */
    public ParseException(String message) {
        super(message);
        this.position = -1;
    }

    /**
     * Returns the position in the input where the error occurred.
     *
     * @return the position, or -1 if not applicable
     */
    public int getPosition() {
        return position;
    }
}
