package trex;

import PamUtils.PamCalendar;

public class LogLineData {

	private long utc;
	private String type;
	private String[] data;

	public LogLineData(long datetime, String type, String[] data) {
		this.utc = datetime;
		this.type = type;
		this.data = data;
	}

	/**
	 * @return the utc
	 */
	public long getUtc() {
		return utc;
	}

	/**
	 * @return the type
	 */
	public String getType() {
		return type;
	}

	/**
	 * @return the data
	 */
	public String[] getData() {
		return data;
	}

	@Override
	public String toString() {
		String str = String.format("%s, Type %d, data: ", PamCalendar.formatDBDateTime(utc, true), type);
		for (int i = 0; i < data.length; i++) {
			str += data[i] + " ";
		}
		return str;
		
	}

}
