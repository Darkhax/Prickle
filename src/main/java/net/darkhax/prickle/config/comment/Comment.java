package net.darkhax.prickle.config.comment;

public class Comment implements IComment {

    private final String[] lines;

    public Comment(String... lines) {
        this.lines = lines;
    }

    @Override
    public String[] getLines() {
        return this.lines;
    }
}