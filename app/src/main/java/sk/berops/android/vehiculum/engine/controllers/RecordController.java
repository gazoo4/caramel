package sk.berops.android.vehiculum.engine.controllers;

import android.util.Log;

import java.util.UUID;

import sk.berops.android.vehiculum.dataModel.Record;

/**
 * @author Bernard Halas
 * @date 5/25/17
 */

public class RecordController {
	public static final String LOG_TAG = "RecordController";

	private Record record;

	public RecordController(Record record) {
		this.record = record;
	}

	public boolean createRecord(Record child) {
		// There's no complex object structure in Record where a new object could be referenced
		return false;
	}

	public boolean updateRecord(Record recordUpdate) {
		boolean updateDone = false;
		if (!record.getComment().equals(recordUpdate.getComment())) {
			logUpdate("comment");
			record.setComment(recordUpdate.getComment());
			updateDone = true;
		}

		if (!record.getModifiedDate().equals(recordUpdate.getModifiedDate())) {
			logUpdate("modifiedDate");
			record.setModifiedDate(recordUpdate.getModifiedDate());
			updateDone = true;
		}

		return updateDone;
	}

	public boolean deleteRecursively(UUID deleteUUID) {
		// There's no complex object structure in Record where a referenced object needs to be deleted from
		return false;
	}

	public static void logUpdate(String component) {
		Log.d(LOG_TAG, "Updating "+ component);
	}
}
