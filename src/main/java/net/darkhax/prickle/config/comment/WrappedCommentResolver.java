package net.darkhax.prickle.config.comment;

import net.darkhax.prickle.annotations.Value;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.lang.reflect.Field;

public class WrappedCommentResolver implements ICommentResolver {

    public static final ICommentResolver DEFAULT = new WrappedCommentResolver(80, true);

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