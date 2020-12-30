package com.example.demo.mapper;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.example.demo.entity.BrawlerMasterEntity;

/**
 * brawler_masterテーブルのレコードをマッピングする際に使用するインターフェース
 * @author root1
 *
 */
@Mapper
public interface BrawlerMasterMapper {

	public int create(BrawlerMasterEntity entity);

	public List<BrawlerMasterEntity> search(String id);

}
