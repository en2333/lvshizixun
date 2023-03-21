package wx.applet.lvshizixun.common.methods;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Simplify {

    /**
     * entity LIST列表转viewObject LIST列表
     * @param service
     * @param queryWrapper
     * @param viewObjName 要包复制类名，不加包路径的话找不到该类
     *
     * 该方法需要很多try catch，把try catch去掉，简写方便看，
     * 如下：
     * List list = new ArrayList();
     * Class clz = Class.forName(viewObjName);
     * Constructor constructor = clz.getConstructor();
     * service.list(queryWrapper).forEach(item->{
     *     Object object = constructor.newInstance();
     *     BeanUtils.copyProperties(item,object);
     *     list.add(object);
     * });
     *
     * @return
     */
    public static List entityToViews(ServiceImpl service, QueryWrapper queryWrapper, String viewObjName){
        List list = new ArrayList();
        Class clz = null;
        try {
            clz = Class.forName(viewObjName);
            Constructor constructor = clz.getConstructor();
            service.list(queryWrapper).forEach(item->{
                Object object = null;
                try {
                    object = constructor.newInstance();
                } catch (InstantiationException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
                BeanUtils.copyProperties(item,object);
                list.add(object);
            });
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }

        return list;
    }

    public static String getUUID(){
        String id = UUID.randomUUID().toString();
        String uid = id.replaceAll("-", "");
        return uid;
    }
}

