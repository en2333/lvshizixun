package wx.applet.lvshizixun.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import wx.applet.lvshizixun.common.Result;
import wx.applet.lvshizixun.common.methods.Simplify;
import wx.applet.lvshizixun.entity.Lawyer;
import wx.applet.lvshizixun.entity.User;
import wx.applet.lvshizixun.service.LawyerService;
import wx.applet.lvshizixun.service.UserService;

import java.io.File;
import java.io.IOException;

import static wx.applet.lvshizixun.common.methods.Simplify.getUUID;

@RestController
@RequestMapping("/user")
public class UserController {
    @Value("${files.imageUpload.path}")
    private String imageUpload;

    @Autowired
    private UserService userService;

    @Autowired
    private LawyerService lawyerService;

    @RequestMapping("/loadLawyers")
    public Result loadLawyers(@RequestParam Integer index,@RequestParam String data){
    return Result.success();
    }

    @RequestMapping("/uploadAvatar")
    public Result upload(@RequestParam MultipartFile file) throws IOException {
        String type = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf("."));
        File uploadParentFile = new File(imageUpload);
        if(!uploadParentFile.exists()){
            uploadParentFile.mkdirs();
        }
        String id=getUUID();
        String name = id+type;
        File uploadFile = new File(imageUpload+name);
        file.transferTo(uploadFile);
        return Result.success(name);
    }

    @RequestMapping("/updateUser")
    public Result updateUser(@RequestBody User user){
        userService.saveOrUpdate(user);

        QueryWrapper queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id",user.getId());
        if(lawyerService.list(queryWrapper).size()==1){
            Lawyer lawyer = lawyerService.getById(user.getId());
            lawyer.setAvatar(user.getAvatar());
            lawyer.setName(user.getName());
            lawyerService.saveOrUpdate(lawyer);
        }
        return Result.success();
    }

    /**
     * 接口5-1-1
     * @param id
     * @return
     */
    @RequestMapping("/login")
    public Result login(@RequestParam String id){
        User user= new User();
        user.setId(id);
        user.setIsDelete(0);
        userService.saveOrUpdate(user);
        return Result.success(userService.getById(id));
    }

    /**
     * 后台 增
     * @param user
     * @return
     */
    @RequestMapping("/bgNewUser")
    public Result bgNewUser(@RequestBody User user){
        user.setId(Simplify.getUUID());
        userService.saveOrUpdate(user);
        return Result.success();
    }

    /**
     * 后台 删
     * @param id
     * @return
     */
    @RequestMapping("/bgDelUser")
    public Result bgNewUser(@RequestParam String id){
        User user = userService.getById(id);
        user.setIsDelete(1);
        userService.saveOrUpdate(user);
        return Result.success();
    }

    /**
     * 后台 查
     * @param search
     * @return
     */
    @RequestMapping("/bgSelUser")
    public Result bgSelUser(@RequestParam(required = false) String search,
                            @RequestParam Integer pageCurr,
                            @RequestParam Integer pageSize){
        IPage<User> page = new Page<>(pageCurr,pageSize);
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("is_delete",0);
        queryWrapper.like(null!=search,"name",search);
        queryWrapper.orderByDesc("create_time");
        return Result.success(userService.page(page,queryWrapper));
    }
}
