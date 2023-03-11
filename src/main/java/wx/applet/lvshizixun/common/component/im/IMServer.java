package wx.applet.lvshizixun.common.component.im;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.stream.ChunkedWriteHandler;
import wx.applet.lvshizixun.common.component.im.handler.WebSocketHandler;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class IMServer {

    public static final Map<String, Channel> USERS = new ConcurrentHashMap<>(1024);

    public static void start() throws InterruptedException {
        EventLoopGroup boss = new NioEventLoopGroup();
        EventLoopGroup worker = new NioEventLoopGroup();

        ServerBootstrap bootstrap = new ServerBootstrap();
        bootstrap.group(boss,worker)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel socketChannel) throws Exception {
                        ChannelPipeline pipeline = socketChannel.pipeline();
                        //添加http编码解码器
                        pipeline.addLast(new HttpServerCodec())
                                //支持大数据流
                                .addLast(new ChunkedWriteHandler())
                                //对http消息做聚合操作
                                .addLast(new HttpObjectAggregator(1024*64))
                                //websocket
                                .addLast(new WebSocketServerProtocolHandler("/"))
                                .addLast(new WebSocketHandler());

                    }
                });
        bootstrap.bind(8080).sync();
    }
}
