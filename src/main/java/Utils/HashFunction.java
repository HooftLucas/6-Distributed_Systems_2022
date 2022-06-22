package Utils;

public class HashFunction {
    /**
     * It converts a string into a number between -32768 and 32767.
     *
     * @param string The string to hash.
     * @return A hash code for the string.
     */
    public static int hash(String string) {
        long max = 2147483647;
        long min = -2147483648;
        return (int) (((long) string.hashCode() + max) * (32768.0 / (max + Math.abs(min))));
    }
}
