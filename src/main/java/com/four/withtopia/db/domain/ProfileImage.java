package com.four.withtopia.db.domain;

import com.four.withtopia.dto.response.ProfileImageListResponseDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class ProfileImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long imageId;

    @Column(nullable = false)
    private String profileIamge;

    public ProfileImage(String profileIamge) {
        this.profileIamge = profileIamge;
    }
}
