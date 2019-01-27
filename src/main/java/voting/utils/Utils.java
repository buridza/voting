package voting.utils;

import java.util.Random;

public class Utils {
    public static String createUri() {
        Random random = new Random();
        StringBuilder uri = new StringBuilder();
        for (int i = 0; i < 30; i++) {
            if (random.nextBoolean())
                uri.append((char) (random.nextInt(25) + 65));
            else uri.append((char) (random.nextInt(25) + 97));
        }
        return uri.toString();
    }
}
