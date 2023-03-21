package wx.applet.lvshizixun;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import wx.applet.lvshizixun.common.methods.Simplify;
import wx.applet.lvshizixun.entity.Lawyer;
import wx.applet.lvshizixun.service.LawyerService;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

//@Controller
public class Test {
    public static void main(String[] args) throws ParseException {
        String b = "2023-03-15 14:16:04";
        System.out.println(b.substring(0,b.lastIndexOf(':')));
    }
}
