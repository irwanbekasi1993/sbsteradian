package sb.steradian.sbsteradian.exception;

import io.jsonwebtoken.ExpiredJwtException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import sb.steradian.sbsteradian.payload.ErrorMessage;

import java.util.Date;

@ControllerAdvice
public class ControllerExceptionHandler {

    ErrorMessage errorMessage = new ErrorMessage();
    int status=0;

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorMessage> exception(Exception ex, WebRequest webRequest){
        System.err.println(ex);
        if(ex instanceof ExpiredJwtException){
            errorMessage = new ErrorMessage(HttpStatus.UNAUTHORIZED.value(),new Date(), ex.getMessage(), webRequest.getDescription(false));
            return new ResponseEntity<ErrorMessage>(errorMessage,HttpStatus.UNAUTHORIZED);
        }
        if(ex instanceof AuthenticationException){
            errorMessage = new ErrorMessage(HttpStatus.UNAUTHORIZED.value(),new Date(), ex.getMessage(), webRequest.getDescription(false));
            return new ResponseEntity<ErrorMessage>(errorMessage,HttpStatus.UNAUTHORIZED);
        }
        if(ex instanceof AccessDeniedException){
            errorMessage = new ErrorMessage(HttpStatus.UNAUTHORIZED.value(),new Date(), ex.getMessage(), webRequest.getDescription(false));
            return new ResponseEntity<ErrorMessage>(errorMessage,HttpStatus.UNAUTHORIZED);
        }
        return null;
    }


}
