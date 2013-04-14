package models;

import javax.persistence.*;

import com.avaje.ebean.Ebean;
//import org.apache.commons.lang.RandomStringUtils;
//import org.jasypt.exceptions.EncryptionOperationNotPossibleException;
//import org.jasypt.util.password.BasicPasswordEncryptor;
import play.db.ebean.Model;

import com.avaje.ebean.ExpressionList;

/**
 * Initial version based on work by Steve Chaloner (steve@objectify.be) for
 * Deadbolt2
 */
@Entity
@Table(name = "users")
public class User extends Model {
    /**
     *
     */

    @Id
    public Long id;

    // if you make this unique, keep in mind that users *must* merge/link their
    // accounts then on signup with additional providers
    @Column(unique = true)
    public String username;

    @Column(unique = true)
    public String email;

    public String password;

    //@Formats.DateTime(pattern = "yyyy-MM-dd HH:mm:ss")
    //public Date lastLogin;

    //public boolean active;

    //public boolean emailValidated;

    //@ManyToMany
    //public List<SecurityRole> roles;

    //@OneToMany(cascade = CascadeType.ALL)
    //public List<LinkedAccount> linkedAccounts;

    //@ManyToMany
    //public List<UserPermission> permissions;

    public static final Finder<Long, User> find = new Finder<Long, User>(
            Long.class, User.class);

    //@Override
    //public List<? extends Role> getRoles() {
    //	return roles;
    //}

    //@Override
    //public List<? extends Permission> getPermissions() {
    //	return permissions;
    //}

    //public static User findByUsernamePasswordIdentity(
    //		final UsernamePasswordAuthUser identity) {
    //	return getUsernamePasswordAuthUserFind(identity).findUnique();
    //}

    //private static ExpressionList<User> getUsernamePasswordAuthUserFind(
    //		final UsernamePasswordAuthUser identity) {
    //	return getEmailUserFind(identity.getEmail()).eq(
    //			"linkedAccounts.providerKey", identity.getProvider());
    //}

    public static User findByUsername(final String username) {
        return getNameUserFind(username).findUnique();
    }

    private static ExpressionList<User> getNameUserFind(final String username) {
        return find.where().eq("username", username);
    }

    public static User findByUsernameOrEmail(final String usernameOrEmail) {
        return find.where().disjunction().eq("username", usernameOrEmail).eq("email", usernameOrEmail).findUnique();
    }

    public static User authenticate(String username, String password) {

        User user = findByUsername(username);

        if (user == null)
            return null;

//        try {
//            BasicPasswordEncryptor passwordEncryptor = new BasicPasswordEncryptor();
//            if (passwordEncryptor.checkPassword(password, user.password)) {
//                // correct!
//                return user;
//            } else {
//                // bad login!
//                return null;
//            }
//        } catch (EncryptionOperationNotPossibleException e) {
//            return null;
//        }

        return user;
    }

    public String resetPassword() {
//        String newPassword = RandomStringUtils.randomAlphabetic(8);
        return "12345678";
//        try {

//            BasicPasswordEncryptor passwordEncryptor = new BasicPasswordEncryptor();
//            this.password = passwordEncryptor.encryptPassword(newPassword);
//            Ebean.save(this);
//            return newPassword;
//        } catch (EncryptionOperationNotPossibleException e) {
//            return null;
//        } catch (OptimisticLockException e) {
//            return null;
//        }
    }
}
