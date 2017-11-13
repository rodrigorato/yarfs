/**
 * Created by nuno at 13/11/17
 */
package a16.yarfs.server.domain;


import org.junit.Before;
import org.junit.Test;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

/**
 *  Class UserTest
 *  Tests for the User class...
 */
public class UserTest {
    protected String USER1_CORRECT_PASSWORD = "password1";
    protected String USER2_INITIAL_PASSWORD = "old";
    protected String USER2_NEW_PASSWORD     = "new";
    protected User u1;
    protected User u2;

    @Before
    public void setup() {
        u1 = new User("user1", USER1_CORRECT_PASSWORD);
        u2 = new User("user2", USER2_INITIAL_PASSWORD);
    }

    @Test
    public void correctPasswordTest()
    {
        assertTrue( u1.authenticate(USER1_CORRECT_PASSWORD) );
    }

    @Test
    public void failWrongPasswordTest()
    {
        assertFalse( u1.authenticate(USER1_CORRECT_PASSWORD + "aaa") );
    }
    
    @Test
    public void changePasswordTest()
    {
        u2.setPassword(USER2_INITIAL_PASSWORD);
        assertTrue( u2.authenticate(USER2_INITIAL_PASSWORD) );
        u2.setPassword(USER2_NEW_PASSWORD);
        assertFalse( u2.authenticate(USER2_INITIAL_PASSWORD) );
        assertTrue( u2.authenticate(USER2_NEW_PASSWORD) );
    }
}
