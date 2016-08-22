package ibrahim.radwan.doctorsconnect;

import org.junit.Test;

import ibrahim.radwan.doctorsconnect.utils.BCrypt;

public class TestBCrypt {
    @Test
    public void addition_isCorrect () throws Exception {
        String password = "20061996";
        String hashed = BCrypt.hashpw(password, BCrypt.gensalt());

        if (BCrypt.checkpw("exexex", "$2a$10$vShHIX3HJgafX6EM77KaouAXh7qg.ZPzEjkvSHW8S1PEAksYQAEyW"))
            System.out.println("It matches " + hashed);
        else
            System.out.println("It does not match");
    }
}