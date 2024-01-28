package ru.practicum.ewm.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;

@Getter
@Setter
public class NewUserRequest {
    @NotBlank
    @Email
    @Min(6)
    @Max(254)
    private String email;
    @NotBlank
    @Min(2)
    @Max(250)
    private String name;
}
