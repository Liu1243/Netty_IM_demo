package cn.liu.server.handler;

import cn.liu.message.ChatRequestMessage;
import cn.liu.message.ChatResponseMessage;
import cn.liu.server.session.SessionFactory;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * @Title: 1
 * @Author 1
 * @Package cn.liu.server.handler
 * @Date 2025/6/28 00:13
 * @description:
 */
@ChannelHandler.Sharable
public class ChatRequestMessageHandler extends SimpleChannelInboundHandler<ChatRequestMessage> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ChatRequestMessage msg) throws Exception {
        String msgTo = msg.getTo();

        Channel channel = SessionFactory.getSession().getChannel(msgTo);

        // 如果在线
        if (channel != null) {
            channel.writeAndFlush(new ChatResponseMessage(msg.getFrom(), msg.getContent()));
        }
        // 不在线
        else {
            ctx.writeAndFlush(new ChatResponseMessage(msg.getFrom(), "对方不在线"));
        }
    }
}
