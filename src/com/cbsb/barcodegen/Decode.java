package com.cbsb.barcodegen;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.UUID;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.view.View;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewAnimator;
import android.util.Log;

import com.cbsb.barcodegen.R;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.Result;
import com.google.zxing.WriterException;
/*
import com.google.zxing.client.result.AddressBookParsedResult;
*/
import com.google.zxing.client.result.ParsedResult;
import com.google.zxing.qrcode.encoder.ByteMatrix;

public class Decode extends Activity {
	private static final String TAG = "Decode";

	private static final int BITMAP_SIZE = 480;

	private ImageView mImage;
	private TextView mSummary;
	private TextView mDetail;
	private Button mShare;

	private Bitmap mBitmap;
	private boolean mFirstLayout;
	private Decoder mDecoder;

	private ParsedResult mResult;
	private String mContent;
	private String mHash;
	private Uri mUri;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.view);

		mImage = (ImageView)findViewById(R.id.image);
		mSummary = (TextView)findViewById(R.id.summary);
		mDetail = (TextView)findViewById(R.id.detail);
		mShare = (Button)findViewById(R.id.share);

		mShare.setOnClickListener(mOnShare);

		mDecoder = new Decoder();
    }

	@Override
	protected void onResume() {
		super.onResume();

		/*
		View layout = findViewById(R.id.container);
		layout.getViewTreeObserver().addOnGlobalLayoutListener(mOnGlobalLayout);
		mFirstLayout = true;
		*/
		Intent intent = getIntent();
		Bundle extras = intent.getExtras();
		if (extras != null) {
			Uri uri = (Uri)extras.getParcelable(Intent.EXTRA_STREAM);
			if (mUri == null && uri != null) {
				mUri = uri;
			}
		}
		if (mUri == null) {
			doPick();
		}
		if (extras != null) {
			extractBarcode();
		}
	}

	@Override
	protected void onActivityResult(int reqCode, int resCode, Intent data) {
		if (resCode != RESULT_OK) {
			finish();
		}
		switch (reqCode) {
		case R.id.request_pick:
			if (data == null) {
				finish();
				break;
			}
			mUri = data.getData();
			Log.d(TAG, "onActivityResult() "+mUri);
			extractBarcode();
			break;
		}
	}

	private void extractBarcode() {
		/*
		View view = findViewById(R.id.container);
		int resolution = Math.min(view.getWidth(), view.getHeight());
		resolution = resolution*7/8;
		Log.d(TAG, "resolution "+resolution);
		*/

		setProgressBarIndeterminateVisibility(true);
		Thread t = new LoadThread(mHandler, mUri);
		t.start();
	}

	private void updateDisplay() {
		mSummary.setText(String.format(getString(R.string.format_summary),
				mResult.getType(),
				mResult.toString()));
		mDetail.setText(mContent.replace("\r", ""));
		mShare.setVisibility(View.VISIBLE);
	}

	private void doPick() {
		Intent intent = new Intent();
		intent.setType("image/*");
		intent.setAction(Intent.ACTION_GET_CONTENT);
		startActivityForResult(Intent.createChooser(intent, getText(R.string.message_complete_action_with)), R.id.request_pick);
	}

	private void doShare() {
		Intent intent = new Intent(Intent.ACTION_SEND);
		intent.setType("text/plain");
		intent.putExtra(Intent.EXTRA_TEXT, mContent);
		startActivity(Intent.createChooser(intent, getText(R.string.button_share)));
	}

	private final Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			AlertDialog.Builder b;

			switch (msg.what) {
			case R.id.load_succeeded:
				Log.d(TAG, "load succeeded "+mUri);
				mBitmap = (Bitmap)msg.obj;
				mImage.setImageBitmap(mBitmap);
				mDecoder.extractBarcode(mBitmap, mHandler);
				break;

			case R.id.load_failed:
				setProgressBarIndeterminateVisibility(false);
				Log.d(TAG, "loaded failed "+mUri);
				b = new AlertDialog.Builder(Decode.this);
				b.setMessage(getString(R.string.message_load_failed)+"\n"+mUri);
				b.setPositiveButton(R.string.button_ok, mOnClicked);
				b.show();
				break;

			case R.id.decode_succeeded:
				setProgressBarIndeterminateVisibility(false);
				Log.d(TAG, "decode succeeded "+mUri);
				Decoder.ResultHolder h = (Decoder.ResultHolder)msg.obj;
				mResult = h.parsedResult;
				mContent = h.result.toString();
				updateDisplay();
				break;

			case R.id.decode_failed:
				setProgressBarIndeterminateVisibility(false);
				Log.d(TAG, "decode failed "+mUri);
				mImage.setImageBitmap((Bitmap)msg.obj);
				mDetail.setText(getString(R.string.message_barcode_not_found));
				break;
			}
		}
	};

	private final View.OnClickListener mOnSave = new View.OnClickListener() {
		public void onClick(View v) {
			/*
			Thread t = new SaveThread(mHandler, false);
			t.start();
			*/
		}
	};

	private final View.OnClickListener mOnShare = new View.OnClickListener() {
		public void onClick(View v) {
			doShare();
		}
	};

	private final View.OnClickListener mOnEdit = new View.OnClickListener() {
		public void onClick(View v) {
			Intent intent = new Intent(Decode.this, Launcher.class);
			intent.putExtra(Intent.EXTRA_TEXT, mContent);
			startActivity(intent);
		}
	};

/*
	private final OnGlobalLayoutListener mOnGlobalLayout = new OnGlobalLayoutListener() {
		public void onGlobalLayout() {
			if (mFirstLayout) {
				Log.d(TAG, "onGlobalLayout() "+mUri);
				extractBarcode();
				mFirstLayout = false;
			}
		}
	};
*/

	private final DialogInterface.OnClickListener mOnClicked = new DialogInterface.OnClickListener() {
		public void onClick(DialogInterface dialog, int which) {
			finish();
		}
	};

	private class LoadThread extends Thread {
		private Uri mUri;
		private Handler mHandler;

		public LoadThread(Handler handler, Uri uri) {
			mUri = uri;
			mHandler = handler;
		}

		public void run() {
			Log.d(TAG, "loading "+mUri);
			Bitmap bm = Util.getBitmap(
					getContentResolver(),
					mUri,
					BITMAP_SIZE);
			if (bm != null) {
				Log.d(TAG, "loaded "+mUri);
				Message msg = mHandler.obtainMessage(R.id.load_succeeded);
				msg.obj = bm;
				msg.sendToTarget();
			} else {
				Log.d(TAG, "loading "+mUri+" return null");
				Message msg = mHandler.obtainMessage(R.id.load_failed);
				msg.sendToTarget();
			}
		}
	}
}
