package com.fw.springboot8583.bbw;

import com.github.kpavlov.jreactive8583.IsoMessageListener;
import com.solab.iso8583.IsoMessage;
import io.netty.channel.ChannelHandlerContext;

public class BBWMessageListener implements IsoMessageListener<IsoMessage> {

  private IsoMessage result;

  public BBWMessageListener() {
    this.result = null;
  }

  public IsoMessage getResult() {
    return result;
  }

  public void setResult(IsoMessage isoMessage) {
    result = isoMessage;
  }

  @Override
  public boolean applies(IsoMessage isoMessage) {
    return true;
  }

  @Override
  public boolean onMessage(ChannelHandlerContext channelHandlerContext, IsoMessage isoMessage) {
    result = isoMessage;
    return true;
  }

}
