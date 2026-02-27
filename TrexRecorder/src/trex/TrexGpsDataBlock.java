package trex;

import PamguardMVC.PamDataBlock;
import PamguardMVC.PamProcess;

public class TrexGpsDataBlock extends PamDataBlock<TrexGpsDataUnit> {

	private TrexLogProcess trexLogProcess;

	public TrexGpsDataBlock(TrexLogProcess parentProcess) {
		super(TrexGpsDataUnit.class, "Trex GPS", parentProcess, 0);
		this.trexLogProcess = parentProcess;
	}


}
