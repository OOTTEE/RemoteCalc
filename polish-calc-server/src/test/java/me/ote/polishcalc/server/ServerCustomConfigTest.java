package me.ote.polishcalc.server;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.TestProfile;
import org.jboss.logging.Logger;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

@QuarkusTest
@TestProfile(ServerProfiles.CustomPort.class)
public class ServerCustomConfigTest {
    @Inject
    Logger log;
    @Inject
    Server server;

    @Test
    public void configPortTest() {
        Assertions.assertEquals(50000, server.getListenPort());
    }

    @Test
    public void clientConnectionTest() throws InterruptedException {
        ChannelFuture clientFuture = null;
        try {
            CountDownLatch countDownLatch = new CountDownLatch(2);
            NioEventLoopGroup eventLoopGroup = new NioEventLoopGroup();
            server.setClientHandlerProvider(() -> new ChannelInboundHandlerAdapter() {
                @Override
                public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
                    countDownLatch.countDown();
                }
            });
            server.start();
            clientFuture = createClient(eventLoopGroup, new ChannelInboundHandlerAdapter() {
                @Override
                public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
                    countDownLatch.countDown();
                }
            }).connect("localhost", 50000).sync();
            clientFuture.channel().close();
            clientFuture.channel().closeFuture().sync();

            Assertions.assertTrue(countDownLatch.await(1000, TimeUnit.MILLISECONDS));
        } finally {
            if (clientFuture != null) {
                clientFuture.channel().closeFuture().sync();
            }
            server.stop();
        }
    }

    @Test
    public void clientDisconnectionTest() throws InterruptedException {
        ChannelFuture clientFuture = null;
        try {
            CountDownLatch countDownLatch = new CountDownLatch(2);
            NioEventLoopGroup eventLoopGroup = new NioEventLoopGroup();
            server.setClientHandlerProvider(() -> new ChannelInboundHandlerAdapter() {
                @Override
                public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
                    countDownLatch.countDown();
                }
            });
            server.start();
            clientFuture = createClient(eventLoopGroup, new ChannelInboundHandlerAdapter() {
                @Override
                public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
                    countDownLatch.countDown();
                }
            }).connect("localhost", 50000).sync();
            clientFuture.channel().close();
            clientFuture.channel().closeFuture().sync();
            Assertions.assertTrue(countDownLatch.await(1000, TimeUnit.MILLISECONDS));
        } finally {
            if (clientFuture != null) {
                clientFuture.channel().closeFuture().sync();
            }
            server.stop();
        }
    }

    @Test
    public void clientMessageTest() throws InterruptedException {
        AtomicReference<String> receivedString = new AtomicReference<>();
        try {
            CountDownLatch countDownLatch = new CountDownLatch(1);
            NioEventLoopGroup eventLoopGroup = new NioEventLoopGroup();
            server.setClientHandlerProvider(() -> new ChannelInboundHandlerAdapter() {
                @Override
                public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                    ByteBuf byteBuf = (ByteBuf) msg;
                    byte[] byteArray = new byte[byteBuf.readableBytes()];
                    byteBuf.readBytes(byteArray);
                    String message = new String(byteArray, StandardCharsets.UTF_8);
                    log.info(String.format("message received: %s", message));
                    receivedString.set(message);
                    countDownLatch.countDown();
                }
            });
            server.start();
            ChannelFuture clientFuture = createClient(eventLoopGroup, new ChannelInboundHandlerAdapter() {
                @Override
                public void channelActive(ChannelHandlerContext ctx) throws Exception {
                    ByteBuf buffer = Unpooled.buffer();
                    buffer.writeBytes("MESSAGE".getBytes(StandardCharsets.UTF_8));
                    ctx.writeAndFlush(buffer);
                }
            }).connect("localhost", server.getListenPort())
                    .sync();
            clientFuture.channel().close();
            Assertions.assertTrue(countDownLatch.await(1000, TimeUnit.MILLISECONDS));
            Assertions.assertEquals("MESSAGE", receivedString.get());
        } finally {
            server.stop();
        }
    }

    private Bootstrap createClient(EventLoopGroup group, ChannelInboundHandlerAdapter handler) {
        // Configure the client.
        Bootstrap b = new Bootstrap();
        b.group(group)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.TCP_NODELAY, true)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    public void initChannel(SocketChannel ch) {
                        ch.pipeline().addLast(handler);
                    }
                });
        return b;
    }

}
