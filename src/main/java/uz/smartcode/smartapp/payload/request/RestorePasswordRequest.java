package uz.smartcode.smartapp.payload.request;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
public class RestorePasswordRequest {
    @Email
    private String email;
    @NotNull
    @Size(min = 7)
    private String password;
    @Size(min = 7)
    private String newPassword;
}
