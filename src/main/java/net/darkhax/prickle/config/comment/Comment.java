package net.darkhax.prickle.config.comment;

/**
 * A simple multi-line comment implementation.
 */
public class Comment implements IComment {

    private final String[] lines;

    /**
     * Constructs a simple multi-line comment.
     *
     * @param lines The lines of the comment.
     */
    public Comment(String... lines) {
        this.lines = lines;
    }

    @Override
    public String[] getLines() {
        return this.lines;
    }
}