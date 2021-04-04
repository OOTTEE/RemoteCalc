package me.ote.polishcalc.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class Server {
    @Inject
    Logger log;
    @ConfigProperty(name = "service.listen-port", defaultValue = "41000")
    int listenPort;
    private ChannelFuture serverSync;
    private SocketChannel socketChannel;
    private ClientHandlerProvider<?> clientHandlerProvider;
    private NioEventLoopGroup workerLoop;
    private NioEventLoopGroup groupLoop;

    public Server() {
    }

    public void setClientHandlerProvider(ClientHandlerProvider<?> clientHandlerProvider) {
        this.clientHandlerProvider = clientHandlerProvider;
    }

    public void start() throws InterruptedException {
        this.groupLoop = new NioEventLoopGroup();
        this.workerLoop = new NioEventLoopGroup();
        ServerBootstrap serverBootstrap = new ServerBootstrap();
        serverBootstrap.group(this.groupLoop, this.workerLoop)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel socketChannel) throws Exception {
                        Server.this.socketChannel = socketChannel;
                        if(clientHandlerProvider != null) {
                            socketChannel
                                    .pipeline()
                                    .addLast(clientHandlerProvider.provide());
                        }
                    }
                })
                .option(ChannelOption.SO_BACKLOG, 128)
                .childOption(ChannelOption.SO_KEEPALIVE, true);
        this.serverSync = serverBootstrap.bind(listenPort).sync();
    }

    public void stop() throws InterruptedException {
        this.socketChannel.close();
        this.socketChannel.parent().close();
        this.serverSync.channel().closeFuture().sync();
        this.groupLoop.shutdownGracefully();
        this.workerLoop.shutdownGracefully();
    }

    public int getListenPort() {
        return listenPort;
    }
}
