package io.e2x.tigor.frameworks.common.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 错误类，继承 Throwable
 * reference: yudao-common
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class ErrorCode extends Throwable {

    /**
     * 错误码
     */
    private final Integer code;
    /**
     * 错误提示
     */
    private final String msg;

    public ErrorCode(Integer code, String message) {
        this.code = code;
        this.msg = message;
    }

}
