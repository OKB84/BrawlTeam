package com.example.demo.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.example.demo.entity.OwnBrawlerEntity;

/**
 * own_brawlerテーブルのレコードをマッピングする際に使用するインターフェース
 * @author root1
 *
 */
@Mapper
public interface OwnBrawlerMapper {

	public int create(OwnBrawlerEntity entity);

	public List<String> search(String playerTag);

}
