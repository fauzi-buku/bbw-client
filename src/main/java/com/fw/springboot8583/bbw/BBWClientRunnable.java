package com.fw.springboot8583.bbw;

import com.github.kpavlov.jreactive8583.client.Iso8583Client;
import com.solab.iso8583.IsoMessage;

public class BBWClientRunnable implements Runnable {

  private Iso8583Client<IsoMessage> iso8583Client;
  private BBWMessageListener bbwMessageListener;
  private IsoMessage isoMessage;

  public BBWClientRunnable(Iso8583Client<IsoMessage> iso8583Client, BBWMessageListener bbwMessageListener, IsoMessage isoMessage) {
    this.iso8583Client = iso8583Client;
    this.bbwMessageListener = bbwMessageListener;
    this.isoMessage = isoMessage;
    iso8583Client.addMessageListener(bbwMessageListener);
  }

  public Iso8583Client<IsoMessage> getIso8583Client() {
    return iso8583Client;
  }

  public void setIso18583Client(Iso8583Client<IsoMessage> iso8583Client) {
    this.iso8583Client = iso8583Client;
  }

  public BBWMessageListener getBBWMessageListener() {
    return bbwMessageListener;
  }

  public void setBBWMessageListener(BBWMessageListener bbwMessageListener) {
    this.bbwMessageListener = bbwMessageListener;
  }

  @Override
  public void run() {
    iso8583Client.sendAsync(isoMessage);
  }

}
