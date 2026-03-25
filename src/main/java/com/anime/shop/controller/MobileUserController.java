package com.anime.shop.controller;

import com.anime.shop.common.BizException;
import com.anime.shop.common.Result;
import com.anime.shop.common.ResultCode;
import com.anime.shop.controller.dto.mine.MobileNicknameEditDTO;
import com.anime.shop.service.MobileUserService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/mobile/user")
public class MobileUserController {

    @Resource
    private MobileUserService mobileUserService;

    /**
     * 移动端用户修改自身用户名
     * @param editDTO 修改参数
     * @param bindingResult 校验结果
     * @return 操作结果
     */
    @PostMapping("/editNickname")
    public Result<Void> editNickname(@Valid @RequestBody MobileNicknameEditDTO editDTO, BindingResult bindingResult) {
        // 1. 参数校验
        if (bindingResult.hasErrors()) {
            String errorMsg = bindingResult.getFieldErrors().stream()
                    .map(FieldError::getDefaultMessage)
                    .collect(Collectors.joining("；"));
            return Result.build(ResultCode.PARAM_ERROR.getCode(), errorMsg, null);
        }

        try {
            // 2. 转换用户ID为Long类型
            Long userId = Long.parseLong(editDTO.getUserId());

            // 3. 调用服务修改用户名
            return mobileUserService.editUsername(userId, editDTO.getNewNickname());
        } catch (NumberFormatException e) {
            return Result.build(ResultCode.PARAM_ERROR.getCode(), "用户ID格式错误，请传递数字", null);
        } catch (BizException e) {
            return Result.build(e.getCode(), e.getMessage(), null);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.build(ResultCode.SERVER_ERROR.getCode(), "用户名修改失败：" + e.getMessage(), null);
        }
    }
}