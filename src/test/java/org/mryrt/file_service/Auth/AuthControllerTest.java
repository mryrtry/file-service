package org.mryrt.file_service.Auth;

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

    private void _prepareUser() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/sign-up")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(signUpRequest)))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.username").value("testUser"))
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
                .andExpect(MockMvcResultMatchers.jsonPath("$.username").value("Username: testUser is already in use"))
                .andDo(print());
    }

    @Test
    void testLogIn_Success() throws Exception {
        _prepareUser();

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/log-in")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(logInRequest)))
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn();

        mockMvc.perform(MockMvcRequestBuilders.get("/api/files/get/all")
                .header("Authorization", "Bearer " + result.getResponse().getContentAsString()))
                .andExpect(status().isOk())
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