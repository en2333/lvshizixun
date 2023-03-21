package wx.applet.lvshizixun.entity;

import lombok.Data;

import java.util.Date;

@Data
public class Chat {
    private String id;
    private String myId;
    private String youId;
    private String content;
    private Integer state;
    private Integer isDelete;
    private String createTime;
}
