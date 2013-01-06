package models;

import helpers.AppException;
import helpers.Hash;
import org.picketbox.core.util.TimeBasedOTPUtil;
import play.data.format.Formats;
import play.data.validation.Constraints;
import play.db.ebean.Model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.security.GeneralSecurityException;
import java.util.Date;

@Entity
public class User extends Model {

    public static final int NUM_DIGITS = 6;

    @Id
    public Long id;

    @Constraints.Required
    @Formats.NonEmpty
    @Column(unique = true)
    public String email;

    @Constraints.Required
    @Formats.NonEmpty
    @Column(unique = true)
    public String fullname;

    public String confirmationToken;

    @Constraints.Required
    @Formats.NonEmpty
    public String passwordHash;

    @Constraints.Required
    @Formats.NonEmpty
    public String secretKey;

    @Formats.DateTime(pattern = "yyyy-MM-dd HH:mm:ss")
    public Date dateCreation;

    @Formats.NonEmpty
    public Boolean validated = false;

    public static Model.Finder<Long, User> find = new Model.Finder<Long, User>(Long.class, User.class);

    public static User findByEmail(String email) {
        return find.where().eq("email", email).findUnique();
    }

    public static User findByConfirmationToken(String token) {
        return find.where().eq("confirmationToken", token).findUnique();
    }

    public static User authenticate(String email, String clearPassword, String timeBasedOTP)
            throws AppException, GeneralSecurityException {

        // get the user with email only to keep the salt password
        User user = find.where().eq("email", email).findUnique();
        if (user != null) {
            // get the hash password from the salt + clear password
            if (Hash.checkPassword(clearPassword, user.passwordHash)
                    && TimeBasedOTPUtil.validate(timeBasedOTP, user.secretKey.getBytes(), NUM_DIGITS)) {
                return user;
            }
        }
        return null;
    }

    public void changePassword(String password) throws AppException {
        this.passwordHash = Hash.createPassword(password);
        this.save();
    }

    public static boolean confirm(User user) throws AppException {
        if (user == null) {
            return false;
        }

        user.confirmationToken = null;
        user.validated = true;
        user.save();
        return true;
    }
}
