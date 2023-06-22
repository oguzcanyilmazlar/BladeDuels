package me.acablade.bladeduels.utils;

import java.io.IOException;
import java.io.InputStream;
import java.text.Normalizer;
import java.util.Collections;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import lombok.Builder;
import lombok.Singular;

/**
 * Class for generating speaking URLs.
 *
 * @author Danny Trunk
 * @since 1.0
 */
public class Slugify {
    private static final String EMPTY = "";
    private static final String UNDERSCORE = "_";
    private static final String HYPHEN = "-";
    private static final Pattern PATTERN_NON_ASCII = Pattern.compile("[^\\p{ASCII}]+");
    private static final Pattern PATTERN_HYPHEN_SEPARATOR = Pattern.compile("[\\W\\s+]+");
    private static final Pattern PATTERN_UNDERSCORE_SEPARATOR =
            Pattern.compile("[[^a-zA-Z0-9\\-]\\s+]+");
    private static final Pattern PATTERN_TRIM_DASH = Pattern.compile("^-|-$");


    /**
     * Creates a slug from the specified text.
     *
     * @param text Text to create a slug from.
     * @return A string representing the slug.
     */
    public static String slugify(final String text) {
        return Optional.ofNullable(text)
                // remove leading and trailing whitespaces
                .map(String::trim)
                // run subsequent calls only if string is not empty
                .filter(s -> !s.equals(EMPTY))
                // transliterate or normalize
                .map(Slugify::normalize)
                // remove all remaining non ascii chars
                .map(str -> PATTERN_NON_ASCII.matcher(str).replaceAll(EMPTY))
                // replace remaining chars matching a pattern with underscore/hyphen
                .map(str -> PATTERN_UNDERSCORE_SEPARATOR.matcher(str).replaceAll(UNDERSCORE))
                // remove leading and trailing dashes
                .map(str -> PATTERN_TRIM_DASH.matcher(str).replaceAll(EMPTY))
                // convert to lower case if needed
                .map(str -> str.toLowerCase(Locale.ENGLISH))
                // return empty string if input is null or empty
                .orElse(EMPTY);
    }

    private static String replaceAll(final String input, final Map<String, String> replacements) {
        return replacements.keySet().stream()
                .map(key -> (Function<String, String>) str -> str.replace(key, replacements.get(key)))
                .reduce(Function.identity(), Function::andThen)
                .apply(input);
    }

    private static String normalize(final String input) {
        return Normalizer.normalize(input, Normalizer.Form.NFKD);
    }
}
