package wx.applet.lvshizixun.common.component.im.handler;

import com.alibaba.fastjson2.JSON;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import wx.applet.lvshizixun.common.component.im.Command;
import wx.applet.lvshizixun.common.component.im.CommandType;
import wx.applet.lvshizixun.common.component.im.Result;

public class WebSocketHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame frame) throws Exception {
        try{
            Command command = JSON.parseObject(frame.text(),Command.class);
            switch (CommandType.match(command.getCode())){
                case CONNECTION : ConnectionHandler.execute(ctx,command);break;
                case CHAT:ChatHandler.execute(ctx,frame);break;
                default : ctx.channel().writeAndFlush(Result.fail("不支持的CODE"));break;
            }
        }
        catch (Exception e){
            ctx.channel().writeAndFlush(e.getMessage());
        }
    }
}
