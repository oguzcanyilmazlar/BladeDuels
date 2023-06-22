package me.acablade.bladeduels.utils;

import java.util.Random;

/**
 * <p>This class will efficiently generate insecure random
 * alpha-numeric Strings.</p>
 *
 * <p>Source largely based on
 * <a href="http://stackoverflow.com/a/41156/772122">this example</a>
 * from Stack Overflow by erickson.</p>
 *
 * <p><a href="http://creativecommons.org/licenses/by-sa/3.0/">
 *   Licensed under an Attribution-ShareAlike 3.0 Unported (CC BY-SA 3.0)
 * </a></p>
 *
 * @author Tristan Waddington
 */
public class RandomString {
    private static final char[] symbols = new char[36];

    static {
        for (int idx = 0; idx < 10; ++idx) {
            symbols[idx] = (char) ('0' + idx);
        }
        for (int idx = 10; idx < 36; ++idx) {
            symbols[idx] = (char) ('a' + idx - 10);
        }
    }

    private final Random random = new Random();
    private final char[] buf;

    public RandomString(int length) {
        buf = new char[length];
    }

    /**
     * Generate an insecure random alpha-numeric String of
     * the length given in the constructor.
     */
    public String getString() {
        for (int idx = 0; idx < buf.length; ++idx) {
            buf[idx] = symbols[random.nextInt(symbols.length)];
        }
        return new String(buf);
    }
}