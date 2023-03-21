package wx.applet.lvshizixun.common.component.im.handler;

import com.alibaba.fastjson2.JSON;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.util.internal.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import wx.applet.lvshizixun.common.component.im.*;
import wx.applet.lvshizixun.common.methods.Simplify;
import wx.applet.lvshizixun.entity.Chat;
import wx.applet.lvshizixun.service.ChatService;

import javax.annotation.PostConstruct;

@Component
public class ChatHandler {
    @Autowired
    private ChatService chatService;
    private static ChatHandler chatHandler;

    @PostConstruct
    public void init(){
        chatHandler = this;
    }

    public static void execute(ChannelHandlerContext ctx, TextWebSocketFrame frame) {
        try{
            ChatMessage chat = JSON.parseObject(frame.text(),ChatMessage.class);
            switch (MessageType.match(chat.getType())){
                case PRIVATE:
                    if (StringUtil.isNullOrEmpty(chat.getTarget())){
                        ctx.channel().writeAndFlush(Result.fail("消息发送失败，请指定接收对象"));
                        return;
                    }
                    Channel channel = IMServer.USERS.get(chat.getTarget());

                    Chat chat1 = new Chat();
                    chat1.setId(Simplify.getUUID());
                    chat1.setYouId(chat.getTarget());
                    chat1.setMyId(chat.getId());
                    chat1.setContent(chat.getContent());
                    chatHandler.chatService.saveOrUpdate(chat1);

                    if (null == channel || !channel.isActive()){
                        ctx.channel().writeAndFlush(Result.fail("消息发送失败，"+chat.getTarget()+"不在线"));
                    }else {
                        channel.writeAndFlush(Result.success(chat.getId(),chat.getContent()));
                    }
                    break;
                default:
                    ctx.channel().writeAndFlush(Result.fail("不支持的消息类型"));
            }
        }
        catch (Exception e){
            ctx.channel().writeAndFlush(Result.fail("发送消息格式错误，请确认后再试"));
        }

    }
}
