package sk.berops.android.vehiculum.dataModel.expense;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.UUID;

import sk.berops.android.vehiculum.dataModel.Currency;
import sk.berops.android.vehiculum.dataModel.Currency.Unit;
import sk.berops.android.vehiculum.dataModel.Record;
import sk.berops.android.vehiculum.dataModel.maintenance.ReplacementPart;
import sk.berops.android.vehiculum.engine.synchronization.controllers.MaintenanceEntryController;

public class MaintenanceEntry extends Entry {
	@Element(name="type")
	private Type type;
	@Element(name="laborCost", required=false)
	private double laborCost;
	@Element(name="laborCostSI", required=false)
	private double laborCostSI;
	@Element(name="laborCostCurrency", required=false)
	private Currency.Unit laborCostCurrency;
	@ElementList(inline=true, required=false)
	private LinkedList<ReplacementPart> parts;
	
	public MaintenanceEntry() {
		super();
		this.setExpenseType(Entry.ExpenseType.MAINTENANCE);
		this.setParts(new LinkedList<ReplacementPart>());
	}
	
	public enum Type{
		PLANNED(0, "planned", 0xFFABD14C),
		UNPLANNED(1, "unplanned", 0xFF4CB0D1),
		ACCIDENT_REPAIR(2, "accident repair", 0xFFD14CB2);
		private int id;
		private String type;
		private int color;

		Type(int id, String type) {
			this(id, type, (int) (Math.random() * Integer.MAX_VALUE) | 0xFF000000);
		}

		Type(int id, String type, int color) {
			this.setId(id);
			this.setType(type);
			this.setColor(color);
		}
		
		private static Map<Integer, Type> idToTypeMapping;

		public static Type getType(int id) {
			if (idToTypeMapping == null) {
				initMapping();
			}
			
			Type result = null;
			result = idToTypeMapping.get(id);
			return result;
		}
		
		private static void initMapping() {
			idToTypeMapping = new HashMap<Integer, MaintenanceEntry.Type>();
			for (Type type : values()) {
				idToTypeMapping.put(type.id, type);
			}
		}
	
		public String getType() {
			return type;
		}
		public void setType(String type) {
			this.type = type;
		}
		public int getId() {
			return id;
		}
		public void setId(int id) {
			this.id = id;
		}
		public int getColor() {
			return color;
		}

		public void setColor(int color) {
			this.color = color;
		}
	}
	
	public double getPartsCostSI() {
		if (getParts() == null) return 0;
		
		double partsCost = 0;
			for (ReplacementPart p : getParts()) {
				partsCost += p.getCostSI() * p.getQuantity();
			}
			
		return partsCost;
	}
	
	public int compareTo(MaintenanceEntry e) {
		return super.compareTo(e);
	}
	
	public double getLaborCost() {
		return laborCost;
	}
	public void setLaborCost(double laborCost, Unit currency) {
		this.laborCost = laborCost;
		this.laborCostCurrency = currency;
		setLaborCostSI(Currency.convertToSI(getLaborCost(), getLaborCostCurrency(), getEventDate()));
	}
	public double getLaborCostSI() {
		return laborCostSI;
	}

	public void setLaborCostSI(double laborCostSI) {
		this.laborCostSI = laborCostSI;
	}

	public Currency.Unit getLaborCostCurrency() {
		return laborCostCurrency;
	}

	public void setLaborCostCurrency(Currency.Unit laborCostCurrency) {
		this.laborCostCurrency = laborCostCurrency;
		setLaborCostSI(Currency.convertToSI(getLaborCost(), getLaborCostCurrency(), getEventDate()));
	}

	public LinkedList<ReplacementPart> getParts() {
		return parts;
	}
	public void setParts(LinkedList<ReplacementPart> parts) {
		this.parts = parts;
	}

	public Type getType() {
		return type;
	}

	public void setType(Type type) {
		this.type = type;
	}

	/****************************** Controller-relevant methods ***********************************/

	/**
	 * This method creates and provides a controller that will do all the synchronization updates on this object
	 * @return controller
	 */
	@Override
	public MaintenanceEntryController getController() {
		return new MaintenanceEntryController(this);
	}

	/****************************** Searchable interface methods follow ***************************/

	/**
	 * Method used to search for an object by its UUID within the Object tree of this Object.
	 * @param uuid of the searched object
	 * @return Record that matches the searched UUID
	 */
	public Record getRecordByUUID(UUID uuid) {
		// Are they looking for me? Delegate task to Record.getRecordByUUID to find out.
		Record result = super.getRecordByUUID(uuid);

		Iterator<ReplacementPart> p = parts.iterator();

		// Search deeper to find the right object
		while (result == null && p.hasNext()) {
			result = p.next().getRecordByUUID(uuid);
		}

		return result;
	}
}
