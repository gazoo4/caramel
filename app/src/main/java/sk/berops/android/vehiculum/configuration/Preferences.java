package sk.berops.android.vehiculum.configuration;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;

import sk.berops.android.vehiculum.Vehiculum;
import sk.berops.android.vehiculum.dataModel.Currency;
import sk.berops.android.vehiculum.dataModel.UnitConstants.ConsumptionUnit;
import sk.berops.android.vehiculum.dataModel.UnitConstants.CostUnit;
import sk.berops.android.vehiculum.dataModel.UnitConstants.DistanceUnit;
import sk.berops.android.vehiculum.dataModel.UnitConstants.VolumeUnit;

/**
 * 
 * @author akhgupta
 * Inspired by: https://github.com/akhgupta/Android-PreferenceDemo/blob/master/app/src/main/java/com/akhgupta/androidpreferencedemo/MyAppPreference.java
 * Singleton class accessing SharedPreferences
 *
 */
public class Preferences {
	private static final String CONSUMPTION_UNIT_KEY = "consumption_unit_key";
	private static final String COST_UNIT_KEY = "cost_unit_key";
	private static final String CURRENCY_KEY = "currency_key";
	private static final String DISTANCE_UNIT_KEY = "distance_unit_key";
	private static final String VOLUME_UNIT_KEY = "volume_unit_key";
	
	private static SharedPreferences mPreferences;
    private static Preferences mInstance;
    private static Editor mEditor;
    
    private Preferences() {
    }
    
    public static Preferences getInstance() {
    	if (mInstance == null) {
    		Context context = Vehiculum.context;
    		mInstance = new Preferences();
    		mPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    		mEditor = mPreferences.edit();
    	}
    	return mInstance;
    }
    
    private int getInt(String key, int defValue) {
    	return Integer.valueOf(mPreferences.getString(key, Integer.valueOf(defValue).toString()));
    }
    
    public ConsumptionUnit getConsumptionUnit() {
    	return ConsumptionUnit.getConsumptionUnit(getInt(CONSUMPTION_UNIT_KEY, 0));
    }
    
    public CostUnit getCostUnit() {
    	return CostUnit.getCostUnit(getInt(COST_UNIT_KEY, 1));
    }
    
    public Currency.Unit getCurrency() {
    	return Currency.Unit.getUnit(getInt(CURRENCY_KEY, 0));
    }
    
    public DistanceUnit getDistanceUnit() {
    	return DistanceUnit.getDistanceUnit(getInt(DISTANCE_UNIT_KEY, 0));
    }
    
    public VolumeUnit getVolumeUnit() {
    	return VolumeUnit.getVolumeUnit(getInt(VOLUME_UNIT_KEY, 0));
    }
}