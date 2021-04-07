package me.ote.polishcalc.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.quarkus.runtime.Quarkus;
import io.quarkus.runtime.QuarkusApplication;
import org.jboss.logging.Logger;

import javax.inject.Inject;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;

public class PolishCalcClientMain implements QuarkusApplication {
    private static final Integer DEFAULT_PORT = 41000;

    @Inject
    Logger log;

    final CountDownLatch countDownLatch = new CountDownLatch(1);

    @Override
    public int run(String... args) throws Exception {
        final String host = getAddrFromArgs(args);
        final Integer port = getPortFromArgs(args);
        Bootstrap bootstrap = createClient(new ChannelInboundHandlerAdapter() {
            @Override
            public void channelActive(ChannelHandlerContext ctx) throws Exception {

            }

            @Override
            public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
                log.info("Connection lost, exit.");
                Quarkus.asyncExit();
            }
        });
        bootstrap.connect(host, port).sync();
        Quarkus.waitForExit();
        return 0;
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
