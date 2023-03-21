package wx.applet.lvshizixun;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import wx.applet.lvshizixun.common.component.im.IMServer;

@SpringBootApplication
public class LvshizixunApplication {
    public static void main(String[] args){
        SpringApplication.run(LvshizixunApplication.class, args);
        try {
            IMServer.start();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
