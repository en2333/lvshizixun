package wx.applet.lvshizixun.entity;

import lombok.Data;

@Data
public class Appraise {
    private String id;
    private String userId;
    private String lawyerId;
    private Integer rate;
    private String text;
    private Integer isDelete;
    private String createTime;
}
