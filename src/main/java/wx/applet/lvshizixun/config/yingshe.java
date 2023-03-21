package wx.applet.lvshizixun.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

/**
 * ⽂件路径配置，可以直接访问
 * 映射的路径后⾯必须加/，否则访问不到
 */
@Configuration
public class yingshe extends WebMvcConfigurerAdapter {
@Override
public void addResourceHandlers(ResourceHandlerRegistry registry) {
//和页⾯有关的静态⽬录都放在项⽬的static⽬录下
        registry.addResourceHandler("/upload/**").addResourceLocations("file:/Users/qwertyuiop/Documents/lvshizixun/");
//        registry.addResourceHandler("/upload/**").addResourceLocations("file:/blender/");
        }
}

