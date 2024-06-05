package sb.steradian.sbsteradian.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import sb.steradian.sbsteradian.entity.RefreshToken;
import sb.steradian.sbsteradian.entity.Role;
import sb.steradian.sbsteradian.entity.User;
import sb.steradian.sbsteradian.entity.UserRole;
import sb.steradian.sbsteradian.payload.request.LoginRequest;
import sb.steradian.sbsteradian.payload.request.ResetPasswordRequest;
import sb.steradian.sbsteradian.payload.request.TokenRefreshRequest;
import sb.steradian.sbsteradian.payload.request.UserRequest;
import sb.steradian.sbsteradian.payload.response.JwtResponse;
import sb.steradian.sbsteradian.payload.response.MessageResponse;
import sb.steradian.sbsteradian.payload.response.TokenRefreshResponse;
import sb.steradian.sbsteradian.repository.RoleRepository;
import sb.steradian.sbsteradian.repository.UserRepository;
import sb.steradian.sbsteradian.repository.UserRoleRepository;
import sb.steradian.sbsteradian.service.RefreshTokenService;
import sb.steradian.sbsteradian.service.UserDetailsImpl;
import sb.steradian.sbsteradian.service.UserService;
import sb.steradian.sbsteradian.util.JwtTokenUtil;

import javax.validation.Valid;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/sbsteradian")
public class UserController {

    @Autowired
    private UserService userService;

    @RequestMapping(value = "/api/auth/signin",method = RequestMethod.POST)
    public ResponseEntity<?> authenticateUser(@RequestBody @Valid LoginRequest loginRequest){
        return ResponseEntity.ok(userService.signIn(loginRequest));
    }

    @RequestMapping(value = "/api/auth/resetPassword",method = RequestMethod.POST)
    public ResponseEntity<?> resetPassword(@RequestBody ResetPasswordRequest resetPassword){
        return  ResponseEntity.ok(userService.resetPassword(resetPassword));
    }

    @RequestMapping(value = "/api/auth/signup",method = RequestMethod.POST)
    public ResponseEntity<?> signup(@RequestBody @Valid UserRequest userRequest){
        return ResponseEntity.ok(userService.insertUser(userRequest));
    }

    @RequestMapping(value = "/test/v1/viewPassword/{id}",method = RequestMethod.GET)
    public ResponseEntity<MessageResponse> viewPassword(@PathVariable UUID id){
        return ResponseEntity.ok(userService.viewUserPassword(id));
    }

    @RequestMapping(value = "/test/v1/",method = RequestMethod.GET)
    public ResponseEntity<MessageResponse>listAllTS(){
        return ResponseEntity.ok(userService.listAllUser());
    }

    @RequestMapping(value = "/test/v1/{id}",method = RequestMethod.GET)
    public ResponseEntity<MessageResponse>welcomeTS(@PathVariable("id") UUID id){
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @RequestMapping(value = "/test/v1/update/{id}",method = RequestMethod.PUT)
    public ResponseEntity<MessageResponse>updateTS(@RequestBody @Valid UserRequest userRequest,@PathVariable("id") UUID id){
        return ResponseEntity.ok(userService.updateUser(userRequest,id));
    }

    @RequestMapping(value = "/test/v1/delete/{id}",method = RequestMethod.PUT)
    public ResponseEntity<MessageResponse>deleteTS(@PathVariable("id") UUID id){
        return ResponseEntity.ok(userService.deleteUser(id));
    }





}
