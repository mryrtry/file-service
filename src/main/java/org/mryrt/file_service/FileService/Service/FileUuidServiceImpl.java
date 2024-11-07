package org.mryrt.file_service.FileService.Service;

// Pair
import org.springframework.data.util.Pair;

// Spring annotations
import org.springframework.stereotype.Service;

// Java time
import java.time.Instant;

@Service
public class FileUuidServiceImpl implements FileUuidService {

    @Override
    public String generateUuid(String fileName, Instant createDate) {
        String fileHash = Integer.toString(fileName.hashCode());
        // String createMills = Long.toString(createDate.toEpochMilli());
        return Long.toHexString(Long.parseLong(fileHash ));
    }
}
