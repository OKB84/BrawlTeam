package com.example.demo.task;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.example.demo.controller.dto.ChampionshipReminderDto;
import com.example.demo.service.ChampionshipService;
import com.example.demo.service.MailService;

/**
 * @author root1
 * 定期実行するタスクの処理内容クラス
 */
@Component
public class ScheduledTasks {

	@Autowired
	ChampionshipService championshipService;

	@Autowired
	MailService mailService;

	// 大会リマインドメールを送信（毎日12時）
	@Scheduled(cron = "0 0 12 * * *", zone = "Asia/Tokyo")
	public void sendReminderMail() {

        // 翌日開催の大会リストを取得
		List<ChampionshipReminderDto> championshipList = championshipService.getTomorrowChampionship();

		// 各大会の作成者にメール送信
		mailService.sendChampionshipReminderMail(championshipList);
	}
}
