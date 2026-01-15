package trex;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import GPS.GPSDataBlock;
import GPS.GPSParameters;
import GPS.GpsData;
import GPS.GpsDataUnit;
import GPS.GpsLogger;
import PamUtils.FileList;
import PamguardMVC.PamProcess;

public class TrexGPSProcess extends PamProcess {

	private TrexControl trexControl;
	private ArrayList<File> gpsFiles;

	private boolean allExtracted;
	
	private GPSDataBlock gpsDataBlock;
	private volatile Thread extractThread;

	public TrexGPSProcess(TrexControl trexControl) {
		super(trexControl, null);
		this.trexControl = trexControl;
		gpsDataBlock = new GPSDataBlock(this);
		gpsDataBlock.SetLogging(new GpsLogger(gpsDataBlock));
		addOutputDataBlock(gpsDataBlock);
	}

	@Override
	public void pamStart() {
		if (allExtracted == false) {
			allExtracted = true;
			extractThread = new Thread(new Runnable() {
				
				@Override
				public void run() {
					extractAllGPS();
				}
			});
		}
	}

	@Override
	public void pamStop() {
		if (extractThread != null) {
			try {
				extractThread.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

	}

	@Override
	public boolean prepareProcessOK() {
		String folder = trexControl.findAcquisitionFolder();
		gpsFiles = null;
		if (folder == null) {
			System.out.println("Trex metadata system unable to find the sound acquisition folder");
			return false;
		}


		return (folder != null);
	}

	public boolean extractAllGPS() {
		String folder = trexControl.findAcquisitionFolder();
		if (folder == null) {
			System.out.println("Trex metadata system unable to find the sound acquisition folder");
			return false;
		}

		// list all .gps files in the folder and sub folders (generally no sub folders for Trex)
		FileList fileList = new FileList();
		gpsFiles = fileList.getFileList(folder, ".gps", true);
		if (gpsFiles == null) {
			return false;
		}
		for (int i = 0; i < gpsFiles.size(); i++) {
			extractGpsData(gpsFiles.get(i));
		}


		return true;
	}

	/**
	 * Unpack a single GPS data file and send to database. 
	 * @param file
	 * @return
	 */
	private boolean extractGpsData(File file) {

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
				processLine(file, line);				
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


		return true;
	}

	private void processLine(File file, String line) {		
		/*
		 * Data look a bit like this ... some early records may not be complete:
		 * 1706907426,630049,"GPS ON"
		 * 1706907446,531415,"$GPRMC,205731.307,V,,,,,0.14,167.82,010224,,,N*41"
		 * 1706907448,531436,"$GPRMC,205733.000,V,,,,,0.08,148.03,010224,,,N*4E"
		 * 1706907449,531446,"$GPRMC,205734.000,A,1559.4184,N,10651.1251,E,0.32,263.14,010224,,,A*6A"
		 */
		// strip out the first two numbers then assume the rest a standard RMC String
		if (line == null || line.contains("$GPRMC") == false) {
			return;
		}
		int comPos = 0;
		String comma = ",";
		for (int i = 0; i < 2; i++) {
			comPos = line.indexOf(comma, comPos+1);
		}
		String[] parts = line.split(comma);
		if (parts.length < 5) {
			return;
		}
		long timeStamp = 0;
		try {
			timeStamp = Long.valueOf(parts[0]) * 1000L + Long.valueOf(parts[1]) / 1000 ;
		}
		catch (NumberFormatException e) {
			System.out.println("Number format exception in " + line);
			return;
		}
		String rmc = line.substring(comPos+1);
		// strip off quotes that may or may not be there. 
		rmc = rmc.replace("\"", "");
		rmc = rmc.trim();
		GpsData gpsData = new GpsData(new StringBuffer(rmc), GPSParameters.READ_RMC);
		if (gpsData != null && gpsData.isDataOk()) {
			GpsDataUnit gpsDataUnit = new GpsDataUnit(timeStamp, gpsData);
			gpsDataBlock.addPamData(gpsDataUnit);
		}
	}


}
