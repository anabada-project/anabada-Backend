package com.example.anabadabackend.account.dto;

import com.example.anabadabackend.entity.User;
import com.example.anabadabackend.entity.enums.Gender;
import com.example.anabadabackend.entity.enums.Specialism;
import lombok.Getter;

@Getter
public class AccountResponse {

    private final String userId;
    private final String email;
    private final String name;
    private final Gender gender;
    private final Specialism specialism;
    private final String generation;

    public AccountResponse(User user) {
        this.userId = user.getUserId();
        this.email = user.getEmail();
        this.name = user.getName();
        this.gender = user.getGender();
        this.specialism = user.getSpecialism();
        this.generation = user.getGeneration();
    }
}
