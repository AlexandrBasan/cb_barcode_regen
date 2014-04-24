package com.cbsb.barcodegen;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.app.Activity;
import android.app.Dialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.ComponentName;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.ViewAnimator;
import android.util.Log;
import com.cbsb.barcodegen.R;

public class Launcher extends Activity {
	private static final String TAG = "Launcher";

	private static final long HOUR = 60*60*1000;;

	private static final int DEFAULT_TYPE = 6;

	private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy/MM/dd");
	private static final SimpleDateFormat TIME_FORMAT = new SimpleDateFormat("HH:mm");

	private Spinner mSpinner;
	private Button mButton;
	private ViewAnimator mViewAnimator;

	private EditText mEventTitle;
	private EditText mEventStartDate;
	private EditText mEventEndDate;
	private EditText mEventStartTime;
	private EditText mEventEndTime;
	private EditText mEventLocation;
	private EditText mEventDescription;

	private EditText mContactName;
	private EditText mContactCompany;
	private EditText mContactPhone;
	private EditText mContactEmail;
	private EditText mContactAddress;
	private EditText mContactWebsite;
	private EditText mContactMemo;

	private EditText mEmailEmail;

	private EditText mGeoLatitude;
	private EditText mGeoLongitude;
	private EditText mGeoQuery;

	private EditText mPhonePhone;

	private EditText mSmsPhone;
	private EditText mSmsMessage;

	private EditText mTextText;

	private EditText mUrlUrl;

	private EditText mWifiSsid;
	private EditText mWifiPassword;
	private Spinner mWifiType;

	private Calendar mStartDate;
	private Calendar mEndDate;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

		mSpinner = (Spinner)findViewById(R.id.spinner);
		mButton = (Button)findViewById(R.id.button);
		mViewAnimator = (ViewAnimator)findViewById(R.id.animator);

		mEventTitle = (EditText)findViewById(R.id.event_title);
		mEventStartDate = (EditText)findViewById(R.id.event_sdate);
		mEventStartTime = (EditText)findViewById(R.id.event_stime);
		mEventEndDate = (EditText)findViewById(R.id.event_edate);
		mEventEndTime = (EditText)findViewById(R.id.event_etime);
		mEventLocation = (EditText)findViewById(R.id.event_location);
		mEventDescription = (EditText)findViewById(R.id.event_description);

		mContactName = (EditText)findViewById(R.id.contact_name);
		mContactCompany = (EditText)findViewById(R.id.contact_company);
		mContactPhone = (EditText)findViewById(R.id.contact_phone);
		mContactEmail = (EditText)findViewById(R.id.contact_email);
		mContactAddress = (EditText)findViewById(R.id.contact_address);
		mContactWebsite = (EditText)findViewById(R.id.contact_website);
		mContactMemo = (EditText)findViewById(R.id.contact_memo);

		mEmailEmail = (EditText)findViewById(R.id.email_email);

		mGeoLatitude = (EditText)findViewById(R.id.geo_latitude);
		mGeoLongitude = (EditText)findViewById(R.id.geo_longitude);
		mGeoQuery = (EditText)findViewById(R.id.geo_query);

		mPhonePhone = (EditText)findViewById(R.id.phone_phone);

		mSmsPhone = (EditText)findViewById(R.id.sms_phone);
		mSmsMessage = (EditText)findViewById(R.id.sms_message);

		mTextText = (EditText)findViewById(R.id.text_text);

		mUrlUrl = (EditText)findViewById(R.id.url_url);

		mWifiSsid = (EditText)findViewById(R.id.wifi_ssid);
		mWifiPassword = (EditText)findViewById(R.id.wifi_password);
		mWifiType = (Spinner)findViewById(R.id.wifi_type);

		mSpinner.setOnItemSelectedListener(mOnSpinnerSelected);
		mButton.setOnClickListener(mOnButtonClicked);

		mEventStartDate.setOnClickListener(mOnDateClicked);
		mEventStartTime.setOnClickListener(mOnTimeClicked);
		mEventEndDate.setOnClickListener(mOnDateClicked);
		mEventEndTime.setOnClickListener(mOnTimeClicked);

		mSpinner.setSelection(DEFAULT_TYPE);

		mStartDate = Calendar.getInstance();
		mEndDate = Calendar.getInstance();
		mEndDate.setTimeInMillis(mStartDate.getTime().getTime()+HOUR);
		updateDateTime();

		String text = getIntent().getStringExtra(Intent.EXTRA_TEXT);
		if (text != null && text.length() > 0) {
			mTextText.setText(text);
		}
    }

	protected void updateDateTime() {
		mEventStartDate.setText(DATE_FORMAT.format(mStartDate.getTime()));
		mEventEndDate.setText(DATE_FORMAT.format(mEndDate.getTime()));
		mEventStartTime.setText(TIME_FORMAT.format(mStartDate.getTime()));
		mEventEndTime.setText(TIME_FORMAT.format(mEndDate.getTime()));
	}

	protected void encodeContent(Intent intent) {
		Bundle bundle = new Bundle();
		switch (mSpinner.getSelectedItemPosition()) {
		case 0: // event
			intent.putExtra(Encoder.TYPE, Encoder.TYPE_EVENT);
			bundle.putString(Encoder.EVENT_SUMMARY, mEventTitle.getText().toString());
			bundle.putString(Encoder.EVENT_START, Encoder.formatDate(mStartDate.getTime()));
			bundle.putString(Encoder.EVENT_END, Encoder.formatDate(mEndDate.getTime()));
			bundle.putString(Encoder.EVENT_LOCATION, mEventLocation.getText().toString());
			bundle.putString(Encoder.EVENT_DESCRIPTION, mEventDescription.getText().toString());
			intent.putExtra(Encoder.DATA, bundle);
			break;

		case 1: // contact
			intent.putExtra(Encoder.TYPE, Encoder.TYPE_CONTACT);
			bundle.putString(Encoder.CONTACT_NAME, mContactName.getText().toString());
			bundle.putString(Encoder.CONTACT_COMPANY, mContactCompany.getText().toString());
			bundle.putString(Encoder.CONTACT_PHONE, mContactPhone.getText().toString());
			bundle.putString(Encoder.CONTACT_EMAIL, mContactEmail.getText().toString());
			bundle.putString(Encoder.CONTACT_ADDRESS, mContactAddress.getText().toString());
			bundle.putString(Encoder.CONTACT_WEBSITE, mContactWebsite.getText().toString());
			bundle.putString(Encoder.CONTACT_MEMO, mContactMemo.getText().toString());
			intent.putExtra(Encoder.DATA, bundle);
			break;

		case 2: // email
			intent.putExtra(Encoder.TYPE, Encoder.TYPE_EMAIL);
			intent.putExtra(Encoder.DATA, mEmailEmail.getText().toString());
			break;

		case 3: // geo
			intent.putExtra(Encoder.TYPE, Encoder.TYPE_GEO);
			bundle.putFloat(Encoder.GEO_LATITUDE, Float.parseFloat(mGeoLatitude.getText().toString()));
			bundle.putFloat(Encoder.GEO_LONGITUDE, Float.parseFloat(mGeoLongitude.getText().toString()));
			bundle.putString(Encoder.GEO_QUERY, mGeoQuery.getText().toString());
			intent.putExtra(Encoder.DATA, bundle);
			break;

		case 4: // phone
			intent.putExtra(Encoder.TYPE, Encoder.TYPE_PHONE);
			intent.putExtra(Encoder.DATA, mPhonePhone.getText().toString());
			break;

		case 5: // sms
			intent.putExtra(Encoder.TYPE, Encoder.TYPE_SMS);
			bundle.putString(Encoder.SMS_PHONE, mSmsPhone.getText().toString());
			bundle.putString(Encoder.SMS_MESSAGE, mSmsMessage.getText().toString());
			intent.putExtra(Encoder.DATA, bundle);
			break;

		case 6: // text
			intent.putExtra(Encoder.TYPE, Encoder.TYPE_TEXT);
			intent.putExtra(Encoder.DATA, mTextText.getText().toString());
			break;

		case 7: // url
			intent.putExtra(Encoder.TYPE, Encoder.TYPE_URL);
			intent.putExtra(Encoder.DATA, mUrlUrl.getText().toString());
			break;

		case 8: // wifi
			intent.putExtra(Encoder.TYPE, Encoder.TYPE_WIFI);
			bundle.putString(Encoder.WIFI_SSID, mWifiSsid.getText().toString());
			bundle.putString(Encoder.WIFI_PASSWORD, mWifiPassword.getText().toString());
			bundle.putString(Encoder.WIFI_TYPE, Encoder.WIFI_TYPES[mWifiType.getSelectedItemPosition()]);
			intent.putExtra(Encoder.DATA, bundle);
			break;
		}
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case R.id.dialog_start_date:
			return new DatePickerDialog(this, mOnStartDateSet,
					mStartDate.get(Calendar.YEAR),
					mStartDate.get(Calendar.MONTH),
					mStartDate.get(Calendar.DAY_OF_MONTH));

		case R.id.dialog_end_date:
			return new DatePickerDialog(this, mOnEndDateSet,
					mEndDate.get(Calendar.YEAR),
					mEndDate.get(Calendar.MONTH),
					mEndDate.get(Calendar.DAY_OF_MONTH));

		case R.id.dialog_start_time:
			return new TimePickerDialog(this, mOnStartTimeSet,
					mStartDate.get(Calendar.HOUR_OF_DAY),
					mStartDate.get(Calendar.MINUTE),
					true);

		case R.id.dialog_end_time:
			return new TimePickerDialog(this, mOnEndTimeSet,
					mEndDate.get(Calendar.HOUR_OF_DAY),
					mEndDate.get(Calendar.MINUTE),
					true);
		}
		return null;
	}

    @Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);

		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.launcher, menu);

		Intent intent = new Intent(null, getIntent().getData());
		intent.addCategory(Intent.CATEGORY_ALTERNATIVE);
		menu.addIntentOptions(Menu.CATEGORY_ALTERNATIVE, 0, 0, new ComponentName(this, Launcher.class), null, intent, 0, null);

		return true;
	}

    @Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.menu_help:
				MenuHelper.showHelp(this);
				break;

			case R.id.menu_pick:
				MenuHelper.showDecode(this);
				break;
			case R.id.menu_about:	
				Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://proalab.com/?page_id=517"));
				startActivity(browserIntent);
				break;
		}
		return super.onOptionsItemSelected(item);
	}

	private final Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case R.id.encode_succeeded:
				Log.d(TAG, "succeeded");
				break;
			case R.id.encode_failed:
				Log.d(TAG, "failed");
				break;
			}
		}
	};

	private AdapterView.OnItemSelectedListener mOnSpinnerSelected = new AdapterView.OnItemSelectedListener() {
		@Override
		public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
			Log.d(TAG, "clicked "+position);
			mViewAnimator.setDisplayedChild(position);
		}

		@Override
		public void onNothingSelected(AdapterView<?> parent) {
		}
	};

	private View.OnClickListener mOnButtonClicked = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			Intent intent = new Intent(Launcher.this, Encode.class);
			intent.setAction(Intent.ACTION_SEND);
			encodeContent(intent);
			startActivity(intent);
		}
	};

	private View.OnClickListener mOnDateClicked = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			if (v == mEventStartDate) {
				showDialog(R.id.dialog_start_date);
			} else if (v == mEventEndDate) {
				showDialog(R.id.dialog_end_date);
			}
		}
	};

	private View.OnClickListener mOnTimeClicked = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			if (v == mEventStartTime) {
				showDialog(R.id.dialog_start_time);
			} else if (v == mEventEndTime) {
				showDialog(R.id.dialog_end_time);
			}
		}
	};

	private DatePickerDialog.OnDateSetListener mOnStartDateSet = new DatePickerDialog.OnDateSetListener() {
		public void onDateSet(DatePicker v, int year, int month, int day) {
			mStartDate.set(Calendar.YEAR, year);
			mStartDate.set(Calendar.MONTH, month);
			mStartDate.set(Calendar.DAY_OF_MONTH, day);
			updateDateTime();
		}
	};

	private DatePickerDialog.OnDateSetListener mOnEndDateSet = new DatePickerDialog.OnDateSetListener() {
		public void onDateSet(DatePicker v, int year, int month, int day) {
			mEndDate.set(Calendar.YEAR, year);
			mEndDate.set(Calendar.MONTH, month);
			mEndDate.set(Calendar.DAY_OF_MONTH, day);
			updateDateTime();
		}
	};

	private TimePickerDialog.OnTimeSetListener mOnStartTimeSet = new TimePickerDialog.OnTimeSetListener() {
		public void onTimeSet(TimePicker v, int hour, int minute) {
			mStartDate.set(Calendar.HOUR_OF_DAY, hour);
			mStartDate.set(Calendar.MINUTE, minute);
			/*
			if (mEndDate.before(mStartDate)) {
				mEndDate = Calendar.getInstance();
				mEndDate.setTime(mStartDate.getTime());
			}
			*/
			updateDateTime();
		}
	};

	private TimePickerDialog.OnTimeSetListener mOnEndTimeSet = new TimePickerDialog.OnTimeSetListener() {
		public void onTimeSet(TimePicker v, int hour, int minute) {
			mEndDate.set(Calendar.HOUR_OF_DAY, hour);
			mEndDate.set(Calendar.MINUTE, minute);
			/*
			if (mEndDate.before(mStartDate)) {
				mStartDate = Calendar.getInstance();
				mStartDate.setTime(mEndDate.getTime());
			}
			*/
			updateDateTime();
		}
	};
}
