package sb.steradian.sbsteradian.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sb.steradian.sbsteradian.entity.RefreshToken;
import sb.steradian.sbsteradian.exception.TokenRefreshException;
import sb.steradian.sbsteradian.repository.RefreshTokenRepository;
import sb.steradian.sbsteradian.repository.UserRepository;
import sb.steradian.sbsteradian.util.JwtTokenUtil;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
public class RefreshTokenService {

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Autowired
    private UserRepository userRepository;

    public Optional<RefreshToken> findByToken(String token){
        return refreshTokenRepository.findByToken(token);
    }

    public RefreshToken createRefreshToken(String username){
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setUser(userRepository.findByUsername(username).get());
        refreshToken.setExpiryDate(Instant.now().plusMillis(JwtTokenUtil.JWT_TOKEN_VALIDITY));
        refreshToken.setToken(UUID.randomUUID().toString());

        refreshToken = refreshTokenRepository.save(refreshToken);
        return refreshToken;
    }

    public RefreshToken verifyExpiration(RefreshToken token){
        if(token.getExpiryDate().compareTo(Instant.now())<0){
            refreshTokenRepository.delete(token);
            throw new TokenRefreshException(token.getToken(), "token is expired, please replace to new token");
        }
        return token;
    }
}
