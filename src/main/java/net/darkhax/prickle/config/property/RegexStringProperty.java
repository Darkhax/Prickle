package net.darkhax.prickle.config.property;

import com.google.gson.stream.JsonWriter;
import net.darkhax.prickle.annotations.Regex;
import net.darkhax.prickle.annotations.Value;
import net.darkhax.prickle.config.property.adapter.IPropertyAdapter;
import org.slf4j.Logger;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.regex.Pattern;

/**
 * A property that holds a string that is validated using Regex.
 */
public class RegexStringProperty extends ObjectProperty<String> {

    /**
     * A property adapter for regex validated strings.
     */
    public static IPropertyAdapter<RegexStringProperty> ADAPTER = new Adapter();

    private final String regex;
    private final Pattern pattern;

    private RegexStringProperty(Field field, Object parent, String value, String regex, Value valueMeta) {
        super(field, parent, value, valueMeta);
        this.regex = regex;
        this.pattern = Pattern.compile(this.regex);
    }

    @Override
    public void writeAdditionalComments(JsonWriter writer, PropertyResolver resolver, Logger log) throws IOException {
        if (this.regex != null) {
            writer.name("//regex");
            writer.value(this.regex);
        }
    }

    @Override
    public boolean validate(String value) throws IllegalArgumentException {
        return value != null && this.pattern.matcher(value).matches();
    }

    private static class Adapter implements IPropertyAdapter<RegexStringProperty> {

        @Override
        public RegexStringProperty toValue(PropertyResolver resolver, Field field, Object parent, Object value, Value valueMeta) {
            if (value instanceof String stringVal) {
                final Regex regex = field.getAnnotation(Regex.class);
                if (regex != null) {
                    return new RegexStringProperty(field, parent, stringVal, regex.value(), valueMeta);
                }
            }
            return null;
        }
    }
}