package com.example.demo.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.example.demo.controller.dto.ChampionshipMemberDto;
import com.example.demo.entity.BelongTeamEntity;

/**
 * belong_teamテーブルのレコードをマッピングする際に使用するインターフェース
 * @author root1
 *
 */
@Mapper
public interface BelongTeamMapper {

	public int create(BelongTeamEntity entity);

	public List<ChampionshipMemberDto> search(int teamId);
}
