//package com.anime.shop.controller;
//
//import com.anime.shop.common.Result;
//import com.anime.shop.mock.GoodsVO;
//import com.anime.shop.mock.MockData;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//import java.util.List;
//
//@RestController
//@RequestMapping("/api/goods")
//public class GoodsController {
//
//    @GetMapping("/list")
//    public Result<List<GoodsVO>> list() {
//        return Result.ok(MockData.GOODS);
//    }
//}