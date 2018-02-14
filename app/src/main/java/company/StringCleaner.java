package company;

/**
 * Created by Anna on 7/9/2015.
 */
public class StringCleaner {
    public static String[] clearText(String text) {
        StringBuilder cleared = new StringBuilder();
        for (int i = 0; i < text.length(); i++) {
            if (i == 32) {
                int stop = 4;
            }
            char ch = text.charAt(i);
            if (Character.isDigit(ch) ||
                    Character.isLetter(ch) ||
                    (Character.isWhitespace(ch) && cleared.length() > 0 && cleared.charAt(cleared.length() - 1) != ' ')) {
                if (Character.isSpaceChar(ch)) {
                    ch = ' ';
                }
                cleared.append(ch);
            }
        }
        String clearedString = cleared.toString().toLowerCase();
        return clearedString.split(" ");
    }
}
