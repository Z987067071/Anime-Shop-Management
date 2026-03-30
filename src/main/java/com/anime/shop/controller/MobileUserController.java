package com.anime.shop.controller;

import com.anime.shop.common.Result;
import com.anime.shop.controller.dto.mine.MobileNicknameEditDTO;
import com.anime.shop.service.MobileUserService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/mobile/user")
public class MobileUserController {

    @Resource
    private MobileUserService mobileUserService;

    /** 修改昵称（需登录，userId 由拦截器注入） */
    @PostMapping("/editNickname")
    public Result<Void> editNickname(
            @Valid @RequestBody MobileNicknameEditDTO editDTO,
            @RequestAttribute Long userId) {
        return mobileUserService.editUsername(userId, editDTO.getNewNickname());
    }
}
