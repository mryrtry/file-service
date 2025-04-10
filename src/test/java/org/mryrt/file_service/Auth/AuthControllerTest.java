package org.mryrt.file_service.Auth;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mryrt.file_service.Auth.Model.LogInRequest;
import org.mryrt.file_service.Auth.Model.SignUpRequest;
import org.mryrt.file_service.Auth.Repository.UserRepository;
import org.mryrt.file_service.FileService.Repository.FileMetaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    UserRepository userRepository;

    @Autowired
    FileMetaRepository fileMetaRepository;

    @Autowired
    TestJwtService testJwtService;

    private SignUpRequest signUpRequest;
    private LogInRequest logInRequest;

    @BeforeEach
    void setUp() {
        signUpRequest = new SignUpRequest();
        signUpRequest.setUsername("testUser");
        signUpRequest.setPassword("password123");

        logInRequest = new LogInRequest();
        logInRequest.setUsername("testUser");
        logInRequest.setPassword("password123");

        userRepository.deleteAll();
        fileMetaRepository.deleteAll();
    }

    private String _getInvalidJSON() {
        return "{ \"username\": \"testUser\", }";
    }

    private SignUpRequest _getSignUpRequest(String username, String password) throws Exception {
        SignUpRequest signUpRequest = new SignUpRequest();
        signUpRequest.setUsername(username);
        signUpRequest.setPassword(password);
        return signUpRequest;
    }

    private LogInRequest _getLogInRequest(String username, String password) throws Exception {
        LogInRequest logInRequest = new LogInRequest();
        logInRequest.setUsername(username);
        logInRequest.setPassword(password);
        return logInRequest;
    }

    private void _prepareUser() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/sign-up")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(signUpRequest)))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.username").value("testUser"))
                .andDo(print());
    }

    private void _prepareUser(String username, String password) throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/sign-up")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(_getSignUpRequest(username, password))))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.username").value(username))
                .andDo(print());
    }

    private void _checkUserCreated(String username, String password) throws Exception {
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/log-in")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(_getLogInRequest(username, password))))
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn();

        mockMvc.perform(MockMvcRequestBuilders.get("/api/files/get/all")
                        .header("Authorization", "Bearer " + result.getResponse().getContentAsString()))
                .andExpect(status().isOk())
                .andDo(print());
    }

    private String _getPreparedToken() throws Exception {
        _prepareUser();

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/log-in")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(logInRequest)))
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn();

        return result.getResponse().getContentAsString();
    }

    @Test
    void testSignUp_Success() throws Exception {
        _prepareUser();
    }

    @Test
    void testSignUp_DuplicateUsername() throws Exception {
        _prepareUser();

        mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/sign-up")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signUpRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.username").value("Username testUser is already in use"))
                .andDo(print());
    }

    @Test
    void testSignUp_MinimumValidUsername() throws Exception {
        _prepareUser("ab", "password");
        _checkUserCreated("ab", "password");
    }

    // todo: надо максимум минимум длин вынести в проперти файлы чтоб оно везде подтягивалось
    @Test
    void testSignUp_MaximumValidUsername() throws Exception {
        String longUsername = "a".repeat(30);
        _prepareUser(longUsername, "password");
        _checkUserCreated(longUsername, "password");
    }

    @Test
    void testSignUp_UsernameWithLeadingTrailingSpaces() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/sign-up")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(_getSignUpRequest("  testUser  ", "password"))))
                .andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.username").value("Username can only contain letters, numbers, and underscores"))
                .andDo(print());
    }

    @Test
    void testSignUp_TooLongUsername() throws Exception {
        String longUsername = "a".repeat(50);
        mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/sign-up")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(_getSignUpRequest(longUsername, "password"))))
                .andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.username").value("Username must be between 2 and 30 characters"))
                .andDo(print());
    }

    @Test
    void testSignUp_BlankUsername() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/sign-up")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(_getSignUpRequest("", "password"))))
                .andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.username").value("Username is required"))
                .andDo(print());
    }

    @Test
    void testSignUp_NoUsername() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/sign-up")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(_getSignUpRequest(null, "password"))))
                .andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.username").value("Username is required"))
                .andDo(print());
    }

    @Test
    void testSignUp_InvalidUsername() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/sign-up")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(_getSignUpRequest("username#", "password"))))
                .andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.username").value("Username can only contain letters, numbers, and underscores"))
                .andDo(print());
    }

    @Test
    void testSignUp_MinimumPasswordLength() throws Exception {
        _prepareUser("testUser", "12345");
        _checkUserCreated("testUser", "12345");
    }

    @Test
    void testSignUp_ValidSpecialCharacterPassword() throws Exception {
        _prepareUser(signUpRequest.getUsername(), "pass@word123");
        _checkUserCreated(signUpRequest.getUsername(), "pass@word123");
    }

    @Test
    void testSignUp_TooShortPassword() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/sign-up")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(_getSignUpRequest("testUser", "1234"))))
                .andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.password").value("Password must be longer than 5 characters"))
                .andDo(print());
    }

    @Test
    void testSignUp_InvalidRequest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/sign-up")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(_getInvalidJSON()))
                .andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.params").value("Invalid JSON format"))
                .andDo(print());
    }

    @Test
    void testSignUp_EmptyRequestBody() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/sign-up")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.username").value("Username is required"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.password").value("Password is required"))
                .andDo(print());
    }

    @Test
    void testLogIn_Success() throws Exception {
        String token = _getPreparedToken();

        mockMvc.perform(MockMvcRequestBuilders.get("/api/files/get/all")
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    void testLogIn_NonExistUsername() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/log-in")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(_getLogInRequest("user", "password"))))
                .andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.username").value("User user wasn't found"))
                .andDo(print());
    }

    @Test
    void testLogIn_InvalidUsername() throws Exception {
        _prepareUser();

        mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/log-in")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(_getLogInRequest("#2214", signUpRequest.getPassword()))))
                .andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.username").value("Username can only contain letters, numbers, and underscores"))
                .andDo(print());
    }

    @Test
    void testLogIn_WrongPassword() throws Exception {
        _prepareUser();

        mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/log-in")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(_getLogInRequest(signUpRequest.getUsername(), signUpRequest.getPassword() + "#"))))
                .andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.password").value("Wrong password"))
                .andDo(print());
    }

    @Test
    void testLogIn_NoPassword() throws Exception {
        _prepareUser();

        mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/log-in")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(_getLogInRequest(signUpRequest.getUsername(), null))))
                .andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.password").value("Password is required"))
                .andDo(print());
    }

    @Test
    void testLogIn_BlankPassword() throws Exception {
        _prepareUser();

        mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/log-in")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(_getLogInRequest(signUpRequest.getUsername(), ""))))
                .andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.password").value("Password is required"))
                .andDo(print());
    }

    @Test
    void testLogIn_InvalidRequest() throws Exception {
        _prepareUser();

        mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/log-in")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ \"username\": \"testUser\", }"))
                .andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.params").value("Invalid JSON format"))
                .andDo(print());
    }

    @Test
    void testAccess_NotBearerJWT() throws Exception {
        String token = _getPreparedToken();

        mockMvc.perform(MockMvcRequestBuilders.get("/api/files/get/all").header("Authorization", token)
                .header("Authorization", token))
                .andExpect(status().isUnauthorized())
                .andExpect(MockMvcResultMatchers.jsonPath("$.error").value("Authorization token doesn't start with 'Bearer '"))
                .andDo(print());
    }

    @Test
    void testAccess_InvalidUsernameInJWT() throws Exception {
        String invalidToken = testJwtService.createToken("nonexistentUser");

        mockMvc.perform(MockMvcRequestBuilders.get("/api/files/get/all")
                        .header("Authorization", "Bearer " + invalidToken))
                .andExpect(status().isUnauthorized())
                .andExpect(MockMvcResultMatchers.jsonPath("$.error").value("User nonexistentUser wasn't found"))
                .andDo(print());
    }

    @Test
    void testAccess_NullAuthorization() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/files/get/all"))
                .andExpect(status().isUnauthorized())
                .andExpect(MockMvcResultMatchers.jsonPath("$.error").value("Missing authorization header"))
                .andDo(print());
    }

    @Test
    void testAccess_EmptyJWT() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/files/get/all")
                        .header("Authorization", "Bearer "))
                .andExpect(status().isUnauthorized())
                .andExpect(MockMvcResultMatchers.jsonPath("$.error").value("Token is empty"))
                .andDo(print());
    }

    @Test
    void testAccess_ExpiredJWT() throws Exception {
        _prepareUser();

        String expiredToken = testJwtService.createToken(logInRequest.getUsername(), 0);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/files/get/all")
                .header("Authorization", "Bearer " + expiredToken))
                .andExpect(status().isUnauthorized())
                .andExpect(MockMvcResultMatchers.jsonPath("$.error").value("Expired token"))
                .andDo(print());
    }

    @Test
    void testAccess_FakeJWT() throws Exception {
        String fakeToken = testJwtService.createFakeToken(logInRequest.getUsername());

        mockMvc.perform(MockMvcRequestBuilders.get("/api/files/get/all")
                .header("Authorization", "Bearer " + fakeToken))
                .andExpect(status().isUnauthorized())
                .andExpect(MockMvcResultMatchers.jsonPath("$.error").value("Token signature mismatch"))
                .andDo(print());
    }

}