package org.mryrt.file_service.Auth.Model;

// Jakarta persistence
import jakarta.persistence.*;

// Lombok
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Entity
@Data
@RequiredArgsConstructor
public class UserInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(unique = true, nullable = false)
    private String name;

    @Column(nullable = false)
    private String password;
    
    private String roles;
}
