package com.cher.httpclient.controller;

import com.cher.httpclient.pojo.MyJsonObject;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;

@RestController
@RequestMapping("/http")
public class HttpController {

    @GetMapping("/test")
    public String get() {
        return "hello, get!";
    }

    @PostMapping("/test")
    public String post(@RequestParam("username") String username,
                       @RequestParam("password") String password) {

        System.out.println("username: " + username);
        System.out.println("password: " + password);

        return "hello, post!";
    }

    @PostMapping("/entity")
    public String entity() {
        System.out.println("ok~");
        return "hello, entity!";
    }

    @PostMapping("/form")
    public String form(@RequestParam("param1") String param1,
                     @RequestParam("param2") String param2) {
        System.out.println(param1);
        System.out.println(param2);

        return "hello, form!";
    }

    @GetMapping("/json")
    public MyJsonObject json() {
        MyJsonObject myjson = new MyJsonObject();
        myjson.setUsername("cher");
        myjson.setPassword("123456");
        myjson.setAge(18);
        ArrayList<String> hobbies = new ArrayList<>();
        hobbies.add("program");
        hobbies.add("read");
        hobbies.add("guitar");
        myjson.setHobbies(hobbies);

        return myjson;
    }
}
