package sb.steradian.sbsteradian.payload.request;

import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;


public class LoginRequest {
    @NotBlank
    @Pattern(regexp = "^[a-z0-9]+$")
    private String username;
    @NotBlank
    @Pattern(regexp = "^(?=.*?[A-Z])(?=.*?[a-z])(?=.*?[0-9]).{8,16}+$")
    private String password;
    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    public LoginRequest(String username, String password) {
        this.username = username;
        this.password = password;
    }


    
}
