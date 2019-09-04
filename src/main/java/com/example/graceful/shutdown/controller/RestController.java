package com.example.graceful.shutdown.controller;

import org.springframework.web.bind.annotation.GetMapping;

@org.springframework.web.bind.annotation.RestController
public class RestController {

  @GetMapping("/long-process")
  public String longProcess() throws InterruptedException {
    Thread.sleep(20000);
    System.out.println("end");
    return "process close";
  }

}
