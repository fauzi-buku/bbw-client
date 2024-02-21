package com.fw.springboot8583;

import com.github.kpavlov.jreactive8583.IsoMessageListener;
import com.github.kpavlov.jreactive8583.iso.ISO8583Version;
import com.github.kpavlov.jreactive8583.iso.J8583MessageFactory;
import com.github.kpavlov.jreactive8583.server.Iso8583Server;
import com.github.kpavlov.jreactive8583.server.ServerConfiguration;
import com.solab.iso8583.IsoMessage;
import com.solab.iso8583.MessageFactory;
import com.solab.iso8583.parse.ConfigParser;
import io.netty.channel.ChannelHandlerContext;
import java.io.IOException;
import org.jetbrains.annotations.NotNull;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SpringBoot8583 {

  public static void main(String[] args) {
    try {
      ServerConfiguration serverConfiguration = ServerConfiguration.newBuilder()
        .addEchoMessageListener(false)
        .addLoggingHandler(false)
        .replyOnError(true)
        .logSensitiveData(false)
        .idleTimeout(0)
        .build();
      MessageFactory<IsoMessage> messageFactory = ConfigParser.createFromClasspathConfig("j8583.xml");
      J8583MessageFactory<IsoMessage> j8583MessageFactory = new J8583MessageFactory<>(messageFactory, ISO8583Version.V1987);
      Iso8583Server<IsoMessage> iso8583Server = new Iso8583Server<>(12345, serverConfiguration, j8583MessageFactory);
      iso8583Server.addMessageListener(new IsoMessageListener<>() {
        @Override
        public boolean applies(@NotNull IsoMessage isoMessage) {
          return true;
        }
        @Override
        public boolean onMessage(@NotNull ChannelHandlerContext channelHandlerContext, @NotNull IsoMessage isoMessage) {
          System.out.println("SERVER: onMessage: " + isoMessage.debugString());
          channelHandlerContext.writeAndFlush(isoMessage);
          return true;
        }
      });
      iso8583Server.init();
      iso8583Server.start();
    } catch(IOException | InterruptedException e) {
      e.printStackTrace();
    }
    /* */
    SpringApplication.run(SpringBoot8583.class, args);
  }

}
