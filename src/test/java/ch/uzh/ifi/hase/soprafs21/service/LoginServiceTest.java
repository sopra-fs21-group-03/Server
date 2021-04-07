package ch.uzh.ifi.hase.soprafs21.service;

import ch.uzh.ifi.hase.soprafs21.constant.GameStatus;
import ch.uzh.ifi.hase.soprafs21.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs21.entity.User;
import ch.uzh.ifi.hase.soprafs21.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.web.server.ResponseStatusException;

import static org.junit.jupiter.api.Assertions.*;

public class LoginServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private LoginService loginService;

    private User testUser;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);

        // given
        testUser = new User();
        testUser.setId(1L);
        testUser.setPassword("testPassword");
        testUser.setUsername("testUsername");
        testUser.setToken("1");
        testUser.setMoney(0);
        testUser.setGamestatus(GameStatus.NOTREADY);

        // when -> any object is being save in the userRepository -> return the dummy testUser
        Mockito.when(userRepository.save(Mockito.any())).thenReturn(testUser);
    }

    /* Tests for registering a user */
    @Test
    public void createUser_validInputs_success() {
        // when -> any object is being save in the userRepository -> return the dummy testUser
        User createdUser = loginService.createUser(testUser);

        // then
        Mockito.verify(userRepository, Mockito.times(1)).save(Mockito.any());

        assertEquals(testUser.getId(), createdUser.getId());
        assertEquals(testUser.getPassword(), createdUser.getPassword());
        assertEquals(testUser.getUsername(), createdUser.getUsername());
        assertNotNull(createdUser.getToken());
        assertEquals(UserStatus.ONLINE, createdUser.getStatus());
    }

    @Test
    public void createUser_duplicateName_throwsException() {
        // given -> a first user has already been created
        loginService.createUser(testUser);

        // when -> setup additional mocks for UserRepository
        Mockito.when(userRepository.findByUsername(Mockito.any())).thenReturn(testUser);

        // then -> attempt to create second user with same user -> check that an error is thrown
        assertThrows(ResponseStatusException.class, () -> loginService.createUser(testUser));
    }

    @Test
    public void createUser_duplicateInputs_throwsException() {
        // given -> a first user has already been created
        loginService.createUser(testUser);

        // when -> setup additional mocks for UserRepository
        Mockito.when(userRepository.findByUsername(Mockito.any())).thenReturn(testUser);

        // then -> attempt to create second user with same user -> check that an error is thrown
        assertThrows(ResponseStatusException.class, () -> loginService.createUser(testUser));
    }

    /* Tests for logging in a user */
    @Test
    public void loginUser_validInputs_success(){

        //given -> a user has already been created
        loginService.createUser(testUser);

        User userInputs = new User();
        userInputs.setUsername("testUsername");
        userInputs.setPassword("testPassword");

        // when -> setup additional mocks for UserRepository
        Mockito.when(userRepository.findByUsername(Mockito.any())).thenReturn(testUser);

        String loggedInToken = loginService.checkLoginCredentials(userInputs);

        assertEquals(loggedInToken, testUser.getToken());
    }

    @Test
    public void loginUser_invalidInputs_errorThrown(){
        //given -> a user has already been created
        loginService.createUser(testUser);

        User userInputs = new User();
        userInputs.setUsername("falseUsername");
        userInputs.setPassword("falsePassword");

        // when -> setup additional mocks for UserRepository
        Mockito.when(userRepository.findByUsername(Mockito.any())).thenReturn(testUser);

        assertThrows(ResponseStatusException.class, () -> loginService.checkLoginCredentials(userInputs));
    }

    /* Tests for logging out a user */

    @Test
    public void logoutUser_validInputs_success(){
        //given -> a user has already been created
        loginService.createUser(testUser);

        User userInputs = new User();
        userInputs.setToken(testUser.getToken());

        // when -> setup additional mocks for UserRepository
        Mockito.when(userRepository.findById(Mockito.any())).thenReturn(java.util.Optional.ofNullable(testUser));

        // Method call
        loginService.getUserToLogout(userInputs, testUser.getId());

        // Check if status was changed
        assertEquals(UserStatus.OFFLINE, testUser.getStatus());
    }

    @Test
    public void logoutUser_invalidInputs_notFound(){
        //given -> a user has already been created
        loginService.createUser(testUser);

        User userInputs = new User();
        userInputs.setToken(testUser.getToken());

        //intentionally give false ID
        long falseID = testUser.getId()+1;

        // when -> setup additional mocks for UserRepository
        Mockito.when(userRepository.findById(falseID)).thenReturn(java.util.Optional.empty());

        assertThrows(ResponseStatusException.class, () -> loginService.getUserToLogout(userInputs, falseID));
    }

    @Test
    public void logoutUser_invalidInputs_unauthorized(){

        //given -> a user has already been created
        loginService.createUser(testUser);

        User userInputs = new User();

        // intentionally give false token
        userInputs.setToken("falseToken");

        // when -> setup additional mocks for UserRepository
        Mockito.when(userRepository.findById(Mockito.any())).thenReturn(java.util.Optional.ofNullable(testUser));

        assertThrows(ResponseStatusException.class, () -> loginService.getUserToLogout(userInputs, testUser.getId()));

    }
}
