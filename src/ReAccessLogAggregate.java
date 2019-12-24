import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import domain.AccessLogData;
import domain.ResultData;

public class ReAccessLogAggregate {

	
	/**
	 * 最新版(時間帯が大きく開く場合を考慮すると128~131行目にやや修正が必要)
	 * @param args
	 */
	public static void main(String[] args) {

		// 1行目の出力
		System.out.println("  時刻  |500ms以下|2,000ms以下|2,001ms以上| 平均応答");

		LocalDateTime limitTime = LocalDateTime.of(2012, 4, 7, 0, 5);
		LocalDateTime endTime = LocalDateTime.of(2012, 4, 8, 0, 5);

		Map<LocalDateTime, ResultData> map = new LinkedHashMap<>();

		// 空のオブジェクトを先にmapに挿入
		for (limitTime = LocalDateTime.of(2012, 4, 7, 0, 5); limitTime
				.isBefore(endTime); limitTime = limitTime.plusMinutes(5)) {
			ResultData resultData = new ResultData(limitTime, 0, 0, 0, 0, 0, 0);
			map.put(limitTime, resultData);
		}

		// limitTimeを初期化
		limitTime = LocalDateTime.of(2012, 4, 7, 0, 5);
		int mapCounter = 0;
		int endPoint = map.size();
		
		try {
			// ファイルのパスを指定する
			File file = new File(
					"/Users/yuichi/Documents/workspace-spring/access-log-aggregate/documents/access_log.txt");
			// BufferedReaderクラスのreadLineメソッドを使って1行ずつ読み込む
			FileReader fileReader = new FileReader(file);
			BufferedReader bufferedReader = new BufferedReader(fileReader);
			String data;

			ResultData resultData = new ResultData();
			List<AccessLogData> inputDataList = new ArrayList<>();

			for (int i = 0; (data = bufferedReader.readLine()) != null; i++) {
				// 最終行の計算用
				mapCounter++;
				
				if (i == 0) {
					// 1行目はスキップ
					continue;
				}

				int under500 = 0;
				int under2000 = 0;
				int over2001 = 0;
				int totalResponceTime = 0;
				int count = 0;

				String[] oneLine = data.split("	");
				DateTimeFormatter f = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
				LocalDateTime inputDateTime = LocalDateTime.parse(oneLine[0], f);

				if (inputDateTime.isBefore(limitTime)) {
					AccessLogData accessLogData = new AccessLogData();
					accessLogData.setDateTime(inputDateTime);
					accessLogData.setPage(oneLine[1]);
					accessLogData.setResponceTime(Integer.parseInt(oneLine[2]));
					inputDataList.add(accessLogData);
				} else {
					for (AccessLogData accessLogData : inputDataList) {
						if (accessLogData.getResponceTime() <= 500) {
							under500++;
							totalResponceTime += accessLogData.getResponceTime();
							count++;
						} else if (500 <= accessLogData.getResponceTime() && accessLogData.getResponceTime() <= 2000) {
							under2000++;
							totalResponceTime += accessLogData.getResponceTime();
							count++;
						} else {
							over2001++;
							totalResponceTime += accessLogData.getResponceTime();
							count++;
						}
					}
					resultData = new ResultData();
					resultData.setOutputDateTime(limitTime);
					resultData.setUnder500(under500);
					resultData.setUnder2000(under2000);
					resultData.setOver2001(over2001);
					resultData.setTotalResponceTime(totalResponceTime);
					resultData.setCount(count);
					map.put(limitTime, resultData);
					
					// 5分進める
					limitTime = limitTime.plusMinutes(5);

					// それぞれの値をリセット
					under500 = 0;
					under2000 = 0;
					over2001 = 0;
					totalResponceTime = 0;
					count = 0;

					inputDataList = new ArrayList<>();
					// 次の時間帯の最初の1行目の処理
					if (inputDateTime.isBefore(limitTime)) {
						AccessLogData accessLogData = new AccessLogData();
						accessLogData.setDateTime(inputDateTime);
						accessLogData.setPage(oneLine[1]);
						accessLogData.setResponceTime(Integer.parseInt(oneLine[2]));
						inputDataList.add(accessLogData);
					} else {
						// 次の5分にデータがなかった場合
						limitTime = limitTime.plusMinutes(5);
						// 空白が10分以上続く場合(次の値がlimitTimeより5分圏内に入るまで5分プラスする)※要修正
//						while(limitTime.minusMinutes(5).isBefore(inputDateTime)) {
//							limitTime = limitTime.plusMinutes(5);
//						}
						AccessLogData accessLogData = new AccessLogData();
						accessLogData.setDateTime(inputDateTime);
						accessLogData.setPage(oneLine[1]);
						accessLogData.setResponceTime(Integer.parseInt(oneLine[2]));
						inputDataList.add(accessLogData);
					}
				}
				// 最終行の計算
				if(mapCounter != endPoint) {
					for (AccessLogData accessLogData : inputDataList) {
						if (accessLogData.getResponceTime() <= 500) {
							under500++;
							totalResponceTime += accessLogData.getResponceTime();
							count++;
						} else if (500 <= accessLogData.getResponceTime() && accessLogData.getResponceTime() <= 2000) {
							under2000++;
							totalResponceTime += accessLogData.getResponceTime();
							count++;
						} else {
							over2001++;
							totalResponceTime += accessLogData.getResponceTime();
							count++;
						}
					}
					resultData = new ResultData();
					resultData.setOutputDateTime(limitTime);
					resultData.setUnder500(under500);
					resultData.setUnder2000(under2000);
					resultData.setOver2001(over2001);
					resultData.setTotalResponceTime(totalResponceTime);
					resultData.setCount(count);
					map.put(limitTime, resultData);
				}
				
			}

			// mapに入れたResultDataをlimitTimeをキーにして順に出力する
			for (limitTime = LocalDateTime.of(2012, 4, 7, 0, 5); limitTime
					.isBefore(endTime); limitTime = limitTime.plusMinutes(5)) {
				resultData = map.get(limitTime);
				System.out.println(resultData);
			}

			// 最後にファイルを閉じてリソースを開放する
			bufferedReader.close();

		} catch (FileNotFoundException e) {
			// ファイルが存在しない場合の例外
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println(e);
		}

	}

}
