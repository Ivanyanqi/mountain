package cn.ivan.mountain.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * @author yanqi69
 * @date 2020/6/4
 */
public class MountainHttpException extends MountainException {

    @Getter
    private HttpStatus httpStatus;

    public MountainHttpException(HttpStatus httpStatus, String message){
        super(message);
        this.httpStatus = httpStatus;

    }
}
