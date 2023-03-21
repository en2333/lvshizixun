package wx.applet.lvshizixun.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import wx.applet.lvshizixun.entity.User;
import wx.applet.lvshizixun.mapper.UserMapper;

@Service
public class UserService extends ServiceImpl<UserMapper, User>{
}
