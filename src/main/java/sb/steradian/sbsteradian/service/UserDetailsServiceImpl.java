package sb.steradian.sbsteradian.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import sb.steradian.sbsteradian.entity.User;
import sb.steradian.sbsteradian.entity.UserRole;
import sb.steradian.sbsteradian.repository.UserRepository;
import sb.steradian.sbsteradian.repository.UserRoleRepository;

import javax.transaction.Transactional;


@Service
public class UserDetailsServiceImpl implements UserDetailsService{
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserRoleRepository userRoleRepository;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // TODO Auto-generated method stub
        User user = userRepository.findByUsername(username).orElseThrow(()->new UsernameNotFoundException("user not found"));
        UserRole userRole = userRoleRepository.getByUsername(user.getUsername());
        return UserDetailsImpl.build(userRole);
    }

    
}
