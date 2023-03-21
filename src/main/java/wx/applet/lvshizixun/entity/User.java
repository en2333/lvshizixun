package wx.applet.lvshizixun.entity;

import lombok.Data;

@Data
public class User {
    private String id;
    private String name;
    private String avatar;
    private Integer status;
    private String address;
    private Integer isDelete;
    private String createTime;
    private String modifyTime;
}
