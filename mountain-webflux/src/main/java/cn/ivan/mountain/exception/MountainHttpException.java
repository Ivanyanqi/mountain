package cn.ivan.mountain.exception;

import lombok.Getter;
import org.springframework.http.HttpStatusCode;

/**
 * @author yanqi69
 * @date 2020/6/4
 */
public class MountainHttpException extends MountainException {

    @Getter
    private HttpStatusCode httpStatus;

    public MountainHttpException(HttpStatusCode httpStatus, String message){
        super(message);
        this.httpStatus = httpStatus;

    }
}
