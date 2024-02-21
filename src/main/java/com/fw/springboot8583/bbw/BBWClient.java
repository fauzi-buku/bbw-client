package com.fw.springboot8583.bbw;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import org.springframework.stereotype.Component;
import com.github.kpavlov.jreactive8583.client.ClientConfiguration;
import com.github.kpavlov.jreactive8583.client.Iso8583Client;
import com.github.kpavlov.jreactive8583.iso.ISO8583Version;
import com.github.kpavlov.jreactive8583.iso.J8583MessageFactory;
import com.solab.iso8583.IsoMessage;
import com.solab.iso8583.IsoType;
import com.solab.iso8583.MessageFactory;
import com.solab.iso8583.parse.ConfigParser;

@Component
public class BBWClient {

  private SocketAddress socketAddress;
  private ClientConfiguration clientConfiguration;
  private MessageFactory<IsoMessage> messageFactory;
  private J8583MessageFactory<IsoMessage> j8583MessageFactory;
  private Iso8583Client<IsoMessage> iso8583Client;

  public BBWClient() throws InterruptedException, IOException {
    socketAddress = new InetSocketAddress("124.158.147.21", 3332);
    clientConfiguration = ClientConfiguration.newBuilder()
      .replyOnError(false)
      .reconnectInterval(5000)
      .addLoggingHandler(false)
      .addEchoMessageListener(false)
      .idleTimeout(0)
      .build();
    messageFactory = ConfigParser.createFromClasspathConfig("j8583.xml");
    j8583MessageFactory = new J8583MessageFactory<>(messageFactory, ISO8583Version.V1987);
    iso8583Client = new Iso8583Client<>(socketAddress, clientConfiguration, j8583MessageFactory);
    iso8583Client.init();
    iso8583Client.connect();
  }

  /**
   *
   */
  public synchronized IsoMessage sendAsyncEcho(String systemTraceAuditNumber) throws InterruptedException {
    IsoMessage isoMessage = iso8583Client.getIsoMessageFactory().newMessage(0x800);
    isoMessage.setValue(7, LocalDateTime.now().format(DateTimeFormatter.ofPattern("MMddHHmmss")), IsoType.DATE10, 10)
      .setValue(11, systemTraceAuditNumber, IsoType.NUMERIC, 6)
      .setValue(70, "301", IsoType.ALPHA, 3);
    return send(isoMessage);
  }

  /**
   *
   */
  public synchronized IsoMessage balanceInquiry() throws InterruptedException {
    IsoMessage isoMessage = iso8583Client.getIsoMessageFactory().newMessage(0x100);
    isoMessage.setValue(2, "622004124510999005", IsoType.LLVAR, 18)
      .setValue(3, "301000", IsoType.NUMERIC, 6)
      .setValue(11, "000147", IsoType.NUMERIC, 6)
      .setValue(14, "2401", IsoType.DATE4, 4)
      .setValue(22, "051", IsoType.NUMERIC, 3)
      .setValue(35, "622004124510999005D0000", IsoType.LLVAR, 23)
      .setValue(41, "36000014", IsoType.ALPHA, 8)
      .setValue(42, "360001000130001", IsoType.ALPHA, 15)
      .setValue(52, "1C28BD84BF475C54", IsoType.ALPHA, 16)
      .setValue(55, "820254009F360201E29F26083160489FABBEE2699F2701809F34030200009F100C0101A000810000DBC4EE2D009F3303E0F8C89F350122950500002400009F3704EB89F0C19F02060000000000009F03060000000000005A0852218400331105385F3401019F1A0203605F2A0203609A031205159C01309F090201019F1E0831323334353637384F07D36000000100008407D36000000100009F4103022786", IsoType.LLLVAR, 318)
      .setValue(62, "000003", IsoType.LLVAR, 6);
    return send(isoMessage);
  }

  /**
   *
   */
  public IsoMessage send(IsoMessage isoMessage) throws InterruptedException {
    BBWClientRunnable bbwClientRunnable = new BBWClientRunnable(iso8583Client, new BBWMessageListener(), isoMessage);
    new Thread(bbwClientRunnable).start();
    while(bbwClientRunnable.getBBWMessageListener().getResult() == null) {
      Thread.sleep(1);
    }
    return bbwClientRunnable.getBBWMessageListener().getResult();
  }

}
