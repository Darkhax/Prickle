package net.darkhax.prickle.config.comment;

import net.darkhax.prickle.annotations.Value;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.lang.reflect.Field;

/**
 * Comment resolvers are responsible for generating an {@link IComment} from a config property. Comment resolvers can
 * resolve to a different comment implementation or even wrap or decorate them with additional info.
 */
public interface ICommentResolver {

    /**
     * Attempts to resolve a comment for a config property using the available information.
     *
     * @param field     The field being mapped.
     * @param value     The value of the field.
     * @param valueMeta The value annotation metadata.
     * @return The comment that was resolved. Null may be returned when the comment could not be resolved.
     * @throws IOException An exception may be thrown if fatal errors are encountered while resolving the comment.
     */
    @Nullable IComment resolve(Field field, @Nullable Object value, Value valueMeta) throws IOException;

    /**
     * Checks if a comment has been defined for the value.
     *
     * @param valueMeta The value metadata annotation.
     * @return If the value has a comment.
     */
    static boolean hasComment(Value valueMeta) {
        return valueMeta != null && valueMeta.comment() != null && !valueMeta.comment().isBlank();
    }
}