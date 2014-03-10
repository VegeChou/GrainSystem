package com.aisino.grain.model;

import android.content.Context;

import com.aisino.grain.model.rfid.RfidAdapter;

public class SyncBlock {
	
	public static boolean SyncBlock4(RfidAdapter rfidAdapter,Context context) {
		int res = rfidAdapter.syncBlock4();
		if (res == 0) {
//			GrainAlertDialog.AlertDialog(GrainAlertDialog.TIPS, "Ð´¿¨Block1³É¹¦", context);
			return true;
		} else {
//			GrainAlertDialog.AlertDialog(GrainAlertDialog.TIPS, "Ð´¿¨Block1Ê§°Ü", context);
			return false;
		}
	}
	
	public static boolean SyncBlock1(RfidAdapter rfidAdapter,Context context) {
		int res = rfidAdapter.syncBlock1();
		if (res == 0) {
//			GrainAlertDialog.AlertDialog(GrainAlertDialog.TIPS, "Ð´¿¨Block5³É¹¦", context);
			return true;
		} else {
//			GrainAlertDialog.AlertDialog(GrainAlertDialog.TIPS, "Ð´¿¨Block5Ê§°Ü", context);
			return false;
		}
	}
	
	public static boolean SyncBlock2(RfidAdapter rfidAdapter,Context context) {
		int res = rfidAdapter.syncBlock2();
		if (res == 0) {
//			GrainAlertDialog.AlertDialog(GrainAlertDialog.TIPS, "Ð´¿¨Block6³É¹¦", context);
			return true;
		} else {
//			GrainAlertDialog.AlertDialog(GrainAlertDialog.TIPS, "Ð´¿¨Block6Ê§°Ü", context);
			return false;
		}
	}
}
