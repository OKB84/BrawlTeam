package com.example.demo.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.example.demo.controller.dto.ChampionshipMemberDto;
import com.example.demo.controller.dto.PlayerDetailDto;
import com.example.demo.entity.PlayerEntity;

/**
 * playerテーブルのレコードをマッピングする際に使用するインターフェース
 * @author root1
 *
 */
@Mapper
public interface PlayerMapper {

	public int createOnDuplicateKeyUpdate(PlayerEntity entity);

	public List<ChampionshipMemberDto> getMembersPlayerInfo(int userId);

	public int update(PlayerEntity entity);

	public PlayerEntity searchByPlayerTag(String playerTag);

	public PlayerDetailDto getPlayerDetail(String playerTag);
}
