package trex;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import PamUtils.FileList;
import PamUtils.LatLong;
import PamUtils.PamCalendar;
import PamguardMVC.PamProcess;
import geoMag.MagneticVariation;
import geoMag.TSAGeoMag;
/**
 * 
 * From Mark J in email on Sat 14 February 2026
 * Ok, here's the steps for a single set of log data (ACC (1x3), ACCOFFS (1x3), MAG (1x3), MAGOFFS (1x3), LAT (decimal degrees), LON (decimal degrees), DATEVEC (1x6)):

A = ACC - ACCOFFS ;
M = MAG - MAGOFFS ;
mA = sqrt(sum(A.^2)) ;

pitch = asin(A(1)/mA) ;    % pitch angle in radians
roll = atan2(A(2),A(3)) ;  % roll angle in radians

Tx = [cos(pitch) -sin(roll)*sin(pitch) -cos(roll)*sin(pitch)] ;
Ty = [0 cos(roll) -sin(roll)] ;

hdg = atan2(-sum(M.*Ty),sum(M.*Tx))*180/pi ; % magnetic heading in degrees

ds = datestr(datenum(DATEVEC),29) ;
url='http://geomag.bgs.ac.uk/web_service/GMModels/igrf/14?' ;
params = sprintf('latitude=%d&longitude=%d&altitude=0&date=%s',round(LAT),round(LON),ds) ;
s=webread([url params],'get',{}) ;
decl = s.geomagnetic_field_model_result.field_value.declination.value ;    % declination angle in degrees

thdg = hdg+decl ;    % true heading in degrees

Good luck!

 */

public class TrexLogProcess extends PamProcess {

	private TrexControl trexControl;

	private boolean allExtracted = false;

	private Thread extractThread;

	private ArrayList<File> logFiles;

	private LogLineData magData;
	
	private MagneticVariation magVar;

	private LatLong latLong;

	public TrexLogProcess(TrexControl trexControl) {
		super(trexControl, null);
		this.trexControl = trexControl;
		magVar = MagneticVariation.getInstance();
	}

	@Override
	public void pamStart() {
		if (allExtracted == false) {
			allExtracted = true;
			extractThread = new Thread(new Runnable() {
				@Override
				public void run() {
					if (extractLogData()) {
						allExtracted = true;
					}
				}
			});
			extractThread.start();
		}

	}

	private boolean extractLogData() {
		String folder = trexControl.findAcquisitionFolder();
		if (folder == null) {
			System.out.println("Trex metadata system unable to find the sound acquisition folder");
			return false;
		}

		// list all .gps files in the folder and sub folders (generally no sub folders for Trex)
		FileList fileList = new FileList();
		logFiles = fileList.getFileList(folder, ".log", true);
		if (logFiles == null) {
			return false;
		}
		for (int i = 0; i < logFiles.size(); i++) {
			extractLogFile(logFiles.get(i));
		}
		return true;
	}

	private boolean extractLogFile(File file) {
		BufferedReader dis = null;
		try {
			dis = new BufferedReader(new FileReader(file));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return false;
		}
		String line;
		try {
			while ((line = dis.readLine()) != null) {
				processLine(file, dis, line);
			}
		}
		catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;

	}

	private void processLine(File file, BufferedReader dis, String line) {
		LogLineData data = breakLine(line);
		if (data == null) {
			return;
		}
		switch (data.getType()) {
		case "ACC":
			processACC(file, data, dis);
			break;
		case "LAT":
			processLAT(file, data, dis);
			break;
		}

	}

	private boolean processACC(File file, LogLineData accData, BufferedReader dis) {
		String magLine = findLine(dis, "MAG", 1);
		if (magLine == null) {
			System.out.println("Unable to find MAG line following " + accData);
			return false;
		}
		magData = breakLine(magLine);
		double[] acc = breakAccMag(accData.getData()[0]);
		double[] mag = breakAccMag(magData.getData()[0]);
		if (mag.length != 3 || acc.length != 3) {
			return false;
		}
		double mA = 0;
		for (int i = 0; i < 3; i++) {
			mA += Math.pow(acc[i], 2);
		}
		mA = Math.sqrt(mA);
		double pitch = Math.asin(acc[0]/mA);
		double roll = Math.atan2(acc[1], acc[2]);
		
		double[] tx = new double[3];
		double[] ty = new double[3];

		tx[0] = Math.cos(pitch);
		tx[1] = -Math.sin(roll)*Math.sin(pitch);
		tx[2] = -Math.cos(roll)*Math.sin(pitch);
		ty[1] = Math.cos(roll);
		ty[2] = Math.sin(roll);
		
		double sx = 0, sy = 0;
		for (int i = 0; i < 3; i++) {
			sy += mag[i]*ty[i];
			sx += mag[i]*tx[i];
		}
		double head = Math.atan2(-sy, sx)*180/Math.PI;
		double trueHead = Double.NaN;
		double decl = Double.NaN;
		if (latLong != null) {
			decl = magVar.getVariation(magData.getUtc(), latLong.getLatitude(), latLong.getLongitude());
			trueHead = head + decl;
		}
				
		System.out.printf("HPR data at %s is Head=%3.1f degM (%3.1fT), pitch = %3.1f, roll = %3.1f\n",
				PamCalendar.formatDBDateTime(magData.getUtc(), true),
				head, trueHead, pitch*180/Math.PI, roll*180/Math.PI);

		

		return true;
	}

	/**
	 * 
	 * @param string
	 * @return
	 */
	private double[] breakAccMag(String string) {
		String[] parts = string.split(" ");
		double[] vals = new double[parts.length];
		for (int i = 0; i < parts.length; i++) {
			try {
				vals[i] = Double.valueOf(parts[i]);
			}
			catch (NumberFormatException e) {
				vals[i] = Double.NaN;
			}
		}
		return vals;
	}

	private boolean processLAT(File file, LogLineData latData, BufferedReader dis) {
		String lonLine = findLine(dis, "LONG", 1);
		if (lonLine == null) {
			return false;
		}
		LogLineData lonData = breakLine(lonLine);
		Double lat = breakLLLine(latData.getData()[0]);
		Double lon = breakLLLine(lonData.getData()[0]);
		if (lat == null || lon == null) {
			System.out.println("Unable to decode latlong at " + latData);
		}
		
		latLong = new LatLong(lat,  lon);
		System.out.printf("Latlong at  %s is %s\n", PamCalendar.formatDBDateTime(latData.getUtc(), true), latLong);
		
		return true;
	}

	/**
	 * Break up a latlong line which will be two doubles and a String
	 * @param data
	 * @return
	 */
	private Double breakLLLine(String data) {
		String[] parts = data.split(" ");
		if (parts.length != 3) {
			return null;
		}
		double d = 0, m = 0;
		try {
			d = Double.valueOf(parts[0]);
			m = Double.valueOf(parts[1]);
		}
		catch (NumberFormatException e) {
			System.out.println("Error unpacking lat or long data " + data);
		}
		d += m/60;
		if (parts[2].equalsIgnoreCase("S") || parts[2].equalsIgnoreCase("W")) {
			d = -d;
		}
		
		return d;
	}

	/**
	 * Find a subsequent line, searching forwards a max of maxLines
	 * @param dis
	 * @param type
	 * @param maxLines
	 * @return
	 */
	private String findLine(BufferedReader dis, String type, int maxLines) {
		try {
			for (int i = 0; i < maxLines; i++) {
				String line = dis.readLine();
				LogLineData llData = breakLine(line);
				if (llData != null && llData.getType().equals(type)) {
					return line;
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	private LogLineData breakLine(String line) {
		String[] parts = line.split(",");
		long utc = 0;
		try {
			long s = Long.valueOf(parts[0]);
			long m = Long.valueOf(parts[1]);
			utc = s*1000 + m/1000;
		}
		catch (Exception e) {
			System.out.println("Error breaking log file line: " + line);
			return null;
		}
		String type = parts[2];
		String[] data = Arrays.copyOfRange(parts, 3, parts.length);
		return new LogLineData(utc, type, data);
	}


	@Override
	public void pamStop() {
		// TODO Auto-generated method stub

	}

}
