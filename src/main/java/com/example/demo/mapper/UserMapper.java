package com.example.demo.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.example.demo.controller.dto.SocialMediaDto;
import com.example.demo.entity.UserEntity;

/**
 * userテーブルのレコードをマッピングする際に使用するインターフェース
 * @author root1
 *
 */
@Mapper
public interface UserMapper {

	public int create(SocialMediaDto dto);

	public List<UserEntity> searchBySocialMediaInfo(SocialMediaDto dto);

	public int updatePlayerTag(UserEntity entity);

	public int updateSocialMediaInfo(SocialMediaDto dto);

	public List<UserEntity> searchByUserId(int userId);

	public int delete(int userId);
}
