package trex;

import PamguardMVC.PamDataBlock;
import PamguardMVC.PamProcess;

public class TrexHPRDataBlock extends PamDataBlock<TrexHPRDataUnit> {
	
	private TrexLogProcess trexLogProcess;

	public TrexHPRDataBlock(TrexLogProcess parentProcess) {
		super(TrexHPRDataUnit.class, "Trex Orientation", parentProcess, 0);
		this.trexLogProcess = parentProcess;
	}


}
