package cn.liu.client.handler;

import cn.liu.message.RpcResponseMessage;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.concurrent.Promise;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Title: 1
 * @Author liu
 * @Package cn.liu.client.handler
 * @Date 2025/7/6 12:02
 * @description:
 */
@Slf4j
@ChannelHandler.Sharable    // 可以存储状态，但是需要考虑线程安全问题
public class RpcResponseMessageHandler extends SimpleChannelInboundHandler<RpcResponseMessage> {

    // 用力接收RPC结果的promise对象  需要使用线程安全的ConcurrentHashMap
    public static final Map<Integer, Promise<Object>> PROMISES = new ConcurrentHashMap<>();

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcResponseMessage msg) throws Exception {
        log.debug("从服务器接收到响应：{}", msg);

        // 拿到promise
        Promise<Object> promise = PROMISES.remove(msg.getSequenceId());

        if (promise != null) {
            Object returnValue = msg.getReturnValue();
            Exception exceptionValue = msg.getExceptionValue();
            if (exceptionValue == null) {
                promise.setSuccess(returnValue);
            } else {
                promise.setFailure(exceptionValue);
            }
        }

    }
}
