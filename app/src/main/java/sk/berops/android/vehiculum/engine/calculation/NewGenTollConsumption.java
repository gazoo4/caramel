package sk.berops.android.vehiculum.engine.calculation;

import java.util.HashMap;

import sk.berops.android.vehiculum.dataModel.expense.Cost;
import sk.berops.android.vehiculum.dataModel.expense.TollEntry;

/**
 * @author Bernard Halas
 * @date 8/16/17
 */

public class NewGenTollConsumption extends NewGenConsumption {
	private HashMap<TollEntry.Type, Cost> totalTTypeCost = new HashMap<>();

	public HashMap<TollEntry.Type, Cost> getTotalTTypeCost() {
		return totalTTypeCost;
	}

	public void setTotalTTypeCost(HashMap<TollEntry.Type, Cost> totalTTypeCost) {
		this.totalTTypeCost = totalTTypeCost;
	}
}