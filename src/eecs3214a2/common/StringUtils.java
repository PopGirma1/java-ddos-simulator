package eecs3214a2.common;

/**
 * String Poly-fills for Java 7.
 *
 * @author Vincent Chu
 * @version 1.0
 */
public class StringUtils {

    /**
     * Returns a new String composed of copies of the
     * {@code CharSequence elements} joined together with a copy of
     * the specified {@code delimiter}.
     *
     * <blockquote>For example,
     * <pre>{@code
     *     String message = String.join("-", "Java", "is", "cool");
     *     // message returned is: "Java-is-cool"
     * }</pre></blockquote>
     *
     * Note that if an element is null, then {@code "null"} is added.
     *
     * @param  delimiter the delimiter that separates each element
     * @param  elements the elements to join together.
     *
     * @return a new {@code String} that is composed of the {@code elements}
     *         separated by the {@code delimiter}
     * @throws NullPointerException If {@code delimiter} or {@code elements}
     *         is {@code null}
     */
    public static String join(CharSequence delimiter, CharSequence... elements) {
        String dlm = "";
        StringBuilder sb = new StringBuilder();
        for (CharSequence str : elements) {
            sb.append(dlm);
            if (dlm.isEmpty()) {
                dlm = delimiter.toString();
            }
            sb.append(str == null ? "" : str);
        }
        return sb.toString();
    }

} // StringUtils
