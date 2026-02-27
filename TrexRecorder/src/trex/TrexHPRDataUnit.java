package trex;

import PamguardMVC.PamDataUnit;

public class TrexHPRDataUnit extends PamDataUnit {

	private double magHead;
	private Double trueHead;
	private double pitch;
	private double roll;

	public TrexHPRDataUnit(long timeMilliseconds, double magHead, Double trueHead, double pitch, double roll) {
		super(timeMilliseconds);
		this.magHead = magHead;
		this.trueHead = trueHead;
		this.pitch = pitch;
		this.roll = roll;
	}

	/**
	 * @return the magHead
	 */
	public double getMagHead() {
		return magHead;
	}

	/**
	 * @return the trueHead
	 */
	public Double getTrueHead() {
		return trueHead;
	}

	/**
	 * @return the pitch
	 */
	public double getPitch() {
		return pitch;
	}

	/**
	 * @return the roll
	 */
	public double getRoll() {
		return roll;
	}


}
