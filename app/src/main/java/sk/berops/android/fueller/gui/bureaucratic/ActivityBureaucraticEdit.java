package sk.berops.android.fueller.gui.bureaucratic;

import android.content.Intent;
import android.os.Bundle;

import sk.berops.android.fueller.dataModel.expense.BureaucraticEntry;
import sk.berops.android.fueller.gui.MainActivity;

public class ActivityBureaucraticEdit extends ActivityBureaucraticAdd {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Intent intent = getIntent();
		int dynamicID = intent.getIntExtra("dynamicID", -1);
		bureaucraticEntry = (BureaucraticEntry) MainActivity.garage.getActiveCar().getHistory().getEntry(dynamicID);
		entry = bureaucraticEntry;
		editMode = true;
		super.onCreate(savedInstanceState);
	}
}
