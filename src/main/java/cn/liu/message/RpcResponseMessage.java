package cn.liu.message;

import lombok.Data;
import lombok.ToString;

/**
 * @Title: 1
 * @Author liu
 * @Package cn.liu.message
 * @Date 2025/7/6 11:43
 * @description:
 */
@Data
@ToString(callSuper = true)
public class RpcResponseMessage extends Message {
    /**
     * 返回值
     */
    private Object returnValue;
    /**
     * 异常值
     */
    private Exception exceptionValue;

    @Override
    public int getMessageType() {
        return RPC_MESSAGE_TYPE_RESPONSE;
    }
}
