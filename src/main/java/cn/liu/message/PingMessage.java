package cn.liu.message;

/**
 * @Title: 1
 * @Author liu
 * @Package cn.liu.message
 * @Date 2025/6/28 13:09
 * @description:
 */
public class PingMessage extends Message {
    @Override
    public int getMessageType() {
        return PingMessage;
    }
}

