package wx.applet.lvshizixun.common.component.im;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChatMessage extends Command{
    private Integer type;
    private String target;
    private String content;
}
