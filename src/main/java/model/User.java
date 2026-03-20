package model;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class User {
    private Integer id;
    private String username;
    private String passwordHash;
    private String fullName;
    private LocalDateTime createdAt;

}
