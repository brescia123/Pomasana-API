package helper;

import java.math.BigInteger;
import java.security.SecureRandom;

public class TokenGenerator {

    private static SecureRandom random = new SecureRandom();

    public static String generate(){
        return new BigInteger(130, random).toString(32);
    }

}
