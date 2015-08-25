package sk.berops.android.fueller.gui.service;

import sk.berops.android.fueller.dataModel.expense.ServiceEntry;
import sk.berops.android.fueller.gui.MainActivity;
import android.content.Intent;
import android.os.Bundle;

public class ActivityServiceEdit extends ActivityServiceAdd {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Intent intent = getIntent();
		int dynamicID = intent.getIntExtra("dynamicID", -1);
		serviceEntry = (ServiceEntry) MainActivity.garage.getActiveCar().getHistory().getEntry(dynamicID);
		entry = serviceEntry;
		editMode = true;
		super.onCreate(savedInstanceState);
	}
	
	@Override
	protected void initializeGuiObjects() {
		super.initializeGuiObjects();
		editTextMileage.setText(Double.valueOf(serviceEntry.getMileage()).toString());
		editTextCost.setText(Double.valueOf(serviceEntry.getCost()).toString());
		editTextComment.setText(serviceEntry.getComment());
		
		spinnerCurrency.setSelection(serviceEntry.getCurrency().getId());
		spinnerServiceType.setSelection(serviceEntry.getType().getId());
	}
}