package trex;

import PamModel.PamDependency;
import PamModel.PamPluginInterface;

public class TrexPlugin implements PamPluginInterface {

	private String jarFile;
	
	@Override
	public String getDefaultName() {
		return "Trex Recorder";
	}

	@Override
	public String getHelpSetName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setJarFile(String jarFile) {
		this.jarFile = jarFile;
	}

	@Override
	public String getJarFile() {
		return jarFile;
	}

	@Override
	public String getDeveloperName() {
		return "Doug Gillespie";
	}

	@Override
	public String getContactEmail() {
		return null;
	}

	@Override
	public String getVersion() {
		return "0.0";
	}

	@Override
	public String getPamVerDevelopedOn() {
		return "2.2.18";
	}

	@Override
	public String getPamVerTestedOn() {
		return "2.2.18";
	}

	@Override
	public String getAboutText() {
		// TODO Auto-generated method stub
		return "Extract Trex recorder metadata";
	}

	@Override
	public String getClassName() {
		return TrexControl.class.getName();
	}

	@Override
	public String getDescription() {
		return getAboutText();
	}

	@Override
	public String getMenuGroup() {
		return "Sensors";
	}

	@Override
	public String getToolTip() {
		return getAboutText();
	}

	@Override
	public PamDependency getDependency() {
		return null;
	}

	@Override
	public int getMinNumber() {
		return 0;
	}

	@Override
	public int getMaxNumber() {
		return 1;
	}

	@Override
	public int getNInstances() {
		return 0;
	}

	@Override
	public boolean isItHidden() {
		return false;
	}

	@Override
	public int allowedModes() {
		return 0;
	}



}
