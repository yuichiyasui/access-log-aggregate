package domain;

import java.time.LocalDateTime;

/**
 * コンソールに表示する分析結果の1行分のデータ.
 * @author yuichi
 *
 */
public class ResultData {

	/**	出力する日時 */
	private LocalDateTime outputDateTime;
	/**	応答時間:500以下のデータ数 */
	private int under500;
	/**	応答時間:2000以下のデータ数 */
	private int under2000;
	/**	応答時間:2001以上のデータ数 */
	private int over2001;
	/**	平均応答時間 */
	private int averageResponceTime;
	/**	合計応答時間 */
	private int totalResponceTime;
	/**	要素数 */
	private int count;
	public LocalDateTime getOutputDateTime() {
		return outputDateTime;
	}
	public void setOutputDateTime(LocalDateTime outputDateTime) {
		this.outputDateTime = outputDateTime;
	}
	public int getUnder500() {
		return under500;
	}
	public void setUnder500(int under500) {
		this.under500 = under500;
	}
	public int getUnder2000() {
		return under2000;
	}
	public void setUnder2000(int under2000) {
		this.under2000 = under2000;
	}
	public int getOver2001() {
		return over2001;
	}
	public void setOver2001(int over2001) {
		this.over2001 = over2001;
	}
	public int getAverageResponceTime() {
		try {
			return getTotalResponceTime() / getCount() ;
		} catch (ArithmeticException e) {
			return 0;
		}
	}
	public void setAverageResponceTime(int averageResponceTime) {
		this.averageResponceTime = averageResponceTime;
	}
	public int getTotalResponceTime() {
		return totalResponceTime;
	}
	public void setTotalResponceTime(int totalResponceTime) {
		this.totalResponceTime = totalResponceTime;
	}
	public int getCount() {
		return count;
	}
	public void setCount(int count) {
		this.count = count;
	}
	@Override
	public String toString() {
		return "ResultData [outputDateTime=" + outputDateTime + ", under500=" + under500 + ", under2000=" + under2000
				+ ", over2001=" + over2001 + ", averageResponceTime=" + averageResponceTime + ", totalResponceTime="
				+ totalResponceTime + ", count=" + count + "]";
	}
	
}
