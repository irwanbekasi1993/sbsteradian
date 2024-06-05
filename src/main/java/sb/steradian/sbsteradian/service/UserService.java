package sb.steradian.sbsteradian.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import sb.steradian.sbsteradian.entity.*;
import sb.steradian.sbsteradian.payload.request.*;
import sb.steradian.sbsteradian.payload.response.*;
import sb.steradian.sbsteradian.repository.RedisUserRepository;
import sb.steradian.sbsteradian.repository.RoleRepository;
import sb.steradian.sbsteradian.repository.UserRepository;
import sb.steradian.sbsteradian.repository.UserRoleRepository;
import sb.steradian.sbsteradian.util.JwtTokenUtil;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.net.Inet4Address;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class UserService {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder encoder;

    @Autowired
    private UserRoleRepository userRoleRepository;

    @Autowired
    private JwtTokenUtil jwtUtils;

    @Autowired
    private RefreshTokenService refreshTokenService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private RedisUserRepository redisUserRepository;


    public MessageResponse signIn(LoginRequest loginRequest) {
        MessageResponse messageResponse = new MessageResponse();

        if (userRepository.existsByUsername(loginRequest.getUsername()) == false) {
            messageResponse.setData("user not found");
        }

        if (userRepository.existsByUsername(loginRequest.getUsername()) == true) {
            Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

            SecurityContextHolder.getContext().setAuthentication(authentication);


            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
            String jwt = jwtUtils.generateJwtToken(userDetails);
            List<String> roles = userDetails.getAuthorities().stream().map(item -> item.getAuthority()).collect(Collectors.toList());

            RefreshToken refreshToken = refreshTokenService.createRefreshToken(userDetails.getUsername());
            JwtResponse response = new JwtResponse(jwt, refreshToken.getToken(), userDetails.getUsername(), userDetails.getEmail(), roles);

            TokenRefreshRequest requestToken = new TokenRefreshRequest();
            requestToken.setRefreshToken(response.getToken());

            refreshTokenService.findByToken(requestToken.getRefreshToken()).map(refreshTokenService::verifyExpiration);
            User getUser = refreshToken.getUser();
//        if(getUser!=null){
//            kafkaProcessing("signin using username: "+getUser.getUsername());
//        }
            String obtainToken = jwtUtils.generateTokenFromUsername(getUser.getUsername());
            TokenRefreshResponse tokenResponse = new TokenRefreshResponse(obtainToken, requestToken.getRefreshToken(), getUser.getUsername(), roleRepository.getAllRolesByUserName(getUser.getUsername()));
            messageResponse.setData(tokenResponse);
        }
        return messageResponse;
    }

    public MessageResponse viewUserPassword(UUID id) {
        MessageResponse messageResponse = new MessageResponse();
        boolean flag = redisUserRepository.findById(id).isPresent();
        if (flag==true) {
            RedisUser redisUser = redisUserRepository.findById(id).get();
            messageResponse.setData(redisUser.getPassword());
        }
        if (flag==false) {
            messageResponse.setData("user not found");
        }
        return messageResponse;
    }

    public MessageResponse insertUser(UserRequest user) {
        MessageResponse messageResponse = new MessageResponse();
        UserResponse userResponse = new UserResponse();
        RedisUser redisUser = new RedisUser();
        boolean cekUser = false;
        boolean cekRole = false;
        User objUser = new User();
        Role objRole = new Role();
        UserRole objUserRole = new UserRole();
        List<RoleResponse> roleResponses = new ArrayList<>();
        List<UserRole> userRoles = new ArrayList<>();

        if (Objects.isNull(user)) {
            messageResponse.setData("please fill the field");
        }
        cekUser = userRepository.findByUsername(user.getUsername()).isPresent();

        if (cekUser == false) {
            objUser.setUsername(user.getUsername());
            objUser.setName(user.getName());
            objUser.setNickname(user.getNickname());
            objUser.setEmail(user.getEmail());
            objUser.setPhoneNumber(user.getPhoneNumber());
            objUser.setPassword(encoder.encode(user.getPassword()));
            objUser.setStatus("created");
            try {
                objUser.setCreateIp(Inet4Address.getLocalHost().getHostAddress());
            } catch (Exception e) {
                messageResponse.setData(e.getMessage());
            }
            userRepository.save(objUser);

            redisUser.setId(objUser.getId());
            redisUser.setUsername(objUser.getUsername());
            redisUser.setName(objUser.getName());
            redisUser.setNickname(objUser.getNickname());
            redisUser.setEmail(objUser.getEmail());
            redisUser.setPhoneNumber(objUser.getPhoneNumber());
            redisUser.setPassword(user.getPassword());
            redisUser.setStatus("created");
            redisUserRepository.save(redisUser);
        }

            cekRole = roleRepository.findByName(user.getRoleRequest().getName()).isPresent();
            if(cekRole==true){
                objRole = roleRepository.findByName(user.getRoleRequest().getName()).get();
                UserRole ur = userRoleRepository
                        .getByUserIdAndRoleId(objUser.getId(), objRole.getId());
                if (Objects.isNull(ur)) {
                    ur = new UserRole();
                    ur.setUserId(objUser);
                    ur.setRoleId(objRole);
                    userRoleRepository.save(ur);
                    userRoles.add(ur);
                }
            }
            if (cekRole == false) {
                objRole = new Role();
                objRole.setName(user.getRoleRequest().getName());
                objRole.setDescription(user.getRoleRequest().getDescription());
                objRole.setStatus("active");
                roleRepository.save(objRole);

                UserRole ur = userRoleRepository
                        .getByUserIdAndRoleId(objUser.getId(), objRole.getId());
                if (Objects.isNull(ur)) {
                    ur = new UserRole();
                    ur.setUserId(objUser);
                    ur.setRoleId(objRole);
                    userRoleRepository.save(ur);
                    userRoles.add(ur);
                }
            }

        RoleResponse roleResponse = new RoleResponse();
        roleResponse.setId(objRole.getId());
        roleResponse.setName(objRole.getName());
        roleResponse.setDescription(objRole.getDescription());
        roleResponse.setStatus(objRole.getStatus());
        roleResponses.add(roleResponse);

        userResponse.setId(objUser.getId());
        userResponse.setUsername(objUser.getUsername());
        userResponse.setEmail(objUser.getEmail());
        userResponse.setPhoneNumber(objUser.getPhoneNumber());
        userResponse.setName(objUser.getName());
        userResponse.setNickname(objUser.getNickname());
        userResponse.setStatus(objUser.getStatus());
        userResponse.setRoleResponse(roleResponse);
        messageResponse.setData(userResponse);

        return messageResponse;
    }

    public MessageResponse updateUser(UserRequest user, UUID id) {
        MessageResponse messageResponse = new MessageResponse();
        UserResponse userResponse = new UserResponse();
        boolean cekUser = false;
        boolean cekRole = false;
        Role objRole = new Role();
        UserRole objUserRole = new UserRole();

        cekUser = userRepository.findById(id).isPresent();

        User objUser = new User();

        if (cekUser == true) {
            objUser = userRepository.findById(id).get();
            objUser.setUsername(user.getUsername());
            objUser.setName(user.getName());
            objUser.setNickname(user.getNickname());
            objUser.setEmail(user.getEmail());
            objUser.setPhoneNumber(user.getPhoneNumber());
            objUser.setUpdatedBy(user.getUsername());
            objUser.setStatus("updated");

            try {
                objUser.setUpdateIp(Inet4Address.getLocalHost().getHostAddress());
            } catch (Exception e) {
                messageResponse.setData(e.getMessage());
            }
            userRepository
                    .updateUser(objUser.getUsername(),
                            objUser.getName(),
                            objUser.getNickname(),
                            objUser.getEmail(),
                            objUser.getPhoneNumber(), id);

        }
        if (cekUser == false) {
            messageResponse.setData("user not found");
        }

            objRole = roleRepository.findByName(user.getRoleRequest().getName()).get();
            if (Objects.nonNull(objRole)) {
                roleRepository.updateByName(user.getRoleRequest().getName(),
                        objRole.getDescription(), objRole.getName());
            }
            cekRole = roleRepository.findByName(user.getRoleRequest().getName()).isPresent();
            if (cekRole==false) {
                objRole = new Role();
                objRole.setName(user.getRoleRequest().getName());
                objRole.setDescription(user.getRoleRequest().getDescription());
                roleRepository.save(objRole);
            }

            UserRole getUserRole = userRoleRepository
                    .getByUsernameAndRoleName(objUser.getUsername(), objRole.getName());

            if (Objects.nonNull(getUserRole)) {
                userRoleRepository.updateUserRole(objUser.getId(), objRole.getId(), getUserRole.getId());
            }

            RoleResponse roleResponse = new RoleResponse();
            roleResponse.setId(objRole.getId());
            roleResponse.setName(objRole.getName());
            roleResponse.setDescription(objRole.getDescription());
            roleResponse.setStatus(objRole.getStatus());

        userResponse.setId(objUser.getId());
        userResponse.setUsername(objUser.getUsername());
        userResponse.setName(objUser.getName());
        userResponse.setNickname(objUser.getNickname());
        userResponse.setEmail(objUser.getEmail());
        userResponse.setPhoneNumber(objUser.getPhoneNumber());
        userResponse.setStatus(objUser.getStatus());
        userResponse.setRoleResponse(roleResponse);
        messageResponse.setData(userResponse);


        return messageResponse;
    }

    public MessageResponse listAllUser() {
        List<UserResponse> list = new ArrayList<>();
        MessageResponse messageResponse = new MessageResponse();
        UserResponse userResponse = new UserResponse();
        RoleResponse roleResponse = new RoleResponse();
        List<User> listUser = userRepository.findAllUsers();
        if (listUser.size() == 0) {
            messageResponse.setData("user not found");
        }
        for (User u : listUser) {
            userResponse = new UserResponse();
            userResponse.setId(u.getId());
            userResponse.setUsername(u.getUsername());
            userResponse.setEmail(u.getEmail());
            userResponse.setPhoneNumber(u.getPhoneNumber());
            userResponse.setName(u.getName());
            userResponse.setNickname(u.getNickname());
            userResponse.setStatus(u.getStatus());
            Role r = roleRepository.getByUserName(u.getUsername());
                roleResponse = new RoleResponse();
                roleResponse.setId(r.getId());
                roleResponse.setName(r.getName());
                roleResponse.setDescription(r.getDescription());
                roleResponse.setStatus(r.getStatus());
            userResponse.setRoleResponse(roleResponse);
            list.add(userResponse);
        }

        messageResponse.setData(list);
        return messageResponse;
    }

    public MessageResponse getUserByUsername(String username) {
        MessageResponse messageResponse = new MessageResponse();
        boolean cekUser = false;
        cekUser = userRepository.findByUsername(username).isPresent();
        if (cekUser) {
            messageResponse.setData(userRepository.findByUsername(username).get());
        }
        messageResponse.setData("user not found");
        return messageResponse;
    }

    public MessageResponse getUserById(UUID id) {
        MessageResponse messageResponse = new MessageResponse();
        UserResponse userResponse = new UserResponse();
        boolean cekUser = false;
        RoleResponse roleResponse = new RoleResponse();
        cekUser = userRepository.getUserById(id).isPresent();
        if (cekUser == true) {
            User objUser = userRepository.getUserById(id).get();
            userResponse.setId(id);
            userResponse.setUsername(objUser.getUsername());
            userResponse.setEmail(objUser.getEmail());
            userResponse.setPhoneNumber(objUser.getPhoneNumber());
            userResponse.setName(objUser.getName());
            userResponse.setNickname(objUser.getNickname());
            userResponse.setStatus(objUser.getStatus());
            Role r = roleRepository.getByUserName(objUser.getUsername());

                roleResponse.setId(r.getId());
                roleResponse.setName(r.getName());
                roleResponse.setDescription(r.getDescription());
                roleResponse.setStatus(r.getStatus());

            userResponse.setRoleResponse(roleResponse);
            messageResponse.setData(userResponse);
        }
        if (cekUser == false) {
            messageResponse.setData("user not found");
        }
        return messageResponse;
    }

    public MessageResponse deleteUser(UUID id) {
        MessageResponse messageResponse = new MessageResponse();
        UserResponse userResponse = new UserResponse();
        RoleResponse roleResponse = new RoleResponse();
        boolean cekUser = false;
        cekUser = userRepository.findById(id).isPresent();
        if (cekUser == true) {
            User objUser = userRepository.getUserById(id).get();
            userRepository.updateDeletedUser(id);
            userResponse.setId(id);
            userResponse.setUsername(objUser.getUsername());
            userResponse.setName(objUser.getName());
            userResponse.setNickname(objUser.getNickname());
            userResponse.setEmail(objUser.getEmail());
            userResponse.setPhoneNumber(objUser.getPhoneNumber());
            userResponse.setStatus(objUser.getStatus());
            List<Role> listRole = roleRepository.getAllRolesByUserName(objUser.getUsername());
            for (Role r : listRole) {

                roleResponse.setId(r.getId());
                roleResponse.setName(r.getName());
                roleResponse.setDescription(r.getDescription());
                roleResponse.setStatus(r.getStatus());
            }
            userResponse.setRoleResponse(roleResponse);
            messageResponse.setData(userResponse);
        }

        if (cekUser == false) {
            messageResponse.setData("user not found");
        }

        return messageResponse;
    }

    public MessageResponse resetPassword(ResetPasswordRequest resetPassword) {
        MessageResponse messageResponse = new MessageResponse();
        boolean flag = userRepository.existsByUsername(resetPassword.getUsername());
        if (!flag) {
            messageResponse.setData("user not exists");
        }
        userRepository.resetPassword(encoder.encode(resetPassword.getNewPassword()), resetPassword.getUsername());
        messageResponse.setData("password changed successfully");
        return messageResponse;
    }
}
