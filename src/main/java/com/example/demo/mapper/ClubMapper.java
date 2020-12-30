package com.example.demo.mapper;

import org.apache.ibatis.annotations.Mapper;

import com.example.demo.entity.ClubEntity;

/**
 * clubテーブルのレコードをマッピングする際に使用するインターフェース
 * @author root1
 *
 */
@Mapper
public interface ClubMapper {

	public int create(ClubEntity entity);

}
