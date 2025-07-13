package microvetcare.microvetcare.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler{

    // Maneja ResourceNotFoundException (para tus casos 404)
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Object> handleResourceNotFoundException(ResourceNotFoundException ex, WebRequest request) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", HttpStatus.NOT_FOUND.value());
        body.put("error", "Not Found");
        body.put("message", ex.getMessage());
        body.put("path", request.getDescription(false).replace("uri=", ""));
        return new ResponseEntity<>(body, HttpStatus.NOT_FOUND);
    }

    // Maneja IllegalArgumentException (para tus casos 400, como validaciones de negocio)
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Object> handleIllegalArgumentException(IllegalArgumentException ex, WebRequest request) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", HttpStatus.BAD_REQUEST.value());
        body.put("error", "Bad Request");
        body.put("message", ex.getMessage());
        body.put("path", request.getDescription(false).replace("uri=", ""));
        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

    // Puedes añadir más @ExceptionHandler para otras excepciones comunes
    // Por ejemplo, MethodArgumentNotValidException para errores de validación de @Valid
    // @ExceptionHandler(MethodArgumentNotValidException.class)
    // public ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, WebRequest request) {
    //     Map<String, Object> body = new LinkedHashMap<>();
    //     body.put("timestamp", LocalDateTime.now());
    //     body.put("status", HttpStatus.BAD_REQUEST.value());
    //     body.put("error", "Validation Error");
    //     List<String> errors = ex.getBindingResult()
    //             .getFieldErrors()
    //             .stream()
    //             .map(x -> x.getDefaultMessage())
    //             .collect(Collectors.toList());
    //     body.put("messages", errors);
    //     body.put("path", request.getDescription(false).replace("uri=", ""));
    //     return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    // }

    // Manejador genérico para cualquier otra excepción no capturada
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleGlobalException(Exception ex, WebRequest request) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
        body.put("error", "Internal Server Error");
        body.put("message", "Ocurrió un error inesperado.");
        body.put("path", request.getDescription(false).replace("uri=", ""));
        // Opcional: Para depuración, puedes incluir ex.getMessage() si no es de producción
        // body.put("message", ex.getMessage());
        return new ResponseEntity<>(body, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
