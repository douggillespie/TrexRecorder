package trex.logging;

import java.sql.Types;

import PamguardMVC.PamDataUnit;
import generalDatabase.PamTableDefinition;
import generalDatabase.PamTableItem;
import generalDatabase.SQLLogging;
import generalDatabase.SQLTypes;
import trex.TrexGpsDataBlock;
import trex.TrexGpsDataUnit;

public class TrexGPSLogging extends SQLLogging {
	
	private PamTableDefinition tableDef;
	
	private PamTableItem latitude, longitude;

	public TrexGPSLogging(TrexGpsDataBlock pamDataBlock) {
		super(pamDataBlock);
		tableDef = new PamTableDefinition("Trex Location");
		tableDef.addTableItem(latitude = new PamTableItem("Latitude", Types.DOUBLE));
		tableDef.addTableItem(longitude = new PamTableItem("Longitude", Types.DOUBLE));
		
		
		setTableDefinition(tableDef);
	}

	@Override
	public void setTableData(SQLTypes sqlTypes, PamDataUnit pamDataUnit) {
		TrexGpsDataUnit gps = (TrexGpsDataUnit) pamDataUnit;
		latitude.setValue(gps.getLatitude());
		longitude.setValue(gps.getLongitude());		
	}

}
