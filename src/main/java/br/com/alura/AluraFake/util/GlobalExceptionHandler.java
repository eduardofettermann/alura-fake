package br.com.alura.AluraFake.util;

import br.com.alura.AluraFake.exception.DomainException;
import br.com.alura.AluraFake.exception.EmailAlreadyRegisteredException;
import br.com.alura.AluraFake.exception.EmailOrPasswordInvalidException;
import br.com.alura.AluraFake.exception.ForbiddenException;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.List;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<List<ErrorItemDTO>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        List<ErrorItemDTO> errors = ex.getBindingResult().getFieldErrors().stream().map(ErrorItemDTO::new).toList();
        return ResponseEntity.badRequest().body(errors);
    }

    @ExceptionHandler(DomainException.class)
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    public ResponseEntity<ErrorItemDTO> handleDomainException(DomainException ex) {
        ErrorItemDTO error = new ErrorItemDTO(ex.getField(), ex.getMessage());
        return ResponseEntity.unprocessableEntity().body(error);
    }

    @ExceptionHandler(ForbiddenException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ResponseEntity<ErrorItemDTO> handleForbiddenException(ForbiddenException ex) {
        ErrorItemDTO error = new ErrorItemDTO(ex.getField(), ex.getMessage());
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
    }

    @ExceptionHandler({JWTCreationException.class, JWTVerificationException.class})
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ResponseEntity<ErrorItemDTO> handleJWTCreationException(RuntimeException ex) {
        ErrorItemDTO error = new ErrorItemDTO("token", ex.getMessage());
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
    }

    @ExceptionHandler(EmailAlreadyRegisteredException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ResponseEntity<ErrorItemDTO> handleEmailAlreadyRegisteredException(EmailAlreadyRegisteredException ex) {
        ErrorItemDTO error = new ErrorItemDTO(ex.getField(), ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }

    @ExceptionHandler(EmailOrPasswordInvalidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ErrorItemDTO> handleEmailAlreadyRegisteredException(EmailOrPasswordInvalidException ex) {
        ErrorItemDTO error = new ErrorItemDTO(ex.getField(), ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }
}
