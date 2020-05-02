package cc.mrbird.handler;

import cc.mrbird.exception.UserNotExistException;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
@RestController
public class ControllerExceptionHandler {

//    @ExceptionHandler(UserNotExistException.class)
//    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
//    public Map<String, Object> handleUserNotExistsException(UserNotExistException e) {
//        Map<String, Object> map = new HashMap<>();
//        map.put("id", e.getId());
//        map.put("message", e.getMessage());
//        return map;
//    }
//    @ResponseStatus(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
//    @ExceptionHandler(Exception.class)
//    public Map<String, Object> handleHttpMediaTypeNotSupportedException(HttpMediaTypeNotSupportedException e) {
//        Map<String, Object> map = new HashMap<>();
//        map.put("person","Exception");
//        map.put("message", e.getMessage());
//        return map;
//    }
//
//    @ResponseStatus(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
//    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
//    public Map<String, Object> handleHttpMediaTypeNotSupportedException(Exception e) {
//        Map<String, Object> map = new HashMap<>();
//        map.put("person","HttpMediaTypeNotSupportedException");
//        map.put("message", e.getMessage());
//        return map;
//    }

    /**
     * 405 - Method Not Allowed
     *
     * @param e the e
     * @return the service response
     */
    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public Map<String, Object> handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException e) {
        Map<String, Object> map = new HashMap<>();
        map.put("person","HttpRequestMethodNotSupportedException");
        map.put("message", e.getMessage());
        return map;
    }

    /**
     * 400 - Bad Request
     *
     * @param e the e
     * @return the service response
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public Map<String, Object> handleHttpMessageNotReadableException(HttpMessageNotReadableException e) {
        Map<String, Object> map = new HashMap<>();
        map.put("person","handleHttpMessageNotReadableException");
        map.put("message", e.getMessage());
        return map;
    }

//    @ExceptionHandler(Exception.class)
////    @ResponseStatus(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
//    public Map<String, Object> handleUserNotExistsException(Exception e) {
//        Map<String, Object> map = new HashMap<>();
//        map.put("person","handleUserNotExistsException");
//        map.put("message", e.getMessage());
//        return map;
//    }
}
