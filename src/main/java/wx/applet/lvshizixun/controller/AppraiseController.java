package wx.applet.lvshizixun.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import wx.applet.lvshizixun.common.Result;
import wx.applet.lvshizixun.common.methods.Simplify;
import wx.applet.lvshizixun.entity.Appraise;
import wx.applet.lvshizixun.entity.Chat;
import wx.applet.lvshizixun.entity.Lawyer;
import wx.applet.lvshizixun.entity.User;
import wx.applet.lvshizixun.entity.viewobject.AppraiseI4_2_1;
import wx.applet.lvshizixun.entity.viewobject.AppraiseO1_2_2;
import wx.applet.lvshizixun.service.AppraiseService;
import wx.applet.lvshizixun.service.LawyerService;
import wx.applet.lvshizixun.service.UserService;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/appraise")
public class AppraiseController {
    @Autowired
    private AppraiseService appraiseService;
    @Autowired
    private UserService userService;
    @Autowired
    private LawyerService lawyerService;
    /**
     * 接口1-2-2
     * @param id 律师ID
     * @return 用户评价列表
     */
    @RequestMapping("/show")
    public Result test(@RequestParam String id){

        QueryWrapper<Appraise> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("lawyer_id",id);
        List<Appraise> appraises = appraiseService.list(queryWrapper);
        List<AppraiseO1_2_2> appraise122List = new ArrayList<>();
        System.out.println(appraises);
        for (int i = 0; i < appraises.size(); i++) {
            AppraiseO1_2_2 appraise122 = new AppraiseO1_2_2();
            String name = userService.getById(appraises.get(i).getUserId()).getName();
            String avatar = userService.getById(appraises.get(i).getUserId()).getAvatar();
            String text = appraises.get(i).getText();
            Integer rate = appraises.get(i).getRate();
            appraise122.setName(name);
            appraise122.setAvatar(avatar);
            appraise122.setText(text);
            appraise122.setRate(rate);
            appraise122List.add(appraise122);
        }
        return Result.success(appraise122List);
    }

    /**
     * 接口4-2-1
     * @param appraiseI421
     * @return 评价保存到数据库中并更新律师的rate值
     */
    @RequestMapping("/save")
    public Result save(@RequestBody AppraiseI4_2_1 appraiseI421){
        Appraise appraise = new Appraise();
        appraise.setId(Simplify.getUUID());
        appraise.setUserId(appraiseI421.getMyId());
        appraise.setLawyerId(appraiseI421.getYouId());
        appraise.setRate(appraiseI421.getRate());
        appraise.setText(appraiseI421.getText());
        appraise.setIsDelete(0);
        appraiseService.saveOrUpdate(appraise);

        //用户评价计算到律师rate里
        Lawyer lawyer = lawyerService.getById(appraiseI421.getYouId());
        QueryWrapper<Appraise> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("lawyer_id",appraiseI421.getYouId());
        Integer appraiseSize = appraiseService.list(queryWrapper).size()-1;
        lawyer.setRate((appraiseSize*lawyer.getRate()+appraiseI421.getRate())/(appraiseSize+1));
        lawyerService.saveOrUpdate(lawyer);
        return Result.success();
    }

    /**
     * 后台 增
     * @param appraise
     * @return
     */
    @RequestMapping("/bgNewAppraise")
    public Result bgNewAppraise(@RequestBody Appraise appraise){
        appraise.setId(Simplify.getUUID());
        appraiseService.saveOrUpdate(appraise);
        return Result.success();
    }

    /**
     * 后台 删
     * @param id
     * @return
     */
    @RequestMapping("/bgDelAppraise")
    public Result bgDelAppraise(@RequestParam String id){
        Appraise appraise = appraiseService.getById(id);
        appraise.setIsDelete(1);
        appraiseService.saveOrUpdate(appraise);
        return Result.success();
    }

    /**
     * 后台 改
     * @param appraise
     * @return
     */
    @RequestMapping("/bgUpdAppraise")
    public Result bgUpdChat(@RequestBody Appraise appraise){
        appraiseService.saveOrUpdate(appraise);
        return Result.success();
    }

    /**
     * 后台 查
     * @param search
     * @return
     */
    @RequestMapping("/bgSelAppraise")
    public Result bgSelOrders(@RequestParam(required = false) String search,
                              @RequestParam Integer pageCurr,
                              @RequestParam Integer pageSize){
        IPage<Appraise> page = new Page<>(pageCurr,pageSize);
        QueryWrapper<Appraise> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("is_delete",0);
        queryWrapper.like(null!=search,"text",search);
        queryWrapper.orderByDesc("create_time");
        return Result.success(appraiseService.page(page,queryWrapper));
    }
}
