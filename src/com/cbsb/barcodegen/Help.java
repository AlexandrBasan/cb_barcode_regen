package com.cbsb.barcodegen;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.KeyEvent;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.util.Log;
import com.cbsb.barcodegen.R;

public class Help extends Activity {
	private static final String TAG = "Help";

	private static final String DEFAULT_URL = "file:///android_asset/html/index.html";

	private WebView mWebView;
	private Button mBack;
	private Button mDone;

	@Override
	protected void onCreate(Bundle state) {
		super.onCreate(state);
		setContentView(R.layout.help);

		mBack = (Button)findViewById(R.id.back);
		mDone = (Button)findViewById(R.id.done);
		mWebView = (WebView)findViewById(R.id.webview);

		mBack.setOnClickListener(mOnBack);
		mDone.setOnClickListener(mOnDone);
		mWebView.setWebViewClient(mWebClient);

		if (state != null) {
			mWebView.restoreState(state);
		} else {
			mWebView.loadUrl(DEFAULT_URL);
		}
	}

	@Override
	protected void onSaveInstanceState(Bundle state) {
		mWebView.saveState(state);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (mWebView.canGoBack()) {
				mWebView.goBack();
				return true;
			}
		}
		return super.onKeyDown(keyCode, event);
	}

	private final Button.OnClickListener mOnBack = new Button.OnClickListener() {
		public void onClick(View view) {
			mWebView.goBack();
		}
	};

	private final Button.OnClickListener mOnDone = new Button.OnClickListener() {
		public void onClick(View view) {
			finish();
		}
	};

	private final WebViewClient mWebClient = new WebViewClient() {
		@Override
		public void onPageFinished(WebView view, String url) {
			setTitle(view.getTitle());
			mBack.setEnabled(view.canGoBack());
		}
	};
}
