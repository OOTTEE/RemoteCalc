package me.ote.polishcalc;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import me.ote.polishcalc.api.protocol.*;
import me.ote.polishcalc.calculator.CalculatorService;
import me.ote.polishcalc.calculator.Operation;
import me.ote.polishcalc.calculator.OperationFactory;
import me.ote.polishcalc.server.Server;
import org.jboss.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class RemotePolishCalculator {

    @Inject
    Logger logger;

    @Inject
    Server server;

    @Inject
    CalculatorService calculatorService;

    public void run() throws InterruptedException {
        logger.info(String.format("Start server on port: %s:", server.getListenPort()));
        server.setClientHandlerProvider(() -> new ChannelInboundHandlerAdapter() {

            @Override
            public void channelRegistered(ChannelHandlerContext ctx) {
                logger.info(String.format("<<< [ip:%s] Client connected", ctx.channel().remoteAddress()));
            }

            @Override
            public void channelUnregistered(ChannelHandlerContext ctx) {
                logger.info(String.format("<<< [ip:%s] Client disconnected", ctx.channel().remoteAddress()));
            }

            @Override
            public void channelRead(ChannelHandlerContext ctx, Object msg) {

            }

        });
        server.start();
    }

    public void stop() throws InterruptedException {
        server.stop();
    }

}
