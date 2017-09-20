package sk.berops.android.vehiculum.dataModel.charting;

import com.github.mikephil.charting.data.PieEntry;

import java.util.ArrayList;
import java.util.HashMap;

import sk.berops.android.vehiculum.R;
import sk.berops.android.vehiculum.Vehiculum;
import sk.berops.android.vehiculum.dataModel.Currency;
import sk.berops.android.vehiculum.dataModel.expense.Entry;
import sk.berops.android.vehiculum.dataModel.expense.History;
import sk.berops.android.vehiculum.engine.calculation.NewGenConsumption;
import sk.berops.android.vehiculum.gui.common.TextFormatter;

/**
 * @author Bernard Halas
 * @date 8/31/17
 */

public class HistoryCharter extends Charter {
	private History history;
	/**
	 * Map holding the latest entry object for each entry type
	 */
	private HashMap<Entry.ExpenseType, Entry> lastEntries;

	public HistoryCharter(History history) {
		this.history = history;
		refreshData();
	}

	/**
	 * Method responsible for populating {@link HistoryCharter#lastEntries}. The reason we are interested in
	 * the last entries is that for each entry-type this variable holds accumulated consumption data
	 * which is then used for charting.
	 */
	private void refreshData() {
		lastEntries = new HashMap<>();
		for (Entry e: history.getEntries()) {
			lastEntries.put(e.getExpenseType(), e);
		}

		vals = new ArrayList<>();
		colors = new ArrayList<>();
		for (Entry.ExpenseType t: lastEntries.keySet()) {
			NewGenConsumption c = lastEntries.get(t).getConsumption();
			vals.add(new PieEntry(c.getTotalTypeCost().getPreferredValue().floatValue()));
			colors.add(t.getColor());
		}
	}

	@Override
	public ArrayList<PieEntry> extractPieChartVals() {
		if (vals == null) {
			refreshData();
		}

		return vals;
	}

	@Override
	public ArrayList<Integer> extractPieChartColors() {
		if (colors == null) {
			refreshData();
		}

		return colors;
	}

	@Override
	public String extractPieChartLabel() {
		String total = Vehiculum.context.getString(R.string.generic_charts_total);
		double value = history.getEntries().getLast().getConsumption().getTotalCost().getPreferredValue();
		Currency.Unit unit = history.getEntries().getLast().getConsumption().getTotalCost().getPreferredUnit();

		return total + ": " + TextFormatter.format(value, "######.##") + " " + unit.getSymbol();
	}
}