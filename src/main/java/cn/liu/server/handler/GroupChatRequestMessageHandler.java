package cn.liu.server.handler;

import cn.liu.message.GroupChatRequestMessage;
import cn.liu.message.GroupChatResponseMessage;
import cn.liu.server.session.GroupSession;
import cn.liu.server.session.GroupSessionFactory;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.util.List;

/**
 * @Title: 1
 * @Author liu
 * @Package cn.liu.server.handler
 * @Date 2025/6/28 11:17
 * @description:
 */
@ChannelHandler.Sharable
public class GroupChatRequestMessageHandler extends SimpleChannelInboundHandler<GroupChatRequestMessage> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, GroupChatRequestMessage msg) throws Exception {
        List<Channel> membersChannel = GroupSessionFactory.getGroupSession().getMembersChannel(msg.getGroupName());

        for (Channel channel : membersChannel) {
            channel.writeAndFlush(new GroupChatResponseMessage(msg.getFrom(), msg.getContent()));
        }
    }
}
