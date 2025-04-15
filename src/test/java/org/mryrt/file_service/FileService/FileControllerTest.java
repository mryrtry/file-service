package org.mryrt.file_service.FileService;

import com.fasterxml.jackson.core.json.JsonWriteFeature;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mryrt.file_service.Auth.Model.LogInRequest;
import org.mryrt.file_service.Auth.Model.SignUpRequest;
import org.mryrt.file_service.Auth.Repository.UserRepository;
import org.mryrt.file_service.FileService.Repository.FileMetaRepository;
import org.mryrt.file_service.Utility.Message.Files.FilesErrorMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriUtils;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.mryrt.file_service.Utility.Message.Files.FilesErrorMessage.*;
import static org.springframework.test.util.AssertionErrors.assertNotNull;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class FileControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private FileMetaRepository fileMetaRepository;

    @TempDir
    static Path tempDir;

    private static String token;

    private static long userId;

    @DynamicPropertySource
    static void overrideProperties(DynamicPropertyRegistry registry) {
        registry.add("file.service.upload-dir", () -> tempDir.toString());
    }

    @BeforeAll
    static void setUp(@Autowired MockMvc mockMvc, @Autowired ObjectMapper objectMapper, @Autowired UserRepository userRepository) throws Exception {
        userRepository.deleteAll();
        objectMapper.configure(JsonWriteFeature.ESCAPE_NON_ASCII.mappedFeature(), true);
        token = getTestUserToken(mockMvc, objectMapper);
    }

    @BeforeEach
    void setUp() {
        fileMetaRepository.deleteAll();
    }

    private static String getTestUserToken(MockMvc mockMvc, ObjectMapper objectMapper) throws Exception {
        String user = mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/sign-up")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new SignUpRequest("testUser", "password"))))
                .andReturn()
                .getResponse()
                .getContentAsString();

        userId = JsonPath.parse(user).read("$.id", Long.class);

        return mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/log-in")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new LogInRequest("testUser", "password"))))
                .andReturn()
                .getResponse()
                .getContentAsString();
    }

    private MockMultipartFile getTestFile(String fileName) throws Exception {
        ClassLoader classLoader = FileControllerTest.class.getClassLoader();
        InputStream inputStream = classLoader.getResourceAsStream(fileName);
        return new MockMultipartFile(
                "file",
                fileName,
                null,
                inputStream
        );
    }

    private String extractField(String filename, String field) throws Exception {
        List<Map<String, Object>> filesMeta = new ObjectMapper().readValue(getFilesMeta(), new TypeReference<>() {});
        Map<String, Object> fileMetas = filesMeta.stream()
                .filter(fileMeta -> filename.equals(fileMeta.get("name")))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("File not found in response"));

        String extractedField = (String) fileMetas.get(field);
        assertNotNull(extractedField, "'%s' not found for file: ".formatted(field) + filename);

        return extractedField;
    }

    private void assertFileEquals(byte[] downloadedFile, MultipartFile file) throws Exception {
        byte[] originalFile = file.getBytes();
        assertArrayEquals(originalFile, downloadedFile, "Downloaded file content does not match the original file");
    }

    private String getFilesMeta() throws Exception {
        return mockMvc.perform(MockMvcRequestBuilders.get("/api/files")
                        .header("Authorization", "Bearer %s".formatted(token)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
    }

    private void uploadFile(MockMultipartFile file) throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.multipart("/api/files")
                        .file(file)
                        .header("Authorization", "Bearer %s".formatted(token)))
                .andExpect(status().isOk());
    }

    private void deleteFile(String uuid, String filename) throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/files/%s".formatted(uuid))
                        .header("Authorization", "Bearer %s".formatted(token)))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value(filename));
    }

    private void deleteFileAndExpectBadRequest(String uuid, FilesErrorMessage filesErrorMessage, Object ... args) throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/files/%s".formatted(uuid))
                        .header("Authorization", "Bearer %s".formatted(token)))
                .andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$." + filesErrorMessage.getErrorField()).value(filesErrorMessage.getFormattedMessage(args)));
    }

    private void uploadFileAndExpectBadRequest(MockMultipartFile file, FilesErrorMessage filesErrorMessage, Object ... args) throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.multipart("/api/files")
                        .file(file)
                        .header("Authorization", "Bearer %s".formatted(token)))
                .andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$." + filesErrorMessage.getErrorField()).value(filesErrorMessage.getFormattedMessage(args)));
    }

    private byte[] getFile(String uuid, String filename) throws Exception {
        return mockMvc.perform(MockMvcRequestBuilders.get("/api/files/%s".formatted(uuid))
                        .header("Authorization", "Bearer %s".formatted(token)))
                .andExpect(status().isOk())
                .andExpect(header().exists("Content-Disposition"))
                .andExpect(header().string("Content-Disposition", "attachment; filename=\"" + UriUtils.encode(filename, StandardCharsets.UTF_8) + "\""))
                .andExpect(header().exists("Content-Type"))
                .andExpect(header().string("Content-Type", "application/octet-stream"))
                .andReturn()
                .getResponse()
                .getContentAsByteArray();
    }

    private void getFileAndExpectBadRequest(String uuid, FilesErrorMessage filesErrorMessage, Object ... args) throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/files/%s".formatted(uuid))
                        .header("Authorization", "Bearer %s".formatted(token)))
                .andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$." + filesErrorMessage.getErrorField()).value(filesErrorMessage.getFormattedMessage(args)));
    }

    @Test
    void uploadFile_Success() throws Exception {
        String filename = "Test50MB.pdf";
        MockMultipartFile file = getTestFile(filename);
        uploadFile(file);
        String uuid = extractField(filename, "uuid");
        assertFileEquals(getFile(uuid, filename), file);
    }

    @Test
    void uploadFile_CyrillicName_Success() throws Exception {
        String filename = "ФайлTxt.txt";
        MockMultipartFile file = getTestFile(filename);
        uploadFile(file);
        String uuid = extractField(filename, "uuid");
        assertFileEquals(getFile(uuid, filename), file);
    }

    @Test
    void uploadFile_EmptyFile_ReturnsBadRequest() throws Exception {
        MockMultipartFile emptyFile = new MockMultipartFile(
                "file",
                "empty.txt",
                MediaType.TEXT_PLAIN_VALUE,
                new byte[0]
        );
        uploadFileAndExpectBadRequest(emptyFile, FILE_IS_EMPTY);
    }

    @Test
    void uploadFile_LargeFile_ReturnBadRequest() throws Exception {
        String filename = "Test100MB.pdf";
        MockMultipartFile file = getTestFile(filename);
        uploadFileAndExpectBadRequest(file, FILE_SIZE_TOO_LARGE);
    }

    @Test
    void uploadFile_ExceedsFolderLimit_ReturnsBadRequest() throws Exception {
        String filename = "Test50MB.pdf";
        MockMultipartFile file = getTestFile(filename);
        for (int i = 0; i < 20; i++) uploadFile(file);
        uploadFileAndExpectBadRequest(file, NOT_ENOUGH_SPACE, userId);
    }

    @Test
    void uploadFile_ReturnsBadRequest() throws Exception {
        String filename1 = "Test50MB.pdf";
        String filename2 = "TestTxt.txt";

        MockMultipartFile file1 = getTestFile(filename1);
        MockMultipartFile file2 = getTestFile(filename2);

        mockMvc.perform(MockMvcRequestBuilders.multipart("/api/files")
                        .file(file1)
                        .file(file2)
                        .header("Authorization", "Bearer %s".formatted(token)))
                .andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$." + "file").value(FILES_LIMIT_EXCEEDED.getFormattedMessage()));
    }

    @Test
    void getFile_FileNotFound_ReturnsBadRequest() throws Exception {
        getFileAndExpectBadRequest("invalid-uuid", INVALID_FILE_UUID);
    }

    @Test
    void deleteFile_Success() throws Exception {
        String filename = "Test50MB.pdf";
        MockMultipartFile file = getTestFile(filename);
        uploadFile(file);
        String uuid = extractField(filename, "uuid");
        deleteFile(uuid, filename);
        getFileAndExpectBadRequest(uuid, UUID_NOT_EXIST, uuid, userId);
    }

    @Test
    void deleteFile_FileNotFound_ReturnsBadRequest() throws Exception {
        String filename = "Test50MB.pdf";
        MockMultipartFile file = getTestFile(filename);
        uploadFile(file);
        String uuid = extractField(filename, "uuid");
        deleteFile(uuid, filename);
        deleteFileAndExpectBadRequest(uuid, UUID_NOT_EXIST, uuid, userId);
    }

    @Test
    void deleteFile_NonExistentUuid_ReturnsBadRequest() throws Exception {
        String uuid = "128b7b5a-e573-317b-8c0b-371a40e4a21e";
        deleteFileAndExpectBadRequest(uuid, UUID_NOT_EXIST, uuid, userId);
    }

    @Test
    void deleteFile_InvalidUuid_ReturnsBadRequest() throws Exception {
        deleteFileAndExpectBadRequest("invalid-uuid", INVALID_FILE_UUID);
    }

    @Test
    void getFilesMeta_WhenNoFilesUploaded_ReturnsEmptyList() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/files")
                        .header("Authorization", "Bearer %s".formatted(token)))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$").isEmpty());
    }

    @Test
    void getFiles_AsUnauthorizedUser_ReturnsUnauthorized() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/files"))
                .andExpect(status().isUnauthorized());
    }

}
