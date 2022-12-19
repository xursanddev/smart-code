package uz.smartcode.smartapp.payload.request;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
public class VerifyEmailRequest {
    @NotNull
    @Email
    private String email;
    @Size(min = 6, max = 6)
    private String code;
}
