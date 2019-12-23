import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import domain.AccessLogData;

public class AccessLogAggregate {

	// 現状の作りだとデータがない時間帯が3つ続くと値が変になる
	public static void main(String[] args) {

		try {
			// ファイルのパスを指定する
			File file = new File(
					"/Users/yuichi/Documents/workspace-spring/access-log-aggregate/documents/access_log.txt");
			// BufferedReaderクラスのreadLineメソッドを使って1行ずつ読み込み表示する
			FileReader fileReader = new FileReader(file);
			BufferedReader bufferedReader = new BufferedReader(fileReader);
			String data;
			List<AccessLogData> logList = new ArrayList<>();

			int year = 2012;
			int month = 04;
			int dayOfMonth = 07;

			// テキストデータを各要素ごとに1行ずつリストに格納
			for (int i = 0; (data = bufferedReader.readLine()) != null; i++) {
				if (i == 0) {
					// 1行目はスキップ
					continue;
				}
				// 1行分のデータを格納するオブジェクト
				AccessLogData accessLogData = new AccessLogData();

				// 日時の切り出し
				DateTimeFormatter f = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
				LocalDateTime logTime = LocalDateTime.parse(data.substring(0, 19), f);
				accessLogData.setDateTime(logTime);

				// ページの切り出し
				String page = data.substring(20, (data.indexOf(".html") + 5));
				accessLogData.setPage(page);

				// 応答時間の切り出し
				int responceTime = (int) Integer.parseInt(data.substring(data.indexOf(".html") + 6));
				accessLogData.setResponceTime(responceTime);

				logList.add(accessLogData);
			}

			LocalDateTime limitTime = LocalDateTime.of(year, month, dayOfMonth, 00, 05);

			// 1行目の出力
			System.out.println("  時刻  |500ms以下|2,000ms以下|2,001ms以上| 平均応答");

			int under500 = 0;
			int under2000 = 0;
			int over2001 = 0;
			int averageResponceTime = 0;
			int totalResponceTime = 0;
			int count = 0;
			int listCounter = 0;
			int endPoint = logList.size();

			LocalDateTime endTime = LocalDateTime.of(year, month, dayOfMonth + 1, 00, 05);

			for (AccessLogData accessLogData : logList) {
				listCounter++;

				LocalDateTime logTime = accessLogData.getDateTime();

				if (logTime.isBefore(limitTime)) {
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

					if (listCounter == endPoint) {
						// 最終行だった場合の処理
						averageResponceTime = calcAverageResponceTime(totalResponceTime, count);

						// 結果の1行分の出力
						DateTimeFormatter f = DateTimeFormatter.ofPattern("HH:mm");
						LocalDateTime shownTime = limitTime; // 表示用の時刻
						shownTime = shownTime.minusMinutes(5); // 表示用に5分戻す
						// 平均応答時間を4桁で半角スペース埋め
						String shownAverageResponceTime = String.valueOf(averageResponceTime);

						System.out.println(" " + shownTime.format(f) + " |    " + under500 + "    |    " + under2000
								+ "     |     " + over2001 + "    |   "
								+ String.format("%4s", shownAverageResponceTime));

						// それぞれの値をリセット
						under500 = 0;
						under2000 = 0;
						over2001 = 0;
						averageResponceTime = 0;
						totalResponceTime = 0;
						count = 0;
						
						// 5分進める
						limitTime = limitTime.plusMinutes(5);
					}

				} else {
					// timeより前の処理が終わったら合計を計算
					averageResponceTime = calcAverageResponceTime(totalResponceTime, count);

					// 結果の1行分の出力
					DateTimeFormatter f = DateTimeFormatter.ofPattern("HH:mm");
					LocalDateTime shownTime = limitTime; // 表示用の時刻
					shownTime = shownTime.minusMinutes(5); // 表示用に5分戻す
					// 平均応答時間を4桁で半角スペース埋め
					String shownAverageResponceTime = String.valueOf(averageResponceTime);

					System.out.println(" " + shownTime.format(f) + " |    " + under500 + "    |    " + under2000
							+ "     |     " + over2001 + "    |   " + String.format("%4s", shownAverageResponceTime));

					// それぞれの値をリセット
					under500 = 0;
					under2000 = 0;
					over2001 = 0;
					averageResponceTime = 0;
					totalResponceTime = 0;
					count = 0;

					// 5分進める
					limitTime = limitTime.plusMinutes(5);

					if (logTime.isBefore(limitTime)) {
						// 新しい時間帯の1行目の計算
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
					} else {
						// 結果の1行分の出力
						LocalDateTime shownTimeWhenNoData = limitTime; // 表示用の時刻
						shownTimeWhenNoData = shownTimeWhenNoData.minusMinutes(5); // 表示用に5分戻す
						// 平均応答時間を4桁で半角スペース埋め
						String shownAverageResponceTimeWhenNoData = "0";

						System.out.println(" " + shownTimeWhenNoData.format(f) + " |    " + under500 + "    |    "
								+ under2000 + "     |     " + over2001 + "    |   "
								+ String.format("%4s", shownAverageResponceTimeWhenNoData));

						// それぞれの値をリセット
						under500 = 0;
						under2000 = 0;
						over2001 = 0;
						averageResponceTime = 0;
						totalResponceTime = 0;
						count = 0;

						// 5分進める
						limitTime = limitTime.plusMinutes(5);

						// 新しい時間帯の1行目の計算
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
				}
			}
			
			// 最終行以降にまだ時間が残っている場合
			for(; endTime.isAfter(limitTime); limitTime = limitTime.plusMinutes(5)) {
				// 結果の1行分の出力
				DateTimeFormatter f = DateTimeFormatter.ofPattern("HH:mm");
				LocalDateTime shownTimeWhenNoData = limitTime; // 表示用の時刻
				shownTimeWhenNoData = shownTimeWhenNoData.minusMinutes(5); // 表示用に5分戻す
				// 平均応答時間を4桁で半角スペース埋め
				String shownAverageResponceTimeWhenNoData = "0";
				System.out.println(" " + shownTimeWhenNoData.format(f) + " |    " + under500 + "    |    "
						+ under2000 + "     |     " + over2001 + "    |   "
						+ String.format("%4s", shownAverageResponceTimeWhenNoData));
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
	
	/**
	 * 平均応答時間の計算用メソッド.
	 * 
	 * @return 平均応答時間
	 */
	public static int calcAverageResponceTime(int totalResponceTime, int count) {
		try {
			return totalResponceTime / count;
		} catch (Exception e) {
			// countかtotalResponceTimeが0だった場合を想定
			e.printStackTrace();
			return 0;
		}
	}

}
