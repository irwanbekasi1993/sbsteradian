package sb.steradian.sbsteradian.payload.response;


import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.util.List;
import java.util.UUID;

public class UserResponse {

    private UUID id;
    @NotBlank
    @Pattern(regexp = "^[a-z0-9]+$")
    private String username;
    @Email
    private String email;
    private String phoneNumber;
    private String name;
    private String nickname;
    private String status;
    private RoleResponse roleResponse;

    public RoleResponse getRoleResponse() {
        return roleResponse;
    }

    public void setRoleResponse(RoleResponse roleResponse) {
        this.roleResponse = roleResponse;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

}
