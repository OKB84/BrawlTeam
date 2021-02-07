package com.example.demo.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.example.demo.controller.dto.OffsetForMedianDto;
import com.example.demo.entity.OwnBrawlerEntity;
import com.example.demo.entity.TrophyMedianEntity;

/**
 * own_brawlerテーブルのレコードをマッピングする際に使用するインターフェース
 * @author root1
 *
 */
@Mapper
public interface OwnBrawlerMapper {

	public int create(OwnBrawlerEntity entity);

	public List<String> search(String playerTag);

	public List<OffsetForMedianDto> getOffsetForMedian(String playerTag);

	public int getSingleMedian(TrophyMedianEntity entity);

	public int getEntireMedian(TrophyMedianEntity entity);
}
