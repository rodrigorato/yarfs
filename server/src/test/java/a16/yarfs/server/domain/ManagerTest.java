package a16.yarfs.server.domain;

import a16.yarfs.server.exception.DuplicatedUsernameException;
import a16.yarfs.server.exception.api.LoginException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Test class for Manager
 */
public class ManagerTest {

    private Manager manager;
    private String token;

    @Before
    public void setUp() throws Exception {
        manager = Manager.getInstance();
        manager.registerUser("Testoesterona", "password1");
        token = manager.loginUser("Testoesterona", "password1");
    }

    @After
    public void tearDown() throws Exception {
        Manager.selfDestruct();
    }

    @Test
    public void getInstance() throws Exception {
        assertNotNull("Error in get instance.", Manager.getInstance());
    }

    @Test
    public void hasSession() throws Exception {
        assertTrue("Error in hasSession", manager.hasSession(token));
    }

    @Test
    public void hasNoSession() throws Exception {
        assertFalse("Error in hasNoSession", manager.hasSession((long) 10000));
    }

    @Test
    public void registerUser() throws Exception {
        manager.registerUser("bla","bla");
        assertTrue("Error in registering", manager.hasUser("bla"));
    }

    @Test(expected = DuplicatedUsernameException.class)
    public void registerSameUser() throws Exception {
        manager.registerUser("Testoesterona","password2");
    }

    @Test
    public void loginUser() throws Exception {
        String token = manager.loginUser("Testoesterona", "password1");
        assertNotNull("Error in login", token);
    }

    @Test(expected = LoginException.class)
    public void loginFailPassword() throws Exception {
        String token = manager.loginUser("Testoesterona", "password2");
    }

    @Test
    public void listUsers() throws Exception {
        List<String> users = manager.listUsers();
        List<String> realUsers = new ArrayList<>();
        realUsers.add("Testoesterona");
        assertEquals("Error in user list", users, realUsers);
    }

    @Test
    public void logout() throws Exception {
        manager.logout(1000);
    }

    @Test
    public void logout1() throws Exception {
        manager.logout(token);
    }

}