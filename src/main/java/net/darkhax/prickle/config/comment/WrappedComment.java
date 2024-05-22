package net.darkhax.prickle.config.comment;

import org.apache.commons.lang3.StringUtils;

import java.util.LinkedList;
import java.util.List;

/**
 * Represents a comment in a Prickle file. This supports single line comments or multi-line comments as arrays.
 */
public class WrappedComment extends Comment {

    /**
     * Create a wrapped comment.
     *
     * @param text       The comment text.
     * @param lineLength The maximum length per line of the comment. If a word causes the line to overflow it will be
     *                   wrapped to the next line.
     * @param pad        When true the lines will be right-padded to the maximum line length if they are below the
     *                   limit.
     */
    public WrappedComment(String text, int lineLength, boolean pad) {
        super(wrap(text, lineLength, pad));
    }

    /**
     * An internal helper to wrap and pad a string to a given length.
     *
     * @param text       The text to wrap.
     * @param lineLength The maximum length per-line.
     * @param pad        When true the lines will be padded to the line length.
     * @return A list of strings representing the wrapped lines of the text.
     */
    private static String[] wrap(String text, int lineLength, boolean pad) {
        final List<String> lines = new LinkedList<>();
        final String[] words = text.split("\\s+");
        StringBuilder currentLine = new StringBuilder();
        for (String word : words) {
            if (currentLine.length() + word.length() <= lineLength) {
                currentLine.append(word);
                if (currentLine.length() != lineLength) {
                    currentLine.append(" ");
                }
            }
            else {
                lines.add(pad ? StringUtils.rightPad(currentLine.toString(), lineLength) : currentLine.toString().trim());
                currentLine = new StringBuilder(word + " ");
            }
        }
        if (!currentLine.isEmpty()) {
            lines.add(pad ? StringUtils.rightPad(currentLine.toString(), lineLength) : currentLine.toString().trim());
        }
        return lines.toArray(String[]::new);
    }
}