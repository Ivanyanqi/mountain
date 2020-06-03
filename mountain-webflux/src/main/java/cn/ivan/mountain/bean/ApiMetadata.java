package cn.ivan.mountain.bean;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author yanqi69
 * @date 2020/6/3
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApiMetadata {

    /**
     *  服务器请求地址
     */
    private String baseUrl;
}
