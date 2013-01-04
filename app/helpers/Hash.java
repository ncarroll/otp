package helpers;

import helpers.AppException;
import org.mindrot.jbcrypt.BCrypt;
import play.i18n.Messages;

public class Hash {

    public static String createPassword(String clearString) throws AppException {
        if (clearString == null) {
            throw new AppException(Messages.get("no.password.defined"));
        }
        return BCrypt.hashpw(clearString, BCrypt.gensalt());
    }

    public static boolean checkPassword(String candidate, String encryptedPassword) {
        if (candidate == null) {
            return false;
        }
        if (encryptedPassword == null) {
            return false;
        }
        return BCrypt.checkpw(candidate, encryptedPassword);
    }
}
