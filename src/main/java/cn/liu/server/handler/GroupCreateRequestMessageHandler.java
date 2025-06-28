package cn.liu.server.handler;

import cn.liu.message.GroupCreateRequestMessage;
import cn.liu.message.GroupCreateResponseMessage;
import cn.liu.server.session.Group;
import cn.liu.server.session.GroupSession;
import cn.liu.server.session.GroupSessionFactory;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.util.Set;

/**
 * @Title: 1
 * @Author liu
 * @Package cn.liu.server.handler
 * @Date 2025/6/28 11:35
 * @description:
 */
@ChannelHandler.Sharable
public class GroupCreateRequestMessageHandler extends SimpleChannelInboundHandler<GroupCreateRequestMessage> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, GroupCreateRequestMessage msg) throws Exception {
        String groupName = msg.getGroupName();
        Set<String> members = msg.getMembers();
        // 群管理器
        GroupSession groupSession = GroupSessionFactory.getGroupSession();
        Group group = groupSession.createGroup(groupName, members);
        if (group == null) {
            // 发送创建群成功消息
            ctx.writeAndFlush(new GroupCreateResponseMessage(true, "创建群聊成功"));
            // 发送拉群消息
            for (Channel channel : groupSession.getMembersChannel(groupName)) {
                ctx.writeAndFlush(new GroupCreateResponseMessage(true, "您已被拉入群聊" + groupName));
            }

        } else {
            ctx.writeAndFlush(new GroupCreateResponseMessage(false, "群聊已存在"));
        }
    }
}
