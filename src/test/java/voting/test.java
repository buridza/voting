package voting;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import voting.dto.Vote;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;
import java.util.Set;

@RunWith(MockitoJUnitRunner.class)
public class test {
    public String createVoteUri() {
        Random random = new Random();
        StringBuilder uri = new StringBuilder();
        for (int i = 0; i < 30; i++) {
            if (random.nextBoolean())
                uri.append((char) (random.nextInt(25) + 65));
            else uri.append((char) (random.nextInt(25) + 97));
        }
        return uri.toString();
    }

    @Test
    public void allCharCode() {
        for (int i = 0; i < 255; ) {
            System.out.printf("%5s -> %5s , %5s -> %5s , %5s -> %5s , %5s -> %5s , %5s -> %5s\n", (char) i, i++, (char) i, i++, (char) i, i++, (char) i, i++, (char) i, i++);
        }
    }

    @Test
    public void runSmthCreateUri() {
        Set<String> strings = new HashSet<>();
        long l = System.currentTimeMillis();
        for (int i = 0; i < 10000000; i++) {
            strings.add(createVoteUri());
        }
        System.out.println(strings.size() + " " + (System.currentTimeMillis() - l));
    }


}
