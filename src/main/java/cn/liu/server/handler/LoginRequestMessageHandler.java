package cn.liu.server.handler;

import cn.liu.message.LoginRequestMessage;
import cn.liu.message.LoginResponseMessage;
import cn.liu.server.service.UserServiceFactory;
import cn.liu.server.session.SessionFactory;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * @Title: 1
 * @Author 1
 * @Package cn.liu.server.handler
 * @Date 2025/6/28 00:11
 * @description:
 */
@ChannelHandler.Sharable
public class LoginRequestMessageHandler extends SimpleChannelInboundHandler<LoginRequestMessage> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, LoginRequestMessage msg) throws Exception {
        String username = msg.getUsername();
        String pwd = msg.getPassword();
        boolean login = UserServiceFactory.getUserService().login(username, pwd);
        LoginResponseMessage responseMsg;
        if (login) {
            SessionFactory.getSession().bind(ctx.channel(), username);
            responseMsg = new LoginResponseMessage(true, "登录成功");
        } else {
            responseMsg = new LoginResponseMessage(false, "用户名或密码错误");
        }

        ctx.writeAndFlush(responseMsg);
    }
}
