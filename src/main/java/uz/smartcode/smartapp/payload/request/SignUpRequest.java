package uz.smartcode.smartapp.payload.request;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
public class SignUpRequest {
    @NotNull
    private String firstname;
    private String lastname;
    @NotNull
    @Size(min = 4, max = 60)
    private String username;
    @NotNull
    @Email
    private String email;
    @NotNull
    @Size(min = 7)
    private String password;
}
