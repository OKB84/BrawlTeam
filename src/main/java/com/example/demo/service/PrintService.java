package com.example.demo.service;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Locale;

import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Service;

import com.example.demo.controller.dto.ChampionshipDetailDto;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.util.JRLoader;

@Service
public class PrintService {

	public void printTeamList(ChampionshipDetailDto dto, HttpServletResponse response) {
		try {

			System.setProperty("java.awt.headless", "true");

			// テンプレートの読み込み
			File jasperFile = new File("src/main/resources/report/team.jasper");
			File subReportFile = new File("src/main/resources/report/teamList.jasper");

			// 帳票レイアウトのロード
			JasperReport jasperReport = (JasperReport)JRLoader.loadObject(jasperFile.getAbsoluteFile());

			// 帳票レイアウトのロード
			JasperReport subReport = (JasperReport)JRLoader.loadObject(subReportFile.getAbsoluteFile());

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
			System.out.print(e.getMessage());
		} catch (JRException e) {
			System.out.print(e.getMessage());
		}

	}
}