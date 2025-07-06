package cn.liu.server.handler;

import cn.liu.message.Message;
import cn.liu.message.RpcRequestMessage;
import cn.liu.message.RpcResponseMessage;
import cn.liu.server.service.HelloService;
import cn.liu.server.service.ServicesFactory;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandler;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @Title: 1
 * @Author liu
 * @Package cn.liu.server.handler
 * @Date 2025/7/6 11:45
 * @description:
 */
@Slf4j
@ChannelHandler.Sharable
public class RpcRequestMessageHandler extends SimpleChannelInboundHandler<RpcRequestMessage> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcRequestMessage msg) {
        RpcResponseMessage response = new RpcResponseMessage();
        response.setSequenceId(msg.getSequenceId());

        // 获取实现对象
        try {
            HelloService service = (HelloService) ServicesFactory.getService(Class.forName(msg.getInterfaceName()));
            // 获取调用的方法
            Method method = service.getClass().getMethod(msg.getMethodName(), msg.getParameterTypes());
            // 调用方法
            Object invoke = method.invoke(service, msg.getParameterValue());
            // 调用成功
            response.setReturnValue(invoke);
        } catch (Exception e) {
            e.printStackTrace();
            response.setExceptionValue(new Exception("远程调用出错：" + e.getCause().getMessage()));
        }

        // 返回结果
        ctx.writeAndFlush(response);
    }

    public static void main(String[] args) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        RpcRequestMessage message = new RpcRequestMessage(
                1,
                "cn.liu.server.service.HelloService",
                "sayHello",
                String.class,
                new Class[]{String.class},
                new Object[]{"zhangsan"}
        );
        HelloService service = (HelloService)
                ServicesFactory.getService(Class.forName(message.getInterfaceName()));
        Method method = service.getClass().getMethod(message.getMethodName(), message.getParameterTypes());
        Object invoke = method.invoke(service, message.getParameterValue());
        System.out.println(invoke);
    }
}
