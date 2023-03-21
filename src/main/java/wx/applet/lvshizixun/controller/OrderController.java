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
import wx.applet.lvshizixun.entity.Chat;
import wx.applet.lvshizixun.entity.Orders;
import wx.applet.lvshizixun.entity.User;
import wx.applet.lvshizixun.entity.viewobject.OrdersI1_2_3;
import wx.applet.lvshizixun.entity.viewobject.OrdersO4_1_1;
import wx.applet.lvshizixun.entity.viewobject.OrdersO4_1_2;
import wx.applet.lvshizixun.service.ChatService;
import wx.applet.lvshizixun.service.LawyerService;
import wx.applet.lvshizixun.service.OrdersService;
import wx.applet.lvshizixun.service.UserService;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/order")
public class OrderController {
    @Autowired
    private OrdersService ordersService;

    @Autowired
    private UserService userService;

    @Autowired
    private ChatService chatService;

    @Autowired
    private LawyerService lawyerService;
    /**
     * 接口1-2-3
     * @param orderI123
     * @return
     */
    @RequestMapping("/submit")
    public Result submit(@RequestBody OrdersI1_2_3 orderI123){
        Orders orders = new Orders();
        orders.setId(Simplify.getUUID());
        orders.setMyId(orderI123.getMyId());
        orders.setYouId(orderI123.getYouId());
        orders.setOrderType(orderI123.getOrderType());
        orders.setConsultType(orderI123.getConsultType());
        orders.setContent(orderI123.getContent());
        orders.setPrice(orderI123.getPrice());
        ordersService.saveOrUpdate(orders);

        //默认自动给用户发送一条信息：
        Chat chat = new Chat();
        chat.setId(Simplify.getUUID());
        chat.setYouId(orderI123.getMyId());
        chat.setMyId(orderI123.getYouId());
        if(orderI123.getOrderType()==1){
            chat.setContent("您好，请问有什么能帮助您的吗？");
        }else {
            chat.setContent("您好，后边是我的电话，欢迎您的致电！"+lawyerService.getById(orderI123.getYouId()).getPhone());
        }
        chatService.saveOrUpdate(chat);

        return Result.success();
    }

    /**
     * 接口4-1-1
     * @param myId
     * @return
     */
    @RequestMapping("/load")
    public Result load(@RequestParam String myId){
        QueryWrapper<Orders> queryWrapper = new QueryWrapper<>();
        queryWrapper.and(qw->{
            qw.or();
            qw.eq("my_id",myId);
            qw.or();
            qw.eq("you_id",myId);
        });
        List<Orders> ordersList = ordersService.list(queryWrapper);
        List<OrdersO4_1_1> ordersO411List = new ArrayList<>();
        ordersList.forEach(item->{
            OrdersO4_1_1 ordersO411 = new OrdersO4_1_1();
            ordersO411.setOrderId(item.getId());
            ordersO411.setOrderType(item.getOrderType());
            ordersO411.setPrice(item.getPrice());
            ordersO411.setTime(item.getCreateTime().substring(0,item.getCreateTime().lastIndexOf(':')));
            if (item.getMyId().equals(myId)){
                ordersO411.setYouId(item.getYouId());
                ordersO411.setDirect(1);
                ordersO411.setName(userService.getById(item.getYouId()).getName());
                ordersO411.setAvatar(userService.getById(item.getYouId()).getAvatar());
            }else if(item.getYouId().equals(myId)){
                ordersO411.setYouId(item.getMyId());
                ordersO411.setDirect(0);
                ordersO411.setName(userService.getById(item.getMyId()).getName());
                ordersO411.setAvatar(userService.getById(item.getMyId()).getAvatar());
            }
            ordersO411List.add(ordersO411);

        });
        return Result.success(ordersO411List);
    }

    /**
     * 接口4-1-2
     * @param id 订单id
     * @return
     */
    @RequestMapping("/show")
    public Result show(@RequestParam String id){
        Orders orders = ordersService.getById(id);
        OrdersO4_1_2 ordersO412 = new OrdersO4_1_2();
        ordersO412.setConsultType(orders.getConsultType());
        ordersO412.setContent(orders.getContent());
        return Result.success(ordersO412);
    }

    /**
     * 后台 增
     * @param orders
     * @return
     */
    @RequestMapping("/bgNewOrders")
    public Result bgNewOrder(@RequestBody Orders orders){
        orders.setId(Simplify.getUUID());
        ordersService.saveOrUpdate(orders);
        return Result.success();
    }

    /**
     * 后台 删
     * @param id
     * @return
     */
    @RequestMapping("/bgDelOrders")
    public Result bgDelOrders(@RequestParam String id){
        Orders orders = ordersService.getById(id);
        orders.setIsDelete(1);
        ordersService.saveOrUpdate(orders);
        return Result.success();
    }

    /**
     * 后台 改
     * @param orders
     * @return
     */
    @RequestMapping("/bgUpdOrders")
    public Result bgUpdOrder(@RequestBody Orders orders){
        ordersService.saveOrUpdate(orders);
        return Result.success();
    }

    /**
     * 后台 查
     * @param search
     * @return
     */
    @RequestMapping("/bgSelOrders")
    public Result bgSelOrders(@RequestParam(required = false) String search,
                              @RequestParam Integer pageCurr,
                              @RequestParam Integer pageSize){
        IPage<Orders> page = new Page<>(pageCurr,pageSize);
        QueryWrapper<Orders> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("is_delete",0);
        queryWrapper.like(null!=search,"content",search);
        queryWrapper.orderByDesc("create_time");
        return Result.success(ordersService.page(page,queryWrapper));
    }
}
