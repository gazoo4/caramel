package sk.berops.android.fueller.dataModel;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;
import java.util.UUID;

import sk.berops.android.fueller.dataModel.expense.Entry;
import sk.berops.android.fueller.dataModel.expense.History;
import sk.berops.android.fueller.dataModel.expense.TyreChangeEntry;
import sk.berops.android.fueller.dataModel.maintenance.Tyre;
import sk.berops.android.fueller.dataModel.tags.Tag;
import sk.berops.android.fueller.gui.MainActivity;

@Root
public class Garage {
	
	@ElementList(inline = true, required = false)
	private LinkedList<Car> cars;

	@Element(name = "activeCar", required = false)
	private int activeCarId;

	@Element(name = "rootTag", required = false)
	private Tag rootTag;

	public Garage() {
		super();
		cars = new LinkedList<Car>();
		activeCarId = -1;

		Tag root = getRootTag();
		if (root == null) {
			root = new Tag();
			root.setName("rootTag");
			root.setDepth(0);
			root.setParent(null);
			setRootTag(root);
		}
	}

	public LinkedList<Car> getCars() {
		return cars;
	}

	public void setCars(LinkedList<Car> cars) {
		this.cars = cars;
	}

	public void addCar(Car car) {
		cars.add(car);
		setActiveCarId(getCars().size() - 1);
	}

	public Car getActiveCar() {
		if (getCars().size() == 0)
			return null;
		if (activeCarId < 0) {
			activeCarId = 0;
		}
		try {
			return cars.get(activeCarId);
		} catch (IndexOutOfBoundsException ex) {
			return cars.getFirst();
		}
	}

	public int getActiveCarId() {
		if ((this.activeCarId < 0) && (this.getCars().size() > 0)) {
			activeCarId = 0;
		}
		return this.activeCarId;
	}

	public void setActiveCarId(int id) {
		this.activeCarId = id;
	}

	public void initAfterLoad() {
		int dynamicId = 0;
		// Init cars
		for (Car c : cars) {
			c.initAfterLoad();
			for (Entry e : c.getHistory().getEntries()) {
				e.setDynamicId(dynamicId++);
			}
		}

		//Init tag tree
		this.rootTag.initAfterLoad();

		// Init tyres
		LinkedList<Tyre> tyres = getAllTyres();
		Car activeCar = getActiveCar();
		if (tyres != null) {
			for (Tyre t : getAllTyres()) {
				// We want to make sure that all tyres are initialized
				// If a tyre is installed on a car, it will get re-initialized with
				// the correct car reference through the below FOR loop
				t.initAfterLoad(activeCar);
			}
		}
	}

	/**
	 * Get a list of tyres from the supplied list of tyre UUIDs
	 * @param uuids
	 * @return
	 */
	public ArrayList<Tyre> getTyresByIDs(Collection<UUID> uuids) {
		ArrayList<Tyre> tyres = new ArrayList<>();
		for (UUID u : uuids) {
			if (u == null) {
				tyres.add(null);
				continue;
			}
			tyres.add(getTyreByID(u));
		}
		return tyres;
	}

	/**
	 * Method to get tyre by UUID
	 * @param u UUID supplied
	 * @return Tyre instance
	 */
	public Tyre getTyreByID(UUID u) {
		LinkedList<TyreChangeEntry> e;
		for (Car c: getCars()) {
			e = c.getHistory().getTyreChangeEntries();
			if (e.size() > 0) {
				return e.getLast().getTyreByID(u);
			}
		}

		return null;
	}

	/**
	 * Gives the list of all tyres ever bought and not trashed
	 * @return list of all tyres
	 */
	public LinkedList<Tyre> getAllTyres() {
		LinkedList<TyreChangeEntry> e;
		for (Car c: getCars()) {
			e = c.getHistory().getTyreChangeEntries();
			if (e.size() > 0) {
				return e.getLast().getAllTyres();
			}
		}

		return null;
	}

	/**
	 * The root tag for the tag tree structure relevant to this garage
	 */
	public Tag getRootTag() {
		return rootTag;
	}

	public void setRootTag(Tag rootTag) {
		this.rootTag = rootTag;
	}
}
