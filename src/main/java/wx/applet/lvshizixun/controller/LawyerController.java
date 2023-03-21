package wx.applet.lvshizixun.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.netty.channel.Channel;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import wx.applet.lvshizixun.common.Result;
import wx.applet.lvshizixun.common.component.im.IMServer;
import wx.applet.lvshizixun.common.methods.Simplify;
import wx.applet.lvshizixun.entity.Lawyer;
import wx.applet.lvshizixun.entity.User;
import wx.applet.lvshizixun.entity.viewobject.LawyerO1_1_1;
import wx.applet.lvshizixun.entity.viewobject.LawyerO1_2_1;
import wx.applet.lvshizixun.service.LawyerService;
import wx.applet.lvshizixun.service.UserService;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

@RestController
@RequestMapping("/lawyer")
public class LawyerController {
    @Autowired
    private LawyerService lawyerService;

    @Autowired
    private UserService userService;

    /**
     * 接口1-1-1
     * 加载律师列表
     * @param index 0,表示搜索全部律师，1表示搜索全部专业律师，2表示搜索全部同城律师 ,默认传入为0
     * @param data 当index是0的时候，data表示律师名字，当index是1时表示工作多少年的才算是专业律师，当是2时表示地名
     * @return list数组
     */
    @RequestMapping("/loadLawyers")
    public Result loadLawyers(@RequestParam Integer index, @RequestParam String data){
        QueryWrapper<Lawyer> queryWrapper = new QueryWrapper();
        queryWrapper.ne("state",0);
        if (index == 0){
            queryWrapper.like(null!=data,"name",data);
        } else if (index == 1){
            Calendar cal = Calendar.getInstance();//获取当前时间
            if (null!=data && ""!=data)
                cal.add(Calendar.YEAR, Integer.valueOf(data));//加上多少时间
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");//时间格式化
            queryWrapper.apply("date_format (enter_time,'%Y-%m-%d') <= date_format('" + sdf.format(cal.getTime()) + "','%Y-%m-%d')");
        } else if (index == 2) {
            queryWrapper.like(null!=data,"address", data);
        }
        List<LawyerO1_1_1> result = Simplify.entityToViews(lawyerService,queryWrapper,"wx.applet.lvshizixun.entity.viewobject.LawyerO1_1_1");
        result.forEach(item->{
            Channel channel = IMServer.USERS.get(item.getId());
                    if (null == channel || !channel.isActive()){
                        item.setState(1);
                    }else {
                        item.setState(2);
                    }
        });
        System.out.println(result);
        return Result.success(result);
    }

    /**
     * 接口1-2-1
     * 展示律师主页面内容
     * @param id 律师ID
     * @return lawyer1-2-1对象
     */
    @RequestMapping("/getLawyerInfo")
    public Result getLawyerInfo(@RequestParam String id){
        LawyerO1_2_1 lawyerO1_2_1 =new LawyerO1_2_1();
        BeanUtils.copyProperties(lawyerService.getById(id), lawyerO1_2_1);
        return Result.success(lawyerO1_2_1);
    }

    /**
     * 接口5-1-2
     * @param lawyer
     * @return
     */
    @RequestMapping("/beLawyer")
    public Result beLawyer(@RequestBody Lawyer lawyer){
        lawyer.setAvatar(userService.getById(lawyer.getId()).getAvatar());
        lawyerService.saveOrUpdate(lawyer);
        return Result.success();
    }

    /**
     * 后台增
     * @param lawyer
     * @return
     */
    @RequestMapping("/bgNewLawyer")
    public Result bgNewLawyer(@RequestBody Lawyer lawyer){
        lawyer.setId(Simplify.getUUID());
        lawyer.setState(1);
        lawyerService.saveOrUpdate(lawyer);//新增律师
        User user = new User();
        user.setId(lawyer.getId());
        user.setName(lawyer.getName());
        user.setAddress(lawyer.getAddress());
        user.setStatus(1);
        userService.saveOrUpdate(user);//新增用户
        return Result.success();
    }

    /**
     * 后台 删
     * @param id
     * @return
     */
    @RequestMapping("/bgDelLawyer")
    public Result bgDelLawyer(@RequestParam String id){
        Lawyer lawyer = lawyerService.getById(id);
        lawyer.setIsDelete(1);
        lawyerService.saveOrUpdate(lawyer);
        return Result.success();
    }

    /**
     * 后台 改
     * @param lawyer
     * @return
     */
    @RequestMapping("/bgUpdLawyer")
    public Result bgUpdLawyer(@RequestBody Lawyer lawyer){
        lawyerService.saveOrUpdate(lawyer);
        return Result.success();
    }

    /**
     * 后台 只改状态
     * @param id
     * @return
     */
    @RequestMapping("/bgUpdLawState")
    public Result bgUpdLawState(@RequestParam String id){
        Lawyer lawyer = lawyerService.getById(id);
        lawyer.setState(lawyer.getState()==0?1:0);
        lawyerService.saveOrUpdate(lawyer);
        return Result.success();
    }

    /**
     * 后台 查
     * @param search
     * @param pageCurr
     * @param pageSize
     * @return
     */
    @RequestMapping("/bgSelectLawyer")
    public Result bgSelectLawyer(@RequestParam(required = false) String search,
                                 @RequestParam Integer pageCurr,
                                 @RequestParam Integer pageSize){
        IPage<Lawyer> page = new Page<>(pageCurr,pageSize);
        QueryWrapper<Lawyer> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("is_delete",0);
        queryWrapper.like(null!=search,"name",search);
        queryWrapper.orderByDesc("create_time");
        return Result.success(lawyerService.page(page,queryWrapper));
    }
}
