package trex.logging;

import java.sql.Types;

import PamguardMVC.PamDataBlock;
import PamguardMVC.PamDataUnit;
import generalDatabase.PamTableDefinition;
import generalDatabase.PamTableItem;
import generalDatabase.SQLLogging;
import generalDatabase.SQLTypes;
import trex.TrexHPRDataBlock;
import trex.TrexHPRDataUnit;

public class TrexHPRLogging extends SQLLogging {

	private PamTableDefinition tableDef;
	
	private PamTableItem magHead, trueHead, pitch, roll;
	
	public TrexHPRLogging(TrexHPRDataBlock pamDataBlock) {
		super(pamDataBlock);
		tableDef = new PamTableDefinition("Trex Orientation");
		tableDef.addTableItem(magHead = new PamTableItem("Mag Head", Types.DOUBLE));
		tableDef.addTableItem(trueHead = new PamTableItem("True Head", Types.DOUBLE));
		tableDef.addTableItem(pitch = new PamTableItem("Pitch", Types.DOUBLE));
		tableDef.addTableItem(roll = new PamTableItem("Roll", Types.DOUBLE));
		
		setTableDefinition(tableDef);
	}

	@Override
	public void setTableData(SQLTypes sqlTypes, PamDataUnit pamDataUnit) {
		TrexHPRDataUnit hpr = (TrexHPRDataUnit) pamDataUnit;
		magHead.setValue(hpr.getMagHead());
		trueHead.setValue(hpr.getTrueHead());
		pitch.setValue(hpr.getPitch());
		roll.setValue(hpr.getRoll());
	}

}
