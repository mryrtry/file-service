package org.mryrt.file_service.Auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mryrt.file_service.Auth.Model.LogInRequest;
import org.mryrt.file_service.Auth.Model.SignUpRequest;
import org.mryrt.file_service.Auth.Repository.UserRepository;
import org.mryrt.file_service.FileService.Repository.FileMetaRepository;
import org.mryrt.file_service.Utility.Message.Auth.AuthErrorMessage;
import org.mryrt.file_service.Utility.TestJwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.function.Function;

import static org.mryrt.file_service.Utility.Message.Auth.AuthErrorMessage.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Integration tests for {@link org.mryrt.file_service.Auth.Controller.UserController}.
 * <p>
 * This test class verifies the authentication functionality including user sign-up, login,
 * and token-based access control. It covers both success scenarios and various error cases
 * including validation failures, security checks, and edge cases.
 *
 * <p>The tests use Spring's {@link MockMvc} to simulate HTTP requests and validate responses.
 *
 * <p>Key test categories include:
 * <ul>
 *   <li>User registration (sign-up) with valid and invalid inputs</li>
 *   <li>User login (log-in) with valid and invalid credentials</li>
 *   <li>JWT token validation and access control</li>
 *   <li>Input validation for usernames and passwords</li>
 *   <li>Error handling for malformed requests</li>
 * </ul>
 *
 * <p>The test environment is reset before each test using {@link BeforeEach} to ensure
 * clean database state for each test case.
 */
@SpringBootTest
@AutoConfigureMockMvc
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FileMetaRepository fileMetaRepository;

    @Autowired
    private TestJwtService testJwtService;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
        fileMetaRepository.deleteAll();
    }

    private String createInvalidJson() {
        return "{ \"username\": \"testUser\", }";
    }

    private SignUpRequest createSignUpRequest(String username, String password) {
        SignUpRequest request = new SignUpRequest();
        request.setUsername(username);
        request.setPassword(password);
        return request;
    }

    private LogInRequest createLogInRequest(String username, String password) {
        LogInRequest request = new LogInRequest();
        request.setUsername(username);
        request.setPassword(password);
        return request;
    }

    private void createUser(String username, String password) throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/sign-up")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createSignUpRequest(username, password))))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.username").value(username))
                .andDo(print());
    }

    private String loginAndGetToken(String username, String password) throws Exception {
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/log-in")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createLogInRequest(username, password))))
                .andExpect(status().isOk())
                .andReturn();
        return result.getResponse().getContentAsString();
    }

    private void checkUserCreated(String username, String password) throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/files")
                        .header("Authorization", "Bearer " + loginAndGetToken(username, password)))
                .andExpect(status().isOk())
                .andDo(print());
    }

    private void checkTokenIsValid(String token) throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/files")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andDo(print());
    }

    private void performSignUpAndExpectBadRequest(String username, String password, String field, AuthErrorMessage authErrorMessage, Object ... args) throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/sign-up")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createSignUpRequest(username, password))))
                .andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$." + field).value(authErrorMessage.getFormattedMessage(args)))
                .andDo(print());
    }

    private void performLogInAndExpectBadRequest(String username, String password, String field, AuthErrorMessage authErrorMessage, Object ... args) throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/log-in")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createLogInRequest(username, password))))
                .andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$." + field).value(authErrorMessage.getFormattedMessage(args)))
                .andDo(print());
    }

    private void performAccessTest(String username, Function<String, String> tokenGenerator, AuthErrorMessage authErrorMessage, Object ... args) throws Exception {
        createUser("testUser", "password123");
        String token = tokenGenerator.apply(username);
        mockMvc.perform(MockMvcRequestBuilders.get("/api/files")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isUnauthorized())
                .andExpect(MockMvcResultMatchers.jsonPath("$.error").value(authErrorMessage.getFormattedMessage(args)))
                .andDo(print());
    }

    /**
     * Tests successful user registration with valid credentials.
     * @throws Exception if the test fails
     */
    @Test
    void signUp_Success() throws Exception {
        createUser("testUser", "password123");
    }

    /**
     * Tests that duplicate usernames are rejected during registration.
     * @throws Exception if the test fails
     */
    @Test
    void signUp_DuplicateUsername() throws Exception {
        createUser("testUser", "password123");
        performSignUpAndExpectBadRequest("testUser", "password123", "username", USERNAME_ALREADY_EXISTS, "testUser");
    }

    /**
     * Tests registration with minimum valid username length (2 characters).
     * @throws Exception if the test fails
     */
    @Test
    void signUp_MinimumValidUsername() throws Exception {
        createUser("ab", "password123");
        checkUserCreated("ab", "password123");
    }

    /**
     * Tests registration with maximum valid username length (30 characters).
     * @throws Exception if the test fails
     */
    @Test
    void signUp_MaximumValidUsername() throws Exception {
        String longUsername = "a".repeat(30);
        createUser(longUsername, "password123");
        checkUserCreated(longUsername, "password123");
    }

    /**
     * Tests that overly long usernames (>30 chars) are rejected.
     * @throws Exception if the test fails
     */
    @Test
    void signUp_TooLongUsername() throws Exception {
        String longUsername = "a".repeat(50);
        performSignUpAndExpectBadRequest(longUsername, "password123", "username", USERNAME_LENGTH);
    }

    /**
     * Tests that usernames with leading/trailing spaces are rejected.
     * @throws Exception if the test fails
     */
    @Test
    void signUp_UsernameWithLeadingTrailingSpaces() throws Exception {
        performSignUpAndExpectBadRequest("  testUser  ", "password123", "username", USERNAME_INVALID_CHARS);
    }

    /**
     * Tests that blank usernames are rejected.
     * @throws Exception if the test fails
     */
    @Test
    void signUp_BlankUsername() throws Exception {
        performSignUpAndExpectBadRequest("", "password123", "username", USERNAME_REQUIRED);
    }

    /**
     * Tests that usernames with invalid characters are rejected.
     * @throws Exception if the test fails
     */
    @Test
    void signUp_InvalidUsername() throws Exception {
        performSignUpAndExpectBadRequest("username#", "password123", "username", USERNAME_INVALID_CHARS);
    }

    /**
     * Tests that null usernames are rejected.
     * @throws Exception if the test fails
     */
    @Test
    void signUp_NoUsername() throws Exception {
        performSignUpAndExpectBadRequest(null, "password123", "username", USERNAME_REQUIRED);
    }

    /**
     * Tests registration with minimum valid password length (5 characters).
     * @throws Exception if the test fails
     */
    @Test
    void signUp_MinimumPasswordLength() throws Exception {
        createUser("testUser", "12345");
        checkUserCreated("testUser", "12345");
    }

    /**
     * Tests registration with password containing special characters.
     * @throws Exception if the test fails
     */
    @Test
    void signUp_ValidSpecialCharacterPassword() throws Exception {
        createUser("testUser", "pass@word123");
        checkUserCreated("testUser", "pass@word123");
    }

    /**
     * Tests that passwords shorter than 5 characters are rejected.
     * @throws Exception if the test fails
     */
    @Test
    void signUp_TooShortPassword() throws Exception {
        performSignUpAndExpectBadRequest("testUser", "1234", "password", PASSWORD_TOO_SHORT);
    }

    /**
     * Tests handling of empty request body during registration.
     * @throws Exception if the test fails
     */
    @Test
    void signUp_EmptyRequestBody() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/sign-up")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.username").value(USERNAME_REQUIRED.getFormattedMessage()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.password").value(PASSWORD_REQUIRED.getFormattedMessage()))
                .andDo(print());
    }

    /**
     * Tests handling of malformed JSON during registration.
     * @throws Exception if the test fails
     */
    @Test
    void signUp_InvalidRequest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/sign-up")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createInvalidJson()))
                .andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.params").value(INVALID_JSON.getFormattedMessage()))
                .andDo(print());
    }

    /**
     * Tests successful login with valid credentials.
     * @throws Exception if the test fails
     */
    @Test
    void logIn_Success() throws Exception {
        createUser("testUser", "password123");
        checkTokenIsValid(loginAndGetToken("testUser", "password123"));
    }

    /**
     * Tests login attempt with non-existent username.
     * @throws Exception if the test fails
     */
    @Test
    void logIn_NonExistentUser() throws Exception {
        performLogInAndExpectBadRequest("nonexistentUser", "password123", "username", USERNAME_NOT_FOUND, "nonexistentUser");
    }

    /**
     * Tests login with invalid username format.
     * @throws Exception if the test fails
     */
    @Test
    void logIn_InvalidUsername() throws Exception {
        createUser("testUser", "password123");
        performLogInAndExpectBadRequest("#invalid", "password123", "username", USERNAME_INVALID_CHARS);
    }

    /**
     * Tests login with incorrect password.
     * @throws Exception if the test fails
     */
    @Test
    void logIn_WrongPassword() throws Exception {
        createUser("testUser", "password123");
        performLogInAndExpectBadRequest("testUser", "wrongPassword", "password", WRONG_PASSWORD);
    }

    /**
     * Tests login with null password.
     * @throws Exception if the test fails
     */
    @Test
    void logIn_NoPassword() throws Exception {
        performLogInAndExpectBadRequest("testUser", null, "password", PASSWORD_REQUIRED);
    }

    /**
     * Tests login with blank password.
     * @throws Exception if the test fails
     */
    @Test
    void logIn_BlankPassword() throws Exception {
        performLogInAndExpectBadRequest("testUser", "", "password", PASSWORD_REQUIRED);
    }

    /**
     * Tests handling of malformed JSON during login.
     * @throws Exception if the test fails
     */
    @Test
    void logIn_InvalidRequest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/log-in")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createInvalidJson()))
                .andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.params").value(INVALID_JSON.getFormattedMessage()))
                .andDo(print());
    }

    /**
     * Tests access attempt without authorization header.
     * @throws Exception if the test fails
     */
    @Test
    void access_MissingAuthorizationHeader() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/files"))
                .andExpect(status().isUnauthorized())
                .andExpect(MockMvcResultMatchers.jsonPath("$.error").value(MISSING_AUTH_HEADER.getFormattedMessage()))
                .andDo(print());
    }

    /**
     * Tests access with expired JWT token.
     * @throws Exception if the test fails
     */
    @Test
    void access_ExpiredJWT() throws Exception {
        performAccessTest("testUser", testJwtService::createExpiredToken, EXPIRED_TOKEN);
    }

    /**
     * Tests access with JWT token signed with wrong secret.
     * @throws Exception if the test fails
     */
    @Test
    void access_WrongSecretJWT() throws Exception {
        performAccessTest("testUser", testJwtService::createWrongSecretToken, TOKEN_SIGNATURE_MISMATCH);
    }

    /**
     * Tests access with JWT token having future issued-at time.
     * @throws Exception if the test fails
     */
    @Test
    void access_FutureIssuedAtJWT() throws Exception {
        performAccessTest("testUser", testJwtService::createTokenWithFutureIssuedAt, FUTURE_ISSUED_AT_TOKEN);
    }

    /**
     * Tests access with JWT token having wrong issuer claim.
     * @throws Exception if the test fails
     */
    @Test
    void access_WrongIssuerJWT() throws Exception {
        performAccessTest("testUser", testJwtService::createTokenWithIssuer, INVALID_TOKEN_CLAIM);
    }

    /**
     * Tests access with JWT token missing subject claim.
     * @throws Exception if the test fails
     */
    @Test
    void access_WithoutSubjectJWT() throws Exception {
        performAccessTest("testUser", testJwtService::createWithoutSubjectToken, TOKEN_EXTRACTION_ERROR);
    }

    /**
     * Tests access with JWT token containing non-existent username.
     * @throws Exception if the test fails
     */
    @Test
    void access_InvalidUsernameInJWT() throws Exception {
        performAccessTest("nonexistentUser", testJwtService::createToken, USERNAME_NOT_FOUND, "nonexistentUser");
    }

    /**
     * Tests access with malformed authorization header (missing "Bearer" prefix).
     * @throws Exception if the test fails
     */
    @Test
    void access_NotBearerJWT() throws Exception {
        createUser("testUser", "password123");
        String token = loginAndGetToken("testUser", "password123");
        mockMvc.perform(MockMvcRequestBuilders.get("/api/files")
                        .header("Authorization", token))
                .andExpect(status().isUnauthorized())
                .andExpect(MockMvcResultMatchers.jsonPath("$.error").value(INVALID_AUTH_HEADER_FORMAT.getFormattedMessage()))
                .andDo(print());
    }

    /**
     * Tests access with empty JWT token.
     * @throws Exception if the test fails
     */
    @Test
    void access_EmptyJWT() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/files")
                        .header("Authorization", "Bearer "))
                .andExpect(status().isUnauthorized())
                .andExpect(MockMvcResultMatchers.jsonPath("$.error").value(EMPTY_TOKEN.getFormattedMessage()))
                .andDo(print());
    }

}