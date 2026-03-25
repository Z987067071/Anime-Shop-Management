package com.anime.shop.mapper.impl;

import com.anime.shop.controller.dto.mine.AddressDTO;
import com.anime.shop.controller.dto.mine.AddressVO;
import com.anime.shop.entity.UserAddressEntity;
import com.anime.shop.mapper.AddressMapper;
import com.anime.shop.service.AddressService;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 地址Service实现（删除功能核心重构）
 */
@Service
public class AddressServiceImpl implements AddressService {

    @Resource
    private AddressMapper addressMapper;

    // 新增/编辑/查询方法保留（仅重构删除方法）

    /**
     * 删除地址（极简重构版）
     * 核心：直接调用自定义SQL删除，避免字段映射问题
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteAddress(Long id, Long userId) {
        // 1. 校验地址是否存在且归属当前用户
        UserAddressEntity address = addressMapper.selectAddressById(id);
        if (address == null) {
            throw new RuntimeException("地址不存在");
        }
        if (!address.getUserId().equals(userId)) {
            throw new RuntimeException("无权删除他人地址");
        }
        if (address.getIsDelete() == 1) {
            throw new RuntimeException("地址已删除");
        }

        // 2. 执行逻辑删除（直接调用自定义SQL，返回影响行数）
        int deleteCount = addressMapper.logicDeleteAddress(id, userId);
        if (deleteCount == 0) {
            throw new RuntimeException("删除地址失败");
        }

        // 3. 如果删除的是默认地址，重新设置第一个地址为默认
        if (address.getIsDefault() == 1) {
            List<UserAddressEntity> validList = addressMapper.selectValidAddressByUserId(userId);
            if (!validList.isEmpty()) {
                UserAddressEntity newDefault = validList.get(0);
                addressMapper.cancelAllDefault(userId); // 先取消所有默认
                newDefault.setIsDefault(1);
                addressMapper.updateById(newDefault); // 设置新默认
            }
        }
    }

    // 其他方法实现（保留）
    @Override
    public List<AddressVO> listAddress(Long userId) {
        List<UserAddressEntity> list = addressMapper.selectValidAddressByUserId(userId);
        return list.stream().map(this::convertToVO).collect(Collectors.toList());
    }

    @Override
    public AddressVO getDefaultAddress(Long userId) {
        UserAddressEntity entity = addressMapper.selectDefaultAddress(userId);
        return entity == null ? null : convertToVO(entity);
    }

    @Override
    public AddressVO getAddressById(Long id, Long userId) {
        UserAddressEntity entity = addressMapper.selectAddressById(id);
        if (entity == null || !entity.getUserId().equals(userId) || entity.getIsDelete() == 1) {
            throw new RuntimeException("地址不存在或无权限查看");
        }
        return convertToVO(entity);
    }

    @Override
    public void addAddress(AddressDTO dto) {
        UserAddressEntity entity = new UserAddressEntity();
        BeanUtils.copyProperties(dto, entity);
        entity.setIsDelete(0);
        if (dto.getIsDefault() == 1) {
            addressMapper.cancelAllDefault(dto.getUserId());
        } else if (addressMapper.selectDefaultAddress(dto.getUserId()) == null) {
            entity.setIsDefault(1);
        }
        addressMapper.insert(entity);
    }

    @Override
    public void editAddress(AddressDTO dto) {
        UserAddressEntity entity = addressMapper.selectAddressById(dto.getId());
        if (entity == null || !entity.getUserId().equals(dto.getUserId()) || entity.getIsDelete() == 1) {
            throw new RuntimeException("地址不存在或无权限编辑");
        }
        if (dto.getIsDefault() == 1) {
            addressMapper.cancelAllDefault(dto.getUserId());
        }
        UserAddressEntity updateEntity = new UserAddressEntity();
        BeanUtils.copyProperties(dto, updateEntity);
        addressMapper.updateById(updateEntity);
    }

    // VO转换
    private AddressVO convertToVO(UserAddressEntity entity) {
        AddressVO vo = new AddressVO();
        BeanUtils.copyProperties(entity, vo);
        vo.setFullAddress(entity.getProvince() + entity.getCity() + entity.getDistrict() + entity.getDetailAddress());
        return vo;
    }
}