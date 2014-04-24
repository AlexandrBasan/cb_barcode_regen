package com.cbsb.barcodegen;

import android.content.Context;
import android.content.Intent;
import com.cbsb.barcodegen.R;

public class MenuHelper {
	public static void showHelp(Context context) {
		context.startActivity(new Intent(context, Help.class));
	}

	public static void showDecode(Context context) {
		context.startActivity(new Intent(context, Decode.class));
	}
}
