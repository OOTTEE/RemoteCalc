package me.ote.polishcalc;

import io.netty.buffer.ByteBuf;
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
                final RequestFrameHelper requestFrameHelper = new RequestFrameHelper();
                final ByteBuf byteBuf = (ByteBuf) msg;
                final byte[] receivedBytes = new byte[byteBuf.readableBytes()];
                byteBuf.readBytes(receivedBytes);
                try {
                    final RequestFrame requestFrame = requestFrameHelper.readFrame(receivedBytes);
                    if (requestFrame instanceof HelloFrame) {
                        handlerHelloFrame(ctx, (HelloFrame) requestFrame);
                        return;
                    } else if (requestFrame instanceof OperationFrame) {
                        handlerOperationFrame(ctx, (OperationFrame) requestFrame);
                        return;
                    } else if (requestFrame instanceof ByeFrame) {
                        handlerByeFrame(ctx, requestFrame);
                        return;
                    }
                    handlerUnexpectedFrame(ctx, requestFrame.getMessageId());
                } catch (Exception e) {
                    handlerUnexpectedFrame(ctx, Integer.MAX_VALUE);
                }
            }

            private void handlerOperationFrame(ChannelHandlerContext ctx, OperationFrame operationFrame) {
                final ResponseFrameHelper responseFrameHelper = new ResponseFrameHelper();
                final Operation operation = OperationFactory.createFromChain(operationFrame.getStringPayload());
                try {
                    logger.info(String.format("<<< [ip:%s] Received OPERATION (Operation: %s)", ctx.channel().remoteAddress().toString(), operation.getOperationStr()));
                    Integer result = calculatorService.calculate(operation);
                    ResponseFrame response = responseFrameHelper.createResponse(operationFrame.getMessageId(), String.valueOf(result));
                    ctx.writeAndFlush(responseFrameHelper.buildFrame(response));
                    logger.info(String.format(">>> [ip:%s] Respond (Operation: %s, result: %s)", ctx.channel().remoteAddress(), operation.getOperationStr(), result));
                } catch (Exception e) {
                    logger.error(String.format("<<< [ip:%s] Received BAD OPERATION (Operation: %s)", ctx.channel().remoteAddress().toString(), operation.getOperationStr()));
                    ResponseFrame failResponse = responseFrameHelper.createFailResponse(operationFrame.getMessageId());
                    logger.info(String.format(">>> [ip:%s] Respond ERROR", ctx.channel().remoteAddress()));
                    ctx.writeAndFlush(responseFrameHelper.buildFrame(failResponse));
                }
            }

            private void handlerByeFrame(ChannelHandlerContext ctx, RequestFrame requestFrame) {
                final ResponseFrameHelper responseFrameHelper = new ResponseFrameHelper();
                logger.info(String.format("<<< [ip:%s] Received BYE frame from client", ctx.channel().remoteAddress().toString()));
                ResponseFrame byeResponse = responseFrameHelper.createByeResponse(requestFrame.getMessageId());
                ctx.writeAndFlush(responseFrameHelper.buildFrame(byeResponse));
                logger.info(String.format(">>> [ip:%s] Respond BYE ACK", ctx.channel().remoteAddress()));
                try {
                    logger.info(String.format(">>> [ip:%s] Close connection from server side", ctx.channel().remoteAddress()));
                    ctx.channel().close().sync();
                } catch (InterruptedException e) {
                    logger.error(String.format("Exception on close connection [ip: %s]", ctx.channel().remoteAddress()), e);
                }
            }


            private void handlerHelloFrame(ChannelHandlerContext ctx, HelloFrame helloFrame) {
                final ResponseFrameHelper responseFrameHelper = new ResponseFrameHelper();
                logger.info(String.format("<<< [ip:%s] Received HELLO", ctx.channel().remoteAddress()));
                ResponseFrame response = responseFrameHelper.createHelloResponse(helloFrame.getMessageId());
                ctx.writeAndFlush(response);
                logger.info(String.format(">>> [ip:%s] Respond HELLO ACK", ctx.channel().remoteAddress()));
            }

            private void handlerUnexpectedFrame(ChannelHandlerContext ctx, Integer messageId) {
                final ResponseFrameHelper responseFrameHelper = new ResponseFrameHelper();
                logger.error(String.format("<<< [ip:%s] Received UNEXPECTED frame", ctx.channel().remoteAddress()));
                ResponseFrame errorResponse = responseFrameHelper.createErrorResponse(messageId);
                ctx.writeAndFlush(responseFrameHelper.buildFrame(errorResponse));
                logger.info(String.format(">>> [ip:%s] Respond ERROR", ctx.channel().remoteAddress()));
            }
        });
        server.start();
    }

    public void stop() throws InterruptedException {
        server.stop();
    }

}
