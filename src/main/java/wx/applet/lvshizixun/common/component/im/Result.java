package wx.applet.lvshizixun.common.component.im;

import com.alibaba.fastjson2.JSON;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Result {
    private String id;
    private LocalDateTime time;
    private String message;

    public static TextWebSocketFrame fail(String message){
        return new TextWebSocketFrame(JSON.toJSONString(new Result("系统消息",LocalDateTime.now(),message)));
    }
    public static TextWebSocketFrame success(String message){
        return new TextWebSocketFrame(JSON.toJSONString(new Result("系统消息",LocalDateTime.now(),message)));
    }
    public static TextWebSocketFrame success(String user,String message){
        return new TextWebSocketFrame(JSON.toJSONString(new Result(user,LocalDateTime.now(),message)));
    }
}
