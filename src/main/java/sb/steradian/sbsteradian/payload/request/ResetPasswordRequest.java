package sb.steradian.sbsteradian.payload.request;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.util.UUID;

public class ResetPasswordRequest {
    @NotBlank
    @Pattern(regexp = "^[a-z0-9]+$")
private String username;
    @Email
private String email;
    @NotBlank
    @Pattern(regexp = "^(?=.*?[A-Z])(?=.*?[a-z])(?=.*?[0-9]).{8,16}+$")
private String newPassword;
private UUID id;

public ResetPasswordRequest() {
}
public ResetPasswordRequest(String username, String newPassword, String email, UUID id) {
    this.username = username;
    this.newPassword = newPassword;
    this.email=email;
    this.id=id;
}



public String getUsername() {
    return username;
}
public void setUsername(String username) {
    this.username = username;
}
public String getNewPassword() {
    return newPassword;
}
public void setNewPassword(String newPassword) {
    this.newPassword = newPassword;
}
public String getEmail() {
    return email;
}
public void setEmail(String email) {
    this.email = email;
}

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }
}
