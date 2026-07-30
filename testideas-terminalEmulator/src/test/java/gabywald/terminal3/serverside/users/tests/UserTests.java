package gabywald.terminal3.serverside.users.tests;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import gabywald.terminal3.serverside.users.User;
import gabywald.terminal3.serverside.users.User.Role;
import gabywald.terminal3.serverside.users.UserDB;

/**
 * @author Gabriel Gabriel Chandesris (2026)
 */
class UserTests {

	@BeforeAll
	static void setUpBeforeClass() throws Exception {
	}

	@AfterAll
	static void tearDownAfterClass() throws Exception {
	}

	@BeforeEach
	void setUp() throws Exception {
	}

	@AfterEach
	void tearDown() throws Exception {
	}

	@Test
	void testUser() {
		String login	= "login";
		String ident	= "ident";
		// String pswd		= "pswd";
		Role role		= Role.ADMIN;
		
		User testUser = new User(login, ident, role);
		Assertions.assertNotNull(testUser);
		Assertions.assertEquals(login, testUser.getLogin());
		Assertions.assertEquals(ident, testUser.getUsername());
		Assertions.assertEquals(role, testUser.getRole());
		Assertions.assertEquals(role.toString(), testUser.getRoleSTR());
	}

	@Test
	void testGetUser() {
		String login1	= "login";
		String pswd1	= "pswd";
		User testUser1	= UserDB.getInstance().getUser(login1, pswd1);
		Assertions.assertNull(testUser1);
		
		// "user", "psswd", "Test User"
		String login2	= "user";
		String ident2	= "Test User";
		String pswd2	= "psswd";
		Role role2		= null;
		User testUser2	= UserDB.getInstance().getUser(login2, pswd2);
		Assertions.assertNotNull(testUser2);
		Assertions.assertEquals(login2, testUser2.getLogin());
		Assertions.assertEquals(ident2, testUser2.getUsername());
		Assertions.assertEquals(role2, testUser2.getRole());
		Assertions.assertNull(testUser2.getRole());
		// Assertions.assertEquals(role2.toString(), testUser2.getRoleSTR());
		
	}

}
