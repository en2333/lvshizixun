package wx.applet.lvshizixun.common.component.im.handler;

import com.alibaba.fastjson2.JSON;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import wx.applet.lvshizixun.common.component.im.Command;
import wx.applet.lvshizixun.common.component.im.IMServer;
import wx.applet.lvshizixun.common.component.im.Result;

public class ConnectionHandler {
    public static void execute(ChannelHandlerContext ctx, Command command) {
        if (IMServer.USERS.containsKey(command.getId())){
            IMServer.USERS.get(command.getId()).writeAndFlush(Result.fail("账号在其他设备登录"));
            IMServer.USERS.get(command.getId()).disconnect();
        }

        IMServer.USERS.put(command.getId(), ctx.channel());
        System.out.println(IMServer.USERS);
        ctx.channel().writeAndFlush(Result.success("与服务连接成功"));
        ctx.channel().writeAndFlush(Result.success(JSON.toJSONString(IMServer.USERS.keySet())));

    }
}
