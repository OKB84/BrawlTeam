package com.example.demo.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.controller.dto.BrawlerStat;
import com.example.demo.entity.BrawlerMasterEntity;
import com.example.demo.mapper.BrawlerMasterMapper;

/**
 * キャラクターマスタに関するサービスクラス
 * @author root1
 *
 */
@Service
public class BrawlerMasterService {

	@Autowired
	BrawlerMasterMapper mapper;

	// レコード新規作成
	public int create(List<BrawlerStat> brawlers) {

		// 詰め替え用Entity
		BrawlerMasterEntity entity = new BrawlerMasterEntity();

		int insertSuccessCount = 0;		// INSERT成功回数

		for(BrawlerStat b : brawlers) {
			entity.setId(b.getId());
			entity.setName(b.getName());
			insertSuccessCount += mapper.create(entity);
		}

		return insertSuccessCount;
	}

	// 全レコード取得
	public List<BrawlerMasterEntity> getAll() {
		return mapper.getAll();
	}

	// 全レコードのキャラタイプ更新
	public int updateType(List<BrawlerMasterEntity> list) {

		int updateSuccessCount = 0;		// UPDATE成功回数

		for (BrawlerMasterEntity entity : list) {
			updateSuccessCount += mapper.updateType(entity);
		}

		return updateSuccessCount;
	}
}
