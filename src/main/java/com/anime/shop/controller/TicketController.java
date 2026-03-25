//package com.anime.shop.controller;
//
//import com.anime.shop.common.Result;
//import com.anime.shop.mock.MockData;
//import com.anime.shop.mock.TicketVO;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//import java.util.List;
//
//@RestController
//@RequestMapping("/api/ticket")
//public class TicketController {
//
//    @GetMapping("/list")
//    public Result<List<TicketVO>> list() {
//        return Result.ok(MockData.TICKETS);
//    }
//}