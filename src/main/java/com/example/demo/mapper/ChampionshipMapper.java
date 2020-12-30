package com.example.demo.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.example.demo.controller.dto.ChampionshipCreateDto;
import com.example.demo.controller.dto.ChampionshipDateFormatedDto;
import com.example.demo.controller.dto.ChampionshipDetailDto;
import com.example.demo.controller.dto.ChampionshipDto;
import com.example.demo.entity.ChampionshipEntity;

/**
 * championshipテーブルのレコードをマッピングする際に使用するインターフェース
 * @author root1
 *
 */
@Mapper
public interface ChampionshipMapper {

	public int create(ChampionshipCreateDto dto);

	public int getCreatedId(ChampionshipCreateDto dto);

	public List<ChampionshipDetailDto> getDetail(ChampionshipCreateDto dto);

	public List<ChampionshipEntity> search(int userId);

	public int delete(ChampionshipDateFormatedDto dto);

	public int deleteBeforeUpdate(ChampionshipDto dto);

	public int update(ChampionshipCreateDto dto);
}
