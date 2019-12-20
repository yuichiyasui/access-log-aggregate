package domain;

import java.time.LocalDateTime;

/**
 * アクセスログ情報のドメインクラス.
 * @author yuichi
 *
 */
public class AccessLogData {
	/**	日時 */
	private LocalDateTime dateTime;
	/**	ページ */
	private String page;
	/**	応答時間 */
	private int responceTime;
	
	public LocalDateTime getDateTime() {
		return dateTime;
	}
	public void setDateTime(LocalDateTime dateTime) {
		this.dateTime = dateTime;
	}
	public String getPage() {
		return page;
	}
	public void setPage(String page) {
		this.page = page;
	}
	public int getResponceTime() {
		return responceTime;
	}
	public void setResponceTime(int responceTime) {
		this.responceTime = responceTime;
	}
	@Override
	public String toString() {
		return "AccessLogData [dateTime=" + dateTime + ", page=" + page + ", responceTime=" + responceTime + "]";
	}
}
