package cn.liu.client;

import cn.liu.client.handler.RpcResponseMessageHandler;
import cn.liu.message.RpcRequestMessage;
import cn.liu.message.RpcResponseMessage;
import cn.liu.protocol.MessageCodecSharable;
import cn.liu.protocol.ProcotolFrameDecoder;
import cn.liu.protocol.SequenceIdGenerator;
import cn.liu.server.service.HelloService;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.util.concurrent.DefaultPromise;
import lombok.extern.slf4j.Slf4j;
import lombok.val;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;

/**
 * @Title: 1
 * @Author liu
 * @Package cn.liu.client
 * @Date 2025/7/6 11:46
 * @description:
 */
@Slf4j
public class RpcClientManager {

    private static Channel channel = null;
    private static final Object LOCK = new Object();

    public static void main(String[] args) {
        HelloService proxyService = getProxyService(HelloService.class);
        System.out.println(proxyService.sayHello("张三"));
        System.out.println(proxyService.sayHello("lisi"));
    }

    // 创建代理类
    public static <T> T getProxyService(Class<T> serviceClass) {
        ClassLoader classLoader = serviceClass.getClassLoader();
        Class<?>[] interfaces = new Class[]{serviceClass};
        Object o = Proxy.newProxyInstance(classLoader, interfaces, (proxy, method, args) -> {
            // 将方法调用转换为 消息对象
            int sequenceId = SequenceIdGenerator.nextId();
            RpcRequestMessage msg = new RpcRequestMessage(
                    sequenceId,
                    serviceClass.getName(),
                    method.getName(),
                    method.getReturnType(),
                    method.getParameterTypes(),
                    args
            );
            // 将消息对象发送出去
            getChannel().writeAndFlush(msg);

            // 使用Promise接收结果
            DefaultPromise<Object> promise = new DefaultPromise<>(getChannel().eventLoop());
            RpcResponseMessageHandler.PROMISES.put(sequenceId, promise);

            // 等待promise结果
            promise.await();
            if (promise.isSuccess()) {
                // 调用正常
                return promise.getNow();
            } else {
                // 调用出错
                throw new RuntimeException(promise.cause());
            }
        });
        return (T) o;
    }

    // 单例模式
    public static Channel getChannel() {
        if (channel != null) {
            return channel;
        }
        synchronized (LOCK) {
            // double check
            if (channel != null) {
                return channel;
            }
            initChannel();
            return channel;
        }
    }

    // 初始化channel
    private static void initChannel() {
        NioEventLoopGroup group = new NioEventLoopGroup();
        LoggingHandler LOGGING_HANDLER = new LoggingHandler(LogLevel.DEBUG);
        MessageCodecSharable MESSAGE_CODEC = new MessageCodecSharable();

        // rpc 响应消息处理器，待实现
        RpcResponseMessageHandler RPC_HANDLER = new RpcResponseMessageHandler();
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.channel(NioSocketChannel.class);
        bootstrap.group(group);
        bootstrap.handler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) throws Exception {
                ch.pipeline().addLast(new ProcotolFrameDecoder());
                ch.pipeline().addLast(LOGGING_HANDLER);
                ch.pipeline().addLast(MESSAGE_CODEC);
                ch.pipeline().addLast(RPC_HANDLER);
            }
        });
        try {
            channel = bootstrap.connect("localhost", 8080).sync().channel();

            // 将sync同步方法修改为异步方式 防止getChannel阻塞
            channel.closeFuture().addListener(future -> {
                group.shutdownGracefully();
            });
        } catch (Exception e) {
            log.error("client error", e);
        }
    }
}
