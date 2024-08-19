package com.deployblitz.backend.domain.entities;

import com.deployblitz.backend.domain.dto.request.UserRegisterRequestDto;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.util.Calendar;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
@Entity(name = "user")
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false, unique = true)
    private String token;

    @Column(columnDefinition = "boolean default true")
    private Boolean status = true;

    @CreatedDate
    private Calendar createdAt = Calendar.getInstance();

    @LastModifiedDate
    private Calendar updatedAt;

    public UserEntity(UserRegisterRequestDto userRegisterRequestDto) {
        this.username = userRegisterRequestDto.username();
        this.token = userRegisterRequestDto.token();
    }


}
