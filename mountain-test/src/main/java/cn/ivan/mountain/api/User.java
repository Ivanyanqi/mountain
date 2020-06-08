package cn.ivan.mountain.api;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author yanqi69
 * @date 2020/6/5
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class User implements Serializable {
    private String index;
}
