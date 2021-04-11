package me.ote.polishcalc;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.quarkus.test.junit.QuarkusTest;
import me.ote.polishcalc.api.protocol.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

@QuarkusTest
public class RemotePolishCalculatorTest {

    @Inject
    RemotePolishCalculator remotePolishCalculator;

    final RequestFrameHelper requestFrameHelper = new RequestFrameHelper();
    final ResponseFrameHelper responseFrameHelper = new ResponseFrameHelper();

    @BeforeEach
    void afterTest() throws InterruptedException {
        remotePolishCalculator.run();
    }

    @AfterEach
    void beforeTest() throws InterruptedException {
        remotePolishCalculator.stop();
    }

    @Test
    public void testHelloFrame() throws InterruptedException {
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        final EventLoopGroup eventLoopGroup = new NioEventLoopGroup();
        final AtomicReference<ResponseFrame> frameAtomicReference = new AtomicReference<>();
        ChannelFuture futureClient = ClientFactory.createClient(eventLoopGroup, new ChannelInboundHandlerAdapter() {

            @Override
            public void channelActive(ChannelHandlerContext ctx) throws Exception {
                ByteBuf buffer = Unpooled.buffer();
                buffer.writeBytes(requestFrameHelper.buildFrame(HelloFrame.create()));
                ctx.writeAndFlush(buffer);
            }

            @Override
            public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                ByteBuf byteBuf = (ByteBuf) msg;
                byte[] rawFrame = new byte[byteBuf.readableBytes()];
                ((ByteBuf) msg).readBytes(rawFrame);
                frameAtomicReference.set(responseFrameHelper.readFrame(rawFrame));
                countDownLatch.countDown();
            }

        }).connect("localhost", 41000).sync();
        Assertions.assertTrue(countDownLatch.await(2000, TimeUnit.MILLISECONDS));
        ResponseFrame requestFrame = frameAtomicReference.get();
        Assertions.assertEquals(0, requestFrame.getMessageId());
        Assertions.assertArrayEquals(requestFrame.getPayload(), new byte[]{0x06});
        futureClient.channel().close();
        eventLoopGroup.shutdownGracefully();
    }

    @Test
    public void testOperationFrame() throws InterruptedException, PayloadFormatException {
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        final EventLoopGroup eventLoopGroup = new NioEventLoopGroup();
        final AtomicReference<ResponseFrame> frameAtomicReference = new AtomicReference<>();
        ChannelFuture futureClient = ClientFactory.createClient(eventLoopGroup, new ChannelInboundHandlerAdapter() {

            @Override
            public void channelActive(ChannelHandlerContext ctx) throws Exception {
                ByteBuf buffer = Unpooled.buffer();
                try {
                    buffer.writeBytes(requestFrameHelper.buildFrame(OperationFrame.create(100, PayloadAdapter.compress("1 2 3 + *"))));
                } catch (PayloadFormatException e) {
                    e.printStackTrace();
                }
                ctx.writeAndFlush(buffer);
            }

            @Override
            public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                ByteBuf byteBuf = (ByteBuf) msg;
                byte[] rawFrame = new byte[byteBuf.readableBytes()];
                ((ByteBuf) msg).readBytes(rawFrame);
                frameAtomicReference.set(responseFrameHelper.readFrame(rawFrame));
                countDownLatch.countDown();
            }
        }).connect("localhost", 41000).sync();
        Assertions.assertTrue(countDownLatch.await(5000, TimeUnit.MILLISECONDS));

        ResponseFrame requestFrame = frameAtomicReference.get();
        Assertions.assertEquals(100, requestFrame.getMessageId());
        Assertions.assertArrayEquals(PayloadAdapter.uncompress(requestFrame.getPayload()).trim().getBytes(StandardCharsets.UTF_8), new byte[]{'5'});

        futureClient.channel().close();
        eventLoopGroup.shutdownGracefully();
    }

    @Test
    public void testByeFrame() throws InterruptedException {
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        final EventLoopGroup eventLoopGroup = new NioEventLoopGroup();
        final AtomicReference<ResponseFrame> frameAtomicReference = new AtomicReference<>();
        ChannelFuture futureClient = ClientFactory.createClient(eventLoopGroup, new ChannelInboundHandlerAdapter() {

            @Override
            public void channelActive(ChannelHandlerContext ctx) throws Exception {
                ByteBuf buffer = Unpooled.buffer();
                buffer.writeBytes(requestFrameHelper.buildFrame(ByeFrame.create(1800)));
                ctx.writeAndFlush(buffer);
            }

            @Override
            public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                ByteBuf byteBuf = (ByteBuf) msg;
                byte[] rawFrame = new byte[byteBuf.readableBytes()];
                ((ByteBuf) msg).readBytes(rawFrame);
                frameAtomicReference.set(responseFrameHelper.readFrame(rawFrame));
                countDownLatch.countDown();
            }
        }).connect("localhost", 41000).sync();
        Assertions.assertTrue(countDownLatch.await(2000, TimeUnit.MILLISECONDS));

        ResponseFrame requestFrame = frameAtomicReference.get();
        Assertions.assertEquals(1800, requestFrame.getMessageId());
        Assertions.assertArrayEquals(requestFrame.getPayload(), new byte[]{'B','Y','E'});

        futureClient.channel().close();
        eventLoopGroup.shutdownGracefully();
    }

}
