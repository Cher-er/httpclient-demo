package com.cher.httpclient.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/redirect")
public class RedirectController {

    @GetMapping("/test1")
    public String test1() {
        return "redirect:/redirect/test2";
    }

    @GetMapping("/test2")
    public String test2() {
        return "redirect:/redirect/test3";
    }

    @GetMapping("/test3")
    @ResponseBody
    public String test3() {
        return "hello, redirect!";
    }
}
