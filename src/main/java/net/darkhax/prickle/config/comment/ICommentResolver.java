package net.darkhax.prickle.config.comment;

import net.darkhax.prickle.annotations.Value;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.lang.reflect.Field;

public interface ICommentResolver {

    /**
     * Attempts to generate a comment
     *
     * @param field
     * @param value
     * @param valueMeta
     * @return
     * @throws IOException
     */
    @Nullable
    IComment resolve(Field field, @Nullable Object value, Value valueMeta) throws IOException;

    static boolean hasComment(Value valueMeta) {
        return valueMeta != null && valueMeta.comment() != null && !valueMeta.comment().isBlank();
    }
}