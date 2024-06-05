package sb.steradian.sbsteradian.service;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import sb.steradian.sbsteradian.entity.User;
import sb.steradian.sbsteradian.entity.UserRole;
import sb.steradian.sbsteradian.repository.UserRoleRepository;

import java.util.*;
import java.util.stream.Collectors;

public class UserDetailsImpl implements UserDetails{

    private static final long serialVersionUID=1L;
    private static final Logger log = LoggerFactory.getLogger(UserDetailsImpl.class);

    private UUID uuid;
    private String username;
    private String email;

    @JsonIgnore
    private String password;

    private Collection<? extends GrantedAuthority> authorities;

    @Autowired
    private UserRoleRepository userRoleRepository;
    

    public UserDetailsImpl(UUID uuid, String username, String email, String password,
            Collection<? extends GrantedAuthority> authorities) {
        this.uuid = uuid;
        this.username = username;
        this.email = email;
        this.password = password;
        this.authorities = authorities;
    }


    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public String getEmail() {
        return email;
    }



    public void setEmail(String email) {
        this.email = email;
    }



    public static UserDetailsImpl build(UserRole userRole){
        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(userRole.getRoleId().getName()));
        return new UserDetailsImpl(userRole.getId(),userRole.getUserId().getUsername(),userRole.getUserId().getEmail(),userRole.getUserId().getPassword(),authorities);
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // TODO Auto-generated method stub
        return authorities;
    }

    @Override
    public String getPassword() {
        // TODO Auto-generated method stub
        return password;
    }

    @Override
    public String getUsername() {
        // TODO Auto-generated method stub
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        // TODO Auto-generated method stub
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        // TODO Auto-generated method stub
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        // TODO Auto-generated method stub
        return true;
    }

    @Override
    public boolean isEnabled() {
        // TODO Auto-generated method stub
        return true;
    }

    @Override
    public boolean equals(Object obj) {
        // TODO Auto-generated method stub
        if(this==obj)
            return true;
        if(obj==null || getClass()!=obj.getClass())
        return false;
        UserDetailsImpl user = (UserDetailsImpl)obj;
        return uuid.equals(user.getUuid());
    }
    
}
