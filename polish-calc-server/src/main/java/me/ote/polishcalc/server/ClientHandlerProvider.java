package me.ote.polishcalc.server;

import io.netty.channel.ChannelInboundHandlerAdapter;

public interface ClientHandlerProvider<P extends ChannelInboundHandlerAdapter> {

    P provide();
}
