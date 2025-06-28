package cn.liu.server.handler;

import cn.liu.message.GroupJoinRequestMessage;
import cn.liu.message.GroupJoinResponseMessage;
import cn.liu.server.session.Group;
import cn.liu.server.session.GroupSessionFactory;
import cn.liu.server.session.SessionFactory;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

/**
 * @Title: 1
 * @Author liu
 * @Package cn.liu.server.handler
 * @Date 2025/6/28 11:21
 * @description:
 */
@ChannelHandler.Sharable
@Slf4j
public class GroupJoinRequestMessageHandler extends ChannelInboundHandlerAdapter {
    // 连接断开出发inactive事件
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        SessionFactory.getSession().unbind(ctx.channel());
        log.debug("{} 已经断开", ctx.channel());
    }

    // 发生异常时触发
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        SessionFactory.getSession().unbind(ctx.channel());
        log.debug("{} 已经断开, cause: {}", ctx.channel(), cause.getMessage());
    }
}
