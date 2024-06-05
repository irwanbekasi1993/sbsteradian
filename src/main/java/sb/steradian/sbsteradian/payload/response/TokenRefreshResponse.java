package sb.steradian.sbsteradian.payload.response;

import java.util.*;

import sb.steradian.sbsteradian.entity.Role;

public class TokenRefreshResponse {
    private String accessToken;
    private String refreshToken;
    private String tokenType="Bearer ";
    private String username;
    private List<Role> roles;
    public TokenRefreshResponse(String accessToken, String refreshToken, String username, List<Role> roles) {
        this.refreshToken = refreshToken;
        this.accessToken = accessToken;
        this.username=username;
        this.roles=roles;
    }
    
    

    public String getUsername() {
        return username;
    }



    public void setUsername(String username) {
        this.username = username;
    }

    


    public String getAccessToken() {
        return accessToken;
    }
    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }
    public String getRefreshToken() {
        return refreshToken;
    }
    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }
    public String getTokenType() {
        return tokenType;
    }
    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }



	public List<Role> getRoles() {
		return roles;
	}



	public void setRoles(List<Role> roles) {
		this.roles = roles;
	}


    
}
