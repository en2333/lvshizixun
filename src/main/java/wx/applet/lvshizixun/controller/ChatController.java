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
import wx.applet.lvshizixun.entity.viewobject.ChatO2_1_1;
import wx.applet.lvshizixun.entity.viewobject.ChatO2_2_1;
import wx.applet.lvshizixun.service.ChatService;
import wx.applet.lvshizixun.service.LawyerService;
import wx.applet.lvshizixun.service.UserService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

@RestController
@RequestMapping("/chat")
public class ChatController {
    @Autowired
    private ChatService chatService;

    @Autowired
    private UserService userService;

    /**
     * 接口2-1-1
     * @param myId 请求消息加载的用户ID
     * @return
     */
    @RequestMapping("/load")
    public Result load(@RequestParam String myId){
        Collection idList = new ArrayList<String>();
        List<Chat> chatList = new ArrayList<>();
        while (true){
            QueryWrapper<Chat> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("my_id",myId);
            if(idList.size()==0){
                queryWrapper.notIn("you_id",0);
            }else {
                queryWrapper.notIn("you_id",idList);
            }
            queryWrapper.orderByDesc("create_time").last("limit 1");
            List<Chat> chats = chatService.list(queryWrapper);
            if(chats.size()!=0){
                chatList.add(chats.get(0));
                idList.add(chats.get(0).getYouId());
            }else {
                break;
            }
        }
        Collection idList2 = new ArrayList<String>();
        List<Chat> chatList2 = new ArrayList<>();
        while (true){
            QueryWrapper<Chat> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("you_id",myId);
            if(idList2.size()==0){
                queryWrapper.notIn("my_id",0);
            }else {
                queryWrapper.notIn("my_id",idList2);
            }
            queryWrapper.orderByDesc("create_time").last("limit 1");
            List<Chat> chats = chatService.list(queryWrapper);
            if(chats.size()!=0){
                chatList2.add(chats.get(0));
                idList2.add(chats.get(0).getMyId());
            }else {
                break;
            }
        }
        List a=new ArrayList<>(idList);
        List b=new ArrayList<>(idList2);
        List a1=new ArrayList<>();

        for (int i = 0; i < a.size(); i++) {
            Boolean iDelete=false;//因为i删除时要在j循环外，所以需要定义一个变量来判断是否删除i
            for (int j = 0; j < b.size(); j++) {
                if(a.get(i).equals(b.get(j))){
                    if(chatList.get(i).getCreateTime().compareTo(chatList2.get(j).getCreateTime())>=0){
                        chatList2.remove(j);
                        b.remove(j);
                        j--;
                    }else {
                        iDelete=true;
                    }
                }
            }
            if(iDelete){
                chatList.remove(i);
                a.remove(i);
                i--;
            }
        }
        chatList2.forEach(item->{
            chatList.add(item);
        });

        List<ChatO2_1_1> chatO211List = new ArrayList<>();
        chatList.forEach(item->{
            ChatO2_1_1 chatO211 = new ChatO2_1_1();
            if(myId.equals(item.getMyId())){
                chatO211.setId(item.getYouId());
                chatO211.setName(userService.getById(item.getYouId()).getName());
                chatO211.setAvatar(userService.getById(item.getYouId()).getAvatar());
                chatO211.setWho(1);
            }else {
                chatO211.setId(item.getMyId());
                chatO211.setName(userService.getById(item.getMyId()).getName());
                chatO211.setAvatar(userService.getById(item.getMyId()).getAvatar());
                chatO211.setWho(0);
            }
            chatO211.setContent(item.getContent());
            chatO211.setTime(item.getCreateTime());
            chatO211.setState(item.getState());
            chatO211List.add(chatO211);
        });
        return Result.success(chatO211List);
    }

    /**
     * 接口2-2-1
     * @param myId
     * @param youId
     * @return
     */
    @RequestMapping("/loadChat")
    public Result loadChat(@RequestParam String myId,@RequestParam String youId){
        List<ChatO2_2_1> chatO221List = new ArrayList<>();
        QueryWrapper<Chat> queryWrapper = new QueryWrapper<>();
        queryWrapper.and(qw->{
            qw.or();
            qw.eq("my_id",myId);
            qw.eq("you_id",youId);
            qw.or();
            qw.eq("my_id",youId);
            qw.eq("you_id",myId);
        });
        queryWrapper.orderByAsc("create_time");
        chatService.list(queryWrapper).forEach(item->{
            ChatO2_2_1 chatO221 = new ChatO2_2_1();
            chatO221.setWho(item.getMyId().equals(myId)?1:0);
            chatO221.setMes(item.getContent());
            chatO221List.add(chatO221);
        });
        return Result.success(chatO221List);
    }

    /**
     * 接口2-2-2
     * @param myId
     * @param youId
     * @return
     */
    @RequestMapping("/mesRead")
    public Result mesRead(@RequestParam String myId,@RequestParam String youId){
        QueryWrapper<Chat> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("my_id",youId);
        queryWrapper.eq("you_id",myId);
        queryWrapper.eq("state",0);
        chatService.list(queryWrapper).forEach(item->{
            item.setState(1);
            chatService.saveOrUpdate(item);
        });

        return Result.success();
    }

    /**
     * 接口2-3-1
     * @param youId
     * @param myId
     * @param content
     * @return
     */
    @RequestMapping("/saveChat")
    public Result saveChat(@RequestParam String youId,@RequestParam String myId,@RequestParam String content){
        Chat chat = new Chat();
        chat.setId(Simplify.getUUID());
        chat.setMyId(myId);
        chat.setYouId(youId);
        chat.setState(0);
        chat.setContent(content);
        chat.setIsDelete(0);
        chatService.saveOrUpdate(chat);
        return Result.success();
    }

    /**
     * 后台 增
     * @param chat
     * @return
     */
    @RequestMapping("/bgNewChat")
    public Result bgNewChat(@RequestBody Chat chat){
        chat.setId(Simplify.getUUID());
        chatService.saveOrUpdate(chat);
        return Result.success();
    }

    /**
     * 后台 删
     * @param id
     * @return
     */
    @RequestMapping("/bgDelChat")
    public Result bgDelChat(@RequestParam String id){
        Chat chat = chatService.getById(id);
        chat.setIsDelete(1);
        chatService.saveOrUpdate(chat);
        return Result.success();
    }

    /**
     * 后台 改
     * @param chat
     * @return
     */
    @RequestMapping("/bgUpdChat")
    public Result bgUpdChat(@RequestBody Chat chat){
        chatService.saveOrUpdate(chat);
        return Result.success();
    }

    /**
     * 后台 查
     * @param search
     * @return
     */
    @RequestMapping("/bgSelChat")
    public Result bgSelOrders(@RequestParam(required = false) String search,
                              @RequestParam Integer pageCurr,
                              @RequestParam Integer pageSize){
        IPage<Chat> page = new Page<>(pageCurr,pageSize);
        QueryWrapper<Chat> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("is_delete",0);
        queryWrapper.like(null!=search,"content",search);
        queryWrapper.orderByDesc("create_time");
        return Result.success(chatService.page(page,queryWrapper));
    }
}
