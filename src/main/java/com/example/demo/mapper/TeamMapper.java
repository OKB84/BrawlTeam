package com.example.demo.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.example.demo.controller.dto.ChampionshipDto;
import com.example.demo.entity.TeamEntity;

/**
 * teamテーブルのレコードをマッピングする際に使用するインターフェース
 * @author root1
 *
 */
@Mapper
public interface TeamMapper {

	public int create(TeamEntity entity);

	public int getCreatedId(TeamEntity entity);

	public List<TeamEntity> search(int championshipId);

	public int delete(ChampionshipDto dto);
}
