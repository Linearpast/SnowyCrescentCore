package io.zershyan.sccore.patchouli.datagen.create.data.format;

public class StringFormat implements IFormat {
    private final String value;

    public StringFormat(String value) {
        this.value = value;
    }

    @Override
    public String parse() {
        return value;
    }
}
