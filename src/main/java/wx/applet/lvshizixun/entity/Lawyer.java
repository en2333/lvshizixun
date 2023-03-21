package wx.applet.lvshizixun.entity;

import lombok.Data;

import java.util.Date;

@Data
public class Lawyer {
    private String id;
    private String name;
    private Integer sex;
    private String address;
    private String identNum;
    private String company;
    private String goodAt;
    private String enterTime;
    private String phone;
    private Float phonePrice;
    private Float imgTxtPrice;
    private String oftenCourts;
    private String introduce;

    private String avatar;

    private Float rate;
    private Integer state;
    private Integer isDelete;
    private String createTime;
    private String modifyTime;
}
