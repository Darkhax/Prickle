package net.darkhax.prickle.config.comment;

import net.darkhax.prickle.annotations.Value;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.LinkedList;
import java.util.List;

/**
 * Represents a comment in a Prickle file. This supports single line comments or multi-line comments as arrays.
 */
public class WrappedComment extends Comment {

    /**
     * The default wrapped comment resolver. Lines will be wrapped to 80 and multi-line comments will be padded.
     */
    public static final ICommentResolver RESOLVER = new WrappedCommentResolver(80, true);

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

    /**
     * Resolves comments by wrapping the text to the specified width and optionally padding multi-line comments to the
     * maximum line length.
     */
    public static class WrappedCommentResolver implements ICommentResolver {

        private final int lineLength;
        private final boolean padLength;

        public WrappedCommentResolver(int lineLength, boolean padLength) {
            this.lineLength = lineLength;
            this.padLength = padLength;
        }

        @Nullable
        @Override
        public WrappedComment resolve(Field field, @Nullable Object value, Value valueMeta) throws IOException {
            return ICommentResolver.hasComment(valueMeta) ? new WrappedComment(valueMeta.comment(), this.lineLength, this.padLength) : null;
        }
    }
}