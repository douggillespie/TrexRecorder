package trex;

import PamguardMVC.DataUnitBaseData;
import PamguardMVC.PamDataUnit;

public class TrexGpsDataUnit extends PamDataUnit {
	
	private double latitude, longitude;

	public TrexGpsDataUnit(long timeMilliseconds, double latitude, double longitude) {
		super(timeMilliseconds);
		this.latitude = latitude;
		this.longitude = longitude;
	}

	/**
	 * @return the latitude
	 */
	public double getLatitude() {
		return latitude;
	}

	/**
	 * @return the longitude
	 */
	public double getLongitude() {
		return longitude;
	}


}
