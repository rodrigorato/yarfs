package a16.yarfs.server.domain;

import a16.yarfs.server.exception.WrongPasswordException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Class SessionTest
 * tests for the Session class
 */
public class SessionTest {
    private String USER1_CORRECT_PASSWORD = "password1";
    private String USER1_USERNAME = "sesionusertest";
    private User u1;
    private Session session;
    private Manager manager = Manager.getInstance();

    @Before
    public void setUp() throws Exception {
        u1 = new User(USER1_USERNAME, USER1_CORRECT_PASSWORD);
        session = new Session(manager, u1, USER1_CORRECT_PASSWORD);
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test (expected = WrongPasswordException.class)
    public void wrongPasswordTest() throws Exception {
        new Session(manager, u1, USER1_CORRECT_PASSWORD + "aaa");
    }

    // FIXME: should we test more stuff?

    @Test
    public void isExpired() throws Exception {
        // session was just created so it should not be expired as long
        // as the test does not take hours to complete
        assertFalse(session.isExpired());
    }

    @Test
    public void tokenToString() throws Exception {
        String strToken = Session.tokenToString(session.getToken());
        assertNotEquals("token should not be an empty string", "", strToken);
        assertEquals("token str conversion failed", session.getToken(),
                Session.stringToToken(strToken));
    }

    @Test
    public void getToken() throws Exception {
        assertTrue(session.getToken() != 0);
    }

    @Test
    public void getUser() throws Exception {
        assertSame(u1, session.getUser());
    }

}