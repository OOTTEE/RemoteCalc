package me.ote.polishcalc.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.quarkus.runtime.Quarkus;
import io.quarkus.runtime.QuarkusApplication;
import me.ote.polishcalc.api.protocol.*;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;

import javax.inject.Inject;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class PolishCalcClientMain implements QuarkusApplication {
    private static final Integer DEFAULT_PORT = 41000;
    private static final Integer MAX_ID = 65535;

    @Inject
    Logger log;

    @ConfigProperty(name = "server.port", defaultValue = "41000")
    int serverPort;
    @ConfigProperty(name = "server.host", defaultValue = "127.0.0.1")
    String serverHost;

    @Inject
    StdinReader stdinReader;

    Map<Integer, RequestFrame> pendingOps = new HashMap<>();

    @Override
    public int run(String... args) throws Exception {
        log.info("Start client.");
        log.info(String.format("Connecting with: %s:%s", serverHost, serverPort));
        Bootstrap bootstrap = createClient(new ChannelInboundHandlerAdapter() {
            final RequestFrameHelper requestFrameHelper = new RequestFrameHelper();
            final ResponseFrameHelper responseFrameHelper = new ResponseFrameHelper();
            final AtomicInteger messageId = new AtomicInteger(0);

            @Override
            public void channelActive(ChannelHandlerContext ctx) throws Exception {
                log.info("Connection open");
                byte[] frame = requestFrameHelper.buildFrame(HelloFrame.create(messageId.getAndIncrement()));
                sendFrame(ctx, frame);
                stdinReader.read(s -> {
                    if(s.equals("quit")) {
                        ByeFrame byeFrame = ByeFrame.create(messageId.getAndIncrement());
                        pendingOps.put(byeFrame.getMessageId(), byeFrame);
                        sendFrame(ctx, requestFrameHelper.buildFrame(byeFrame));
                    } else {
                        OperationFrame operationFrame = OperationFrame.create(messageId.getAndIncrement(), s.getBytes(StandardCharsets.UTF_8));
                        pendingOps.put(operationFrame.getMessageId(), operationFrame);
                        sendFrame(ctx, requestFrameHelper.buildFrame(operationFrame));
                    }
                    if(messageId.get() > MAX_ID ) {
                        messageId.set(0);
                    }
                });
            }

            @Override
            public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
                log.info("Connection lost, exit.");
                stdinReader.close();
                Quarkus.asyncExit();
            }

            @Override
            public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                ByteBuf inBuf = (ByteBuf) msg;
                byte[] bytes = new byte[inBuf.readableBytes()];
                ((ByteBuf) msg).readBytes(bytes);
                ResponseFrame responseFrame = responseFrameHelper.readFrame(bytes);

                if(responseFrame.getPayload().length > 0 && Arrays.equals(responseFrame.getPayload(), ResponseFrameHelper.ACK)){
                    log.info("Connected to server");
                } else if(responseFrame.getPayload().length > 0 && Arrays.equals(responseFrame.getPayload(), ResponseFrameHelper.BYE_PAYLOAD)) {
                    log.info("Close connection");
                    ctx.channel().close();
                } else if(responseFrame.getPayload().length > 0) {
                    OperationFrame operationFrame = (OperationFrame) pendingOps.get(responseFrame.getMessageId());
                    if(operationFrame != null) {
                        log.info(String.format("result of [%s] is %s", operationFrame.getStringPayload(), new String(responseFrame.getPayload(), StandardCharsets.UTF_8)));
                    }
                } else {
                    log.info("unkown server response");
                }
            }

            private void sendFrame(ChannelHandlerContext ctx, byte[] frame) {
                ByteBuf byteBuf = Unpooled.buffer(frame.length);
                byteBuf.writeBytes(frame);
                ctx.writeAndFlush(byteBuf);
            }
        });
        bootstrap.connect(serverHost, serverPort).sync();
        Quarkus.waitForExit();
        return 0;
    }

    private void startInputReader() {

    }

    private Bootstrap createClient(ChannelInboundHandlerAdapter handler) {
        // Configure the client.
        Bootstrap b = new Bootstrap();
        b.group(new NioEventLoopGroup())
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

    private Integer getPortFromArgs(String ... args) {
        if (args.length > 2) {
            try {
                return Integer.parseInt(args[1]);
            } catch (NumberFormatException e) {
                return DEFAULT_PORT;
            }
        } else {
            return DEFAULT_PORT;
        }
    }

    private String getAddrFromArgs(String ... args) {
        if (args.length >= 1) {
            return args[0];
        } else {
            log.error("Missing host address ");
            Quarkus.asyncExit(1);
            throw new IllegalArgumentException("Missing host address");
        }
    }
}
