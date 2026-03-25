package com.anime.shop.controller;

import com.anime.shop.common.BizException;
import com.anime.shop.common.Result;
import com.anime.shop.common.ResultCode;
import com.anime.shop.controller.dto.LoginDTO;
import com.anime.shop.controller.dto.RegisterDTO;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
public class TestController {

    @GetMapping("/test")
    public Result<String> test() {
        return Result.ok("hello anime-shop");
    }

    /* --- 故意抛个异常看效果 --- */
    @GetMapping("/err")
    public Result<String> err() {
        throw new RuntimeException("我故意的");
    }

    //测试400
    @GetMapping("/400")
    public Result<String> bad(@RequestParam("id") Integer id){
        if (id == null) throw new BizException(ResultCode.PARAM_ERROR);
        return Result.ok("ok");
    }

    //测试404
    @GetMapping("/404")
    public Result<String> notFound(){
        throw new BizException(ResultCode.NOT_FOUND);
    }

    //测试500
    @GetMapping("/500")
    public Result<String> inner(){
        int x = 1 / 0;          // 运行时异常
        return Result.ok("ok");
    }

    @PostMapping("/login")
    public Result<Map<String,String>> login(@RequestBody LoginDTO dto){
        // TODO 后续接真实业务
        Map<String,String> map = Map.of("token","fake-jwt-token-"+dto.getUsername());
        return Result.ok(map);
    }
    @PostMapping("/register")
    public Result<Void> register(@RequestBody RegisterDTO dto){
        return Result.ok(null);
    }
}