import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import domain.AccessLogData;
import domain.ResultData;

public class ReAccessLogAggregate {

	public static void main(String[] args) {

		// 1行目の出力
		System.out.println("  時刻  |500ms以下|2,000ms以下|2,001ms以上| 平均応答");

		LocalDateTime limitTime = LocalDateTime.of(2012, 4, 7, 0, 5);
		LocalDateTime endTime = LocalDateTime.of(2012, 4, 8, 0, 0);
		
		Map<LocalDateTime, ResultData> map = new HashMap<>();
		try {
			// ファイルのパスを指定する
			File file = new File(
					"/Users/yuichi/Documents/workspace-spring/access-log-aggregate/documents/access_log.txt");
			// BufferedReaderクラスのreadLineメソッドを使って1行ずつ読み込む
			FileReader fileReader = new FileReader(file);
			BufferedReader bufferedReader = new BufferedReader(fileReader);
			String data;

			List<AccessLogData> inputDataList = new ArrayList<>();

			for (int i = 0; (data = bufferedReader.readLine()) != null; i++) {
				if (i == 0) {
					// 1行目はスキップ
					continue;
				}

				String[] oneLine = data.split("	");
				DateTimeFormatter f = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
				LocalDateTime inputDateTime = LocalDateTime.parse(oneLine[0], f);
				
				int under500 = 0;
				int under2000 = 0;
				int over2001 = 0;
				int totalResponceTime = 0;
				int count = 0;
				
				if(inputDateTime.isBefore(limitTime)) {
				AccessLogData accessLogData = new AccessLogData();
				accessLogData.setDateTime(inputDateTime);
				accessLogData.setPage(oneLine[1]);
				accessLogData.setResponceTime(Integer.parseInt(oneLine[2]));
				inputDataList.add(accessLogData);
				}else {
					for(AccessLogData accessLogData : inputDataList) {
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
					ResultData resultData = new ResultData();
					resultData.setOutputDateTime(limitTime);
					resultData.setUnder500(under500);
					resultData.setUnder2000(under2000);
					resultData.setOver2001(over2001);
					resultData.setTotalResponceTime(totalResponceTime);
					resultData.setCount(count);
					map.put(limitTime, resultData);
					
					// それぞれの値をリセット
					under500 = 0;
					under2000 = 0;
					over2001 = 0;
					totalResponceTime = 0;
					count = 0;
					
					// 5分進める
					limitTime = limitTime.plusMinutes(5);
					
					inputDataList = new ArrayList<>();
					if(inputDateTime.isBefore(limitTime)) {
						AccessLogData accessLogData = new AccessLogData();
						accessLogData.setDateTime(inputDateTime);
						accessLogData.setPage(oneLine[1]);
						accessLogData.setResponceTime(Integer.parseInt(oneLine[2]));
						inputDataList.add(accessLogData);
						}
					// elseだった場合スルーされてしまう(次が5分飛ぶ場合)
					
				}
			}
			
			// mapに入れたResultDataをlimitTimeをキーにして順に出力する
			for (limitTime = LocalDateTime.of(2012, 4, 7, 0, 5); limitTime.isBefore(endTime);
					limitTime = limitTime.plusMinutes(5)) {
				ResultData resultData;
					resultData = map.get(limitTime);
				DateTimeFormatter resultF = DateTimeFormatter.ofPattern("HH:mm");
				try {
					System.out.println(" " + limitTime.minusMinutes(5).format(resultF) + " |    " + resultData.getUnder500() + "    |    " + resultData.getUnder2000()
					+ "     |     " + resultData.getOver2001() + "    |   "
					+ String.format("%4s", resultData.getAverageResponceTime()));
				} catch (NullPointerException e) {
					System.out.println(" " + limitTime.minusMinutes(5).format(resultF) + " |    " + 0 + "    |    " + 0
					+ "     |     " + 0 + "    |   "
					+ String.format("%4s", "0"));
				}
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
