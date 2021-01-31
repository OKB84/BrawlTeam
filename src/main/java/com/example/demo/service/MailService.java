package com.example.demo.service;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Service;

import com.example.demo.controller.dto.ChampionshipReminderDto;

/**
 * @author root1
 * メール送信処理に関するサービスクラス
 */
@Service
public class MailService {

	@Autowired
    private MailSender sender;

	// メール送信用アドレス及びパスワードを取得
	public static final String GMAIL_USER_NAME = System.getenv("GMAIL_USER_NAME");
	public static final String GMAIL_PASSWORD = System.getenv("GMAIL_PASSWORD");

	// 大会リマインドメールを送信（毎日12時）
	public void sendChampionshipReminderMail(List<ChampionshipReminderDto> championshipList) {

		// メーラーに送信元アドレスを設定
        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setFrom(GMAIL_USER_NAME + "@gmail.com");

		// 日付の表示形式を設定
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy年M月d日 (E) HH:mm", Locale.JAPANESE);

		// 各大会の作成者にメール送信
		for (ChampionshipReminderDto championship : championshipList) {

			msg.setTo(championship.getEmail());		// 宛先
	        msg.setSubject("明日の大会のお知らせ");		//タイトルの設定
	        msg.setText(createChampionshipReminderText(championship,
	        					dateFormat.format(championship.getDate())));	//本文の設定
	        // 送信
	        this.sender.send(msg);
		}
	}

	// 大会・ユーザ情報からメール本文を作成
	private String createChampionshipReminderText(ChampionshipReminderDto dto, String dateFormated) {

		String text = dto.getUserName() + " 様 \n\n"
				+ "いつも「Brawl Team」をご利用いただきまして誠にありがとうございます。\n"
				+ "ご登録されている大会の開催日前日となりましたので、ご連絡させていただきます。\n\n"
				+ "ご武運をお祈りいたします。\n\n\n"
				+ "【大会名】\n" + dto.getChampionshipName() + "\n\n"
				+ "【開催日時】\n" + dateFormated + "\n\n\n"
				+ "※本メールはお客様にご登録いただいたメールアドレスに発送しており、入力ミス等の理由により全く別の方にメールが届く可能性があります。\n"
				+ "もし本メールにお心当たりが無い場合は、お手数ですが、本メールの破棄をお願いします。\n\n"
				+ "※本メールにご返信いただきましても、対応いたしかねますので、何卒ご了承ください。\n\n"
				+ "Brawl Team\n\n"
				+ "https://guarded-journey-55505.herokuapp.com";

		return text;
	}
}
