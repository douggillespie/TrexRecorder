package trex;

import java.io.File;

import Acquisition.AcquisitionControl;
import Acquisition.AcquisitionParameters;
import Acquisition.DaqSystem;
import Acquisition.FolderInputSystem;
import PamController.PamConfiguration;
import PamController.PamControlledUnit;
import PamController.PamController;
import PamguardMVC.debug.Debug;
import pamguard.GlobalArguments;

/**
 * Set of tools to automatically extract meta data from dtg and other Trex files. 
 * @author dg50
 *
 */
public class TrexControl extends PamControlledUnit {
	
	public static final String unitType = "Trex Recorder";
	
	private TrexGPSProcess trexGPSProcess;
	
	public TrexControl(String unitName) {
		super(unitType, unitName);
		this.trexGPSProcess = new TrexGPSProcess(this);
		addPamProcess(trexGPSProcess);
	}

	public String findAcquisitionFolder() {
		// first check to see if it's been set from the batck processor
		String globalFolder = GlobalArguments.getParam(FolderInputSystem.GlobalWavFolderArg);
		Debug.out.println("Checking -wavfilefolder option: is " + globalFolder);
		if (globalFolder != null) {
			File globalFile = new File(globalFolder);
			if (globalFile.exists() && globalFile.isDirectory()) {
				return globalFolder;
			}
		}
		
		// otherwise find the acquisition and get the folder name from there. 
		AcquisitionControl daq = findAcquisition();
		if (daq == null) {
			return null;
		}
		/**
		 * This just gets the basic params. Harder to find the folder for
		 * the acquisition
		 */
		AcquisitionParameters params = daq.getAcquisitionParameters();
//		System.out.println(params);
		DaqSystem daqSystem = daq.findDaqSystem(null);
		if (daqSystem == null) {
			return null;
		}
		String name = daqSystem.getDeviceName(); // this will be the folder for a folder input system. Check!
		if (name == null) {
			return null;
		}
		File fold = new File(name);
		if (fold.exists() && fold.isDirectory()) {
			return name;
		}
		else {
			return null;
		}
	}

	public AcquisitionControl findAcquisition() {
		PamControlledUnit daq = PamController.getInstance().findControlledUnit(AcquisitionControl.unitType);
		return (AcquisitionControl) daq;
	}

}
