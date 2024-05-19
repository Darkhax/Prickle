package net.darkhax.prickle.config.property;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Type;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * Represents a comment in a Prickle file. This supports single line comments or multi-line comments as arrays.
 */
public class Comment {

    /**
     * A GSON type adapter that can handle serializing Prickle comment objects.
     */
    public static final Adapter ADAPTER = new Adapter();

    /**
     * The lines of the comment.
     */
    private final String[] comments;

    /**
     * Creates a new comment object from a collection of lines.
     *
     * @param lines The lines of the comment.
     */
    public Comment(Collection<String> lines) {
        this(lines.toArray(new String[0]));
    }

    /**
     * Creates a new comment from an array of lines.
     *
     * @param lines The lines of the comment.
     */
    public Comment(String... lines) {
        this.comments = lines;
    }

    /**
     * Checks if the comment is empty. A comment is empty if it has no lines of text or the only line is empty.
     *
     * @return If the comment is empty.
     */
    public boolean isEmpty() {
        return this.comments.length == 0 || (this.comments.length == 1 && comments[0].isBlank());
    }

    /**
     * Creates a comment by wrapping and padding the text. The text will be wrapped and padded to 80 characters.
     *
     * @param text The text to wrap.
     * @return The wrapped comment.
     */
    public static Comment of(String text) {
        return text.length() > 80 ? new Comment(wrap(text, 80, true)) : new Comment(text);
    }

    /**
     * Create a wrapped comment from text. The text may optionally be padded to the line length.
     *
     * @param text       The text to include in the comment.
     * @param lineLength The maximum length per line of the comment. If a word goes past this length it will be wrapped
     *                   to the text line.
     * @param pad        When true the lines will be right-padded to the maximum line length when they are below the
     *                   limit. This creates a consistent width for multi-line comments.
     * @return The wrapped comment.
     */
    public static Comment wrapped(String text, int lineLength, boolean pad) {
        return new Comment(wrap(text, lineLength, pad));
    }

    /**
     * An internal helper to wrap and pad a string to a given length.
     *
     * @param text       The text to wrap.
     * @param lineLength The maximum length per-line.
     * @param pad        When true the lines will be padded to the line length.
     * @return A list of strings representing the wrapped lines of the text.
     */
    private static List<String> wrap(String text, int lineLength, boolean pad) {
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

        return lines;
    }

    /**
     * A GSON adapter to serialize comment objects.
     */
    private static final class Adapter implements JsonSerializer<Comment>, JsonDeserializer<Comment> {

        @Override
        public JsonElement serialize(Comment src, Type typeOfSrc, JsonSerializationContext context) {
            if (src.comments.length == 1) {
                return new JsonPrimitive(src.comments[0]);
            }
            else {
                final JsonArray array = new JsonArray();
                for (String line : src.comments) {
                    array.add(line);
                }
                return array;
            }
        }

        @Override
        public Comment deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            if (json instanceof JsonPrimitive primitive && primitive.isString()) {
                return new Comment(primitive.getAsString());
            }
            else if (json instanceof JsonArray array) {
                final String[] comments = new String[array.size()];
                for (int i = 0; i < comments.length; i++) {
                    if (array.get(i) instanceof JsonPrimitive primitive && primitive.isString()) {
                        comments[i] = primitive.getAsString();
                    }
                    else {
                        throw new JsonParseException("Comments can only contain strings. Found " + array.get(i));
                    }
                }
                return new Comment(comments);
            }

            throw new JsonParseException("Comments can only be defined as a string or a string array. Found " + json);
        }
    }
}