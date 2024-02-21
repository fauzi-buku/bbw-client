package com.fw.springboot8583.web.api;

import com.fw.springboot8583.bbw.BBWClient;
import com.solab.iso8583.IsoMessage;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class HomeController {

  private BBWClient bbwClient;

  public HomeController(BBWClient bbwClient) throws InterruptedException {
    this.bbwClient = bbwClient;
  }

  /**
   *
   */
  @GetMapping("/send-async-echo-to-bbw")
  public ResponseEntity<?> sendAsyncEchoToBbw(@RequestParam(name = "stan", required = true) String stan) throws InterruptedException {
    IsoMessage isoMessage = bbwClient.sendAsyncEcho(stan);
    String temp = isoMessage.debugString();
    String temp2 = stan.equals(temp.substring(46, 52)) ? "" : "NOOOOOOOOOOOOOOOO\n";
    return ResponseEntity.ok().body(stan + " -- " + isoMessage.getField(11) + " " + temp2 + "\n");
  }

  /**
   *
   */
  @GetMapping("/test-balance-inquiry")
  public ResponseEntity<?> balanceInquiry() throws InterruptedException {
    return ResponseEntity.ok().body(bbwClient.balanceInquiry().debugString() + "\n");
  }

}
