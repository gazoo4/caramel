package sk.berops.android.vehiculum.engine.calculation;

import android.util.Log;

import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;

import sk.berops.android.vehiculum.dataModel.UnitConstants;
import sk.berops.android.vehiculum.dataModel.calculation.FuelConsumption;
import sk.berops.android.vehiculum.dataModel.expense.Cost;
import sk.berops.android.vehiculum.dataModel.expense.Entry;
import sk.berops.android.vehiculum.dataModel.expense.FuellingEntry;

import static sk.berops.android.vehiculum.engine.calculation.NewGenFuelConsumption.FLOATING_AVG_COUNT;
import static sk.berops.android.vehiculum.engine.calculation.NewGenFuelConsumption.FLOATING_AVG_CUT;
import static sk.berops.android.vehiculum.engine.calculation.NewGenFuelConsumption.MOVING_AVG_COUNT;

/**
 * @author Bernard Halas
 * @date 7/25/17
 */

public class NewGenFuelCalculator extends NewGenTypeCalculator {
	private HashMap<UnitConstants.Substance, FuellingEntry> initialBySubstance = new HashMap<>();
	private HashMap<FuellingEntry.FuelType, FuellingEntry> initialByFuelType = new HashMap<>();
	private HashMap<UnitConstants.Substance, FuellingEntry> previousBySubstance = new HashMap<>();
	private HashMap<FuellingEntry.FuelType, FuellingEntry> previousByFuelType = new HashMap<>();

	private HashMap<UnitConstants.Substance, LinkedList<FuellingEntry>> movingEntries = new HashMap<>();
	private HashMap<UnitConstants.Substance, LinkedList<FuellingEntry>> floatingEntries = new HashMap<>();

	@Override
	public void calculateNext(Entry entry) {
		super.calculateNext(entry);

		if (entry == null) {
			return;
		}

		if (! (entry instanceof FuellingEntry)) {
			Log.w(this.getClass().toString(), "Asked to calculate fuelling consumption on non-fuelling entry");
			return;
		}

		FuellingEntry fEntry = (FuellingEntry) entry;

		NewGenFuelConsumption prevC = (previous == null) ? null : (NewGenFuelConsumption) previous.getConsumption();
		NewGenFuelConsumption nextC = fEntry.getFuelConsumption();

		nextC.setCostLastRefuel(calculateCostLastRefuel(fEntry));

		nextC.setTotalVolume(calculateTotalVolume(prevC, fEntry));
		nextC.setTotalTypeVolume(calculateTotalTypeVolume(prevC, fEntry));

		nextC.setAverageConsumption(calculateAverageConsumption(prevC, fEntry));
		nextC.setLastConsumption(calculateLastConsumption(fEntry));

		nextC.setAverageTypeConsumption(calculateAverageTypeConsumption(prevC, fEntry));

		slide(movingEntries, fEntry, MOVING_AVG_COUNT);
		slide(floatingEntries, fEntry, FLOATING_AVG_COUNT);

		nextC.setMovingConsumption(calculateMovingConsumption(prevC, fEntry));
		nextC.setFloatingConsumption(calculateFloatingConsumption(prevC, fEntry));
	}

	@Override
	public void shiftPointers(Entry e) {
		super.shiftPointers(e);

		FuellingEntry f = (FuellingEntry) e;
		FuellingEntry.FuelType type = f.getFuelType();
		UnitConstants.Substance substance = f.getFuelType().getSubstance();

		if (initialByFuelType.get(type) == null) initialByFuelType.put(type, f);
		if (initialBySubstance.get(substance) == null) initialBySubstance.put(substance, f);

		previousByFuelType.put(type, f);
		previousBySubstance.put(substance, f);
	}

	private Cost calculateCostLastRefuel(FuellingEntry entry) {
		Cost result = new Cost();
		FuellingEntry.FuelType type = entry.getFuelType();
		if (previousByFuelType.get(type) != null) {
			double mileage = entry.getMileageSI() - previousByFuelType.get(entry).getMileageSI();
			result = Cost.divide(entry.getCost(), (mileage * 100));
		}

		return result;
	}

	private HashMap<UnitConstants.Substance, Double> calculateTotalVolume(NewGenFuelConsumption prevC, FuellingEntry entry) {
		// Total volume
		HashMap<UnitConstants.Substance, Double> result;
		result = (prevC == null) ? new HashMap<>() : new HashMap<>(prevC.getTotalVolume());

		// Fuel substance needs to be taken into the account (as we can't combine liquids, solid, electricity,...)
		UnitConstants.Substance substance = entry.getFuelType().getSubstance();
		double volume = (result.get(substance) == null) ? 0 : result.get(substance);
		result.put(substance, volume + entry.getFuelQuantitySI());

		return result;
	}

	private HashMap<FuellingEntry.FuelType, Double> calculateTotalTypeVolume(NewGenFuelConsumption prevC, FuellingEntry entry) {
		HashMap<FuellingEntry.FuelType, Double> result;
		result = (prevC == null) ? new HashMap<>() : new HashMap<>(prevC.getTotalTypeVolume());

		FuellingEntry.FuelType type = entry.getFuelType();
		double volume = (result.get(type) == null) ? 0 : result.get(type);
		result.put(type, volume + entry.getFuelQuantitySI());

		return result;
	}

	private HashMap<UnitConstants.Substance, Double> calculateAverageConsumption(NewGenFuelConsumption prevC, FuellingEntry entry) {
		HashMap<UnitConstants.Substance, Double> result;
		result = (prevC == null) ? new HashMap<>() : new HashMap<>(prevC.getAverageConsumption());

		UnitConstants.Substance substance = entry.getFuelType().getSubstance();
		FuellingEntry initial = initialBySubstance.get(substance);
		if (initial == null) {
			result.put(substance, 0.0);
		} else {
			// At this moment the totalVolume needs to be calculated
			double mileage = entry.getMileageSI() - initial.getMileageSI();
			double volume = ((NewGenFuelConsumption) entry.getConsumption()).getTotalVolume().get(substance);
			result.put(substance, volume / (mileage * 100));
		}
		return result;
	}

	private double calculateLastConsumption(FuellingEntry entry) {
		UnitConstants.Substance substance = entry.getFuelType().getSubstance();
		FuellingEntry previous = previousBySubstance.get(substance);
		if (previous == null) {
			return 0.0;
		} else {
			double mileage = entry.getMileageSI() - previous.getMileageSI();
			double volume = entry.getFuelQuantitySI();
			return volume / (mileage * 100);
		}
	}

	private HashMap<FuellingEntry.FuelType, Double> calculateAverageTypeConsumption(NewGenFuelConsumption prevC, FuellingEntry entry) {
		HashMap<FuellingEntry.FuelType, Double> result;
		result = (prevC == null) ? new HashMap<>() : new HashMap<>(prevC.getAverageTypeConsumption());

		FuellingEntry.FuelType type = entry.getFuelType();
		FuellingEntry initial = initialByFuelType.get(type);
		if (initial == null) {
			result.put(type, 0.0);
		} else {
			// At this moment the totalVolume needs to be calculated
			double mileage = entry.getMileageSI() - initial.getMileageSI();
			double volume = ((NewGenFuelConsumption) entry.getConsumption()).getTotalTypeVolume().get(type);
			result.put(type, volume / (mileage * 100));
		}
		return result;
	}

	private HashMap<UnitConstants.Substance, Double> calculateMovingConsumption(NewGenFuelConsumption prevC, FuellingEntry entry) {
		HashMap<UnitConstants.Substance, Double> result;
		result = (prevC == null) ? new HashMap<>() : new HashMap<>(prevC.getMovingConsumption());

		UnitConstants.Substance substance = entry.getFuelType().getSubstance();
		LinkedList<FuellingEntry> queue = movingEntries.get(substance);
		if (queue == null || queue.size() <= 1) {
			result.put(substance, 0.0);
		} else {
			double mileage = queue.getLast().getMileageSI() - queue.getFirst().getMileageSI();
			double volume = 0.0;
			for (FuellingEntry e : queue) {
				volume += e.getFuelQuantitySI();
			}
			volume -= queue.getFirst().getFuelQuantitySI();

			double consumption = volume / (mileage * 100);
			result.put(substance, consumption);
		}

		return result;

	}

	private HashMap<UnitConstants.Substance, Double> calculateFloatingConsumption(NewGenFuelConsumption prevC, FuellingEntry entry) {
		HashMap<UnitConstants.Substance, Double> result;
		result = (prevC == null) ? new HashMap<>() : new HashMap<>(prevC.getFloatingConsumption());

		UnitConstants.Substance substance = entry.getFuelType().getSubstance();
		LinkedList<FuellingEntry> queue = floatingEntries.get(substance);
		int minLength = (FLOATING_AVG_CUT * 2) + 1;
		if (queue == null || queue.size() <= minLength) {
			result.put(substance, 0.0);
		} else {
			LinkedList<Double> values = new LinkedList<>();
			// Load all the values
			for (FuellingEntry e: queue) {
				values.add(e.getFuelConsumption().getLastConsumption());
			}
			// Sort all the values
			values.sort((Double d1, Double d2) -> Double.compare(d1, d2));

			// Cut min & max values
			for (int i = 0; i < FLOATING_AVG_CUT; i++) {
				values.removeFirst();
				values.removeLast();
			}

			double consumption = 0.0;
			// Calculate the average consumption
			for (Double d: values) {
				consumption += d;
			}
			consumption /= values.size();

			result.put(substance, consumption);
		}

		return result;
	}

	private void slide(HashMap<UnitConstants.Substance, LinkedList<FuellingEntry>> entries, FuellingEntry entry, int length) {
		UnitConstants.Substance substance = entry.getFuelType().getSubstance();
		LinkedList<FuellingEntry> queue = entries.get(substance);

		if (queue == null) {
			queue = new LinkedList<>();
			entries.put(substance, queue);
		}

		queue.add(entry);
		if (queue.size() > length) {
			queue.removeFirst();
		}
	}
}