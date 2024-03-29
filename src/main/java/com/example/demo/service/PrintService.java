package com.example.demo.service;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

import com.example.demo.controller.dto.ChampionshipDetailDto;
import com.example.demo.controller.dto.LeagueRoundDto;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.util.JRLoader;

/**
 * @author root1
 * PDF出力を行うクラス
 */
@Service
public class PrintService {

	@Autowired
	ResourceLoader resourceLoader;

	// チームメンバーPDFを作成
	public void printTeamList(ChampionshipDetailDto dto, HttpServletResponse response) {
		try {

			System.setProperty("java.awt.headless", "true");

			URL urlMainReport = this.getClass().getResource("/report/team.jasper");
			URL urlSubReport = this.getClass().getResource("/report/teamList.jasper");

			// 帳票レイアウトのロード
			JasperReport jasperReport = (JasperReport)JRLoader.loadObject(urlMainReport.openStream());

			// 帳票レイアウトのロード
			JasperReport subReport = (JasperReport)JRLoader.loadObject(urlSubReport.openStream());

			// データ作成（パラメータ）
			HashMap<String,  Object> params = new HashMap<String,  Object>();
			// チーム番号
			params.put("championshipName", dto.getChampionshipName());

			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/M/d (E) HH:mm", Locale.JAPANESE);

			// チーム名
			params.put("date",  dateFormat.format(dto.getDate()));
			// サブレポート
			params.put("subReport", subReport);

			// データの設定
			JasperPrint print = JasperFillManager.fillReport(
										jasperReport,
										params,
										new JRBeanCollectionDataSource(dto.getTeamList()));

		    response.setContentType("application/x-download");
		    response.addHeader("Content-disposition", "attachment; filename=championship" + dto.getId() + ".pdf");
		    OutputStream out = response.getOutputStream();
		    JasperExportManager.exportReportToPdfStream(print,out);//export PDF directly


		} catch (IOException e) {
			e.printStackTrace();
			System.out.print(e.getMessage());
		} catch (JRException e) {
			e.printStackTrace();
			System.out.print(e.getMessage());
		}
	}

	// リーグ戦の対戦表PDFを作成
	public void printLeagueMatchPattern(List<LeagueRoundDto> list, HttpServletResponse response) {
		try {

			System.setProperty("java.awt.headless", "true");

			URL urlMainReport = this.getClass().getResource("/report/league.jasper");
			URL urlSubReport = this.getClass().getResource("/report/match.jasper");

			// 帳票レイアウトのロード
			JasperReport jasperReport = (JasperReport)JRLoader.loadObject(urlMainReport.openStream());

			// 帳票レイアウトのロード
			JasperReport subReport = (JasperReport)JRLoader.loadObject(urlSubReport.openStream());

			// データ作成（パラメータ）
			HashMap<String,  Object> params = new HashMap<String,  Object>();

			// サブレポート
			params.put("subReport", subReport);

			// データの設定
			JasperPrint print = JasperFillManager.fillReport(
										jasperReport,
										params,
										new JRBeanCollectionDataSource(list));

		    response.setContentType("application/x-download");
		    response.addHeader("Content-disposition", "attachment; filename=league.pdf");
		    OutputStream out = response.getOutputStream();
		    JasperExportManager.exportReportToPdfStream(print,out);//export PDF directly


		} catch (IOException e) {
			e.printStackTrace();
			System.out.print(e.getMessage());
		} catch (JRException e) {
			e.printStackTrace();
			System.out.print(e.getMessage());
		}
	}
}
