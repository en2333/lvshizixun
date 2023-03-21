package wx.applet.lvshizixun.entity;

import lombok.Data;

@Data
public class Orders {
    private String id;
    private String myId;
    private String youId;
    private Integer orderType;
    private Integer consultType;
    private Float price;
    private String content;
    private Integer isDelete;
    private String createTime;
}
