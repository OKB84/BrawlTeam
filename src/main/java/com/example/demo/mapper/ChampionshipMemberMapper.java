package com.example.demo.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.example.demo.entity.ChampionshipMemberEntity;

/**
 * championship_memberテーブルのレコードをマッピングする際に使用するインターフェース
 * @author root1
 *
 */
@Mapper
public interface ChampionshipMemberMapper {

	public int create(ChampionshipMemberEntity entity);

	public List<String> search(int userId);

	public int delete(ChampionshipMemberEntity entity);

	public List<String> searchIfExisted(ChampionshipMemberEntity entity);
}
