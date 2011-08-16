package beonit.NaverMoneySync;

import java.util.Calendar;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.TabActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TextView;

public class ViewMain extends TabActivity implements OnTabChangeListener {
    /** Called when the activity is first created. */
	
	final static int NOTI_ID = 1159;
	
	TabHost mTabHost = null;
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        mTabHost = getTabHost();
        
        mTabHost.addTab(mTabHost.newTabSpec("tabWrite")
        		.setIndicator("���� ���")
        		.setContent(R.id.viewRecord)
        		);
        mTabHost.addTab(mTabHost.newTabSpec("tabNaverView")
        		.setIndicator("����� ��ȸ")
        		.setContent(R.id.viewNaver)
        		);
        mTabHost.addTab(mTabHost.newTabSpec("tabRewrite")
        		.setIndicator("������")
        		.setContent(R.id.viewRewrite)
        		);
        mTabHost.setOnTabChangedListener(this);
        
        for ( int tab = 0; tab < mTabHost.getTabWidget().getChildCount(); ++tab )
        {
        	mTabHost.getTabWidget().getChildAt(tab).getLayoutParams().height = 45;
        }	
        
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND,
//                WindowManager.LayoutParams.FLAG_BLUR_BEHIND);
        
        // rewrite setup
        SharedPreferences prefs = getSharedPreferences("NaverMoneySync", Context.MODE_PRIVATE);
        updateRewriteView(prefs);
        
        // get the current date
        Button recordDate = (Button)findViewById(R.id.EditTextRecordDate);
        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);
    	recordDate.setText(
            new StringBuilder()
                    // Month is 0 based so add 1
            		.append(mYear).append("�� ")
                    .append(mMonth + 1).append("�� ")
                    .append(mDay).append("��")
                    );

    	// ���� ������ ������ ���� ���� ��Ƽ��Ƽ ����
		String id = prefs.getString("naverID", null);
		String passwd = prefs.getString("naverPasswd", null);
		if( id == null || passwd == null || id.length() == 0 || passwd.length() == 0 ){
			Intent intent = new Intent(this, AccountSetting.class);
        	startActivityForResult(intent, 100);
		}
		else
			startNaverView(prefs);
	}
	
	public void onActivityResult(int requestCode, int resultCode, Intent intent){
		if( resultCode != RESULT_OK )
			return;
		startNaverView( getSharedPreferences("NaverMoneySync", Context.MODE_PRIVATE) );
		updateNaverView();
	}

	
	public class MyWebChromeClient extends WebChromeClient {
		@Override
		public void onProgressChanged(WebView view, int newProgress) {
			ViewMain.this.setProgress(newProgress * 100);
		}
	}
	
	private void startNaverView(SharedPreferences prefs) {
		// webview setting
        WebView wb = (WebView)findViewById(R.id.naverView);
        wb.setWillNotDraw( true );
        try {
        	wb.setWebChromeClient(new MyWebChromeClient());
			wb.setWebViewClient( new NaverViewClient(prefs.getString("naverID", null), SimpleCrypto.decrypt("SECGAL", prefs.getString("naverPasswd", null) ) ));
			
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
        wb.getSettings().setJavaScriptEnabled(true);
	}
	
	public static final int MENU_ACCOUNT_SETTING = 1;
	public static final int MENU_ABOUT = 2;
	public boolean onCreateOptionsMenu (Menu menu){
		menu.add(0, MENU_ACCOUNT_SETTING, 1, "naver ���� ����");
		menu.add(0, MENU_ABOUT, 1, "���α׷��� ���Ͽ�");
		return true;
	}
	
	/* Handles item selections */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	Intent intent;
        switch ( item.getItemId() ) {
        case MENU_ACCOUNT_SETTING:
        	WebView wb = (WebView)findViewById(R.id.naverView);
        	wb.setWillNotDraw(true);
        	intent = new Intent(this, AccountSetting.class);
        	startActivityForResult(intent, 0);
        	return true;
        case MENU_ABOUT:
        	intent = new Intent(this, Developer.class);
        	startActivity(intent);
        	return true;
        default:
        	return false;
		}
    }
    
    private void updateRewriteView(SharedPreferences prefs) {
    	TextView TextViewRewriteItmes = (TextView)findViewById(R.id.TextViewRewrite);
    	Button buttonRewrite = (Button)findViewById(R.id.ButtonRewrite);
    	Button buttonDismiss = (Button)findViewById(R.id.ButtonDismiss);
    	String items = prefs.getString("items", "");
    	if( items.length() == 0 ){
        	TextViewRewriteItmes.setText("������ ���� ����");
        	buttonRewrite.setEnabled(false);
        	buttonDismiss.setEnabled(false);
        }else{
        	TextViewRewriteItmes.setText(items);
        	buttonRewrite.setEnabled(true);
        	buttonDismiss.setEnabled(true);
        }
	}

    public void onSubmitRewrite(View view){
    	SharedPreferences prefs = getSharedPreferences("NaverMoneySync", Context.MODE_PRIVATE);
        String items = prefs.getString("items", "");
    	if( items.length() == 0 ){
    		AlertDialog.Builder alert = new AlertDialog.Builder(this);
			alert.setTitle( "���� ����" );
			alert.setMessage( "�������� ������ �����ϴ�" );
			alert.setPositiveButton(
					 "�ݱ�", new DialogInterface.OnClickListener() {
					    public void onClick( DialogInterface dialog, int which) {
					        dialog.dismiss();   //�ݱ�
					    }
					});
			alert.show();
			return;
    	}
    	if( doSubmit(items, true) ){
    		Editor ed = prefs.edit();
        	ed.putString("items", "");
        	ed.commit();
    	}
    }
    
    public void onSubmitRewriteDimiss(View view){
    	SharedPreferences prefs = getSharedPreferences("NaverMoneySync", Context.MODE_PRIVATE);
    	Editor ed = prefs.edit();
    	ed.putString("items", "");
    	ed.commit();
    	updateRewriteView(prefs);
    }
    
    private int mYear;
    private int mMonth;
    private int mDay;
    static final int DATE_DIALOG_ID = 0;
    public void onButtonDatePick(View v) {
        showDialog(DATE_DIALOG_ID);
    }
    
    ProgressThread progressThread;
    public ProgressDialog mProgressDialog;
	TabActivity activity = this;
	
    public boolean onSubmitRecord(View view){
    	// ���� ��ĭ Ȯ��
    	EditText editText = (EditText)findViewById(R.id.EditTextRecordContents);
    	EditText editMoney = (EditText)findViewById(R.id.EditTextRecordMoney);
    	if( editText.getText().length() == 0 || editMoney.getText().length() == 0 ){
    		AlertDialog.Builder alert = new AlertDialog.Builder(this);
			alert.setTitle( "���� ����" );
			alert.setMessage( "����� ������ ä���ּ���." );
			alert.setPositiveButton(
					 "�ݱ�", new DialogInterface.OnClickListener() {
					    public void onClick( DialogInterface dialog, int which) {
					        dialog.dismiss();   //�ݱ�
					    }
					});
			alert.show();
    		return false;
    	}

		// ��¥ v ��볻�� v ī�� or ���� v �ݾ� (v=����)
		String contents = editText.getText().toString();
		contents.replace(" ", "");
		String items = new String( new StringBuilder().append(mMonth+1).append("/")
								.append(mDay).append(" ")
								.append(contents).append(" ")
								.append("���� ")
								.append(editMoney.getText()).append("��")
								);

		return doSubmit(items, false);
    }
    
    public boolean doSubmit(String items, boolean failSave ){
		String id, passwd;
    	// ���̹� ���� ����
    	SharedPreferences prefs = getSharedPreferences("NaverMoneySync", Context.MODE_PRIVATE);
		id = prefs.getString("naverID", null);
		try {
			passwd = SimpleCrypto.decrypt("SECGAL", prefs.getString("naverPasswd", null) );
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		if( id == null || passwd == null || id.length() == 0 || passwd.length() == 0 ){
			AlertDialog.Builder alert = new AlertDialog.Builder(this);
			alert.setTitle( "���� ����" );
			alert.setMessage( "���̹� ������ ������ �ּ���\n�����Կ� ������� �ʽ��ϴ�" );
			alert.setPositiveButton(
					 "�ݱ�", new DialogInterface.OnClickListener() {
					    public void onClick( DialogInterface dialog, int which) {
					        dialog.dismiss();   //�ݱ�
					    }
					});
			alert.show();
			this.startActivity( new Intent(this, AccountSetting.class ));
			return false;
		}
		
		// network state check
    	if( checkNetwork() == false ){
    		AlertDialog.Builder alert = new AlertDialog.Builder(this);
			alert.setTitle( "��� �Ұ���" );
			alert.setMessage( "�����Կ� ������� �ʽ��ϴ�" );
			alert.setPositiveButton(
					 "�ݱ�", new DialogInterface.OnClickListener() {
					    public void onClick( DialogInterface dialog, int which) {
					        dialog.dismiss();   //�ݱ�
					    }
					});
			alert.show();
			return false;
    	}

    	// send thread and dialog start
    	mProgressDialog = ProgressDialog.show(this, "����� ����", "������...", false);
    	mProgressDialog.setCancelable(true);
		QuickWriter writer = new QuickWriter(id, passwd, this);
		writer.setFailSave(failSave);
		writer.setResultNoti(false);
		progressThread = new ProgressThread(mHandler, writer, items);
		progressThread.start();
		return true;
	}
    
    // send
    private Handler mHandler = new SyncHandler(); 
    public class SyncHandler extends Handler {
    	private AlertDialog.Builder alert = null;
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case QuickWriter.WRITE_READY:
				mProgressDialog.setMessage("���� ��...");
				break;
			case QuickWriter.WRITE_LOGIN:
				mProgressDialog.setMessage("�α��� ������ �ε�");
				break;
			case QuickWriter.WRITE_LOGIN_SUCCESS:
				mProgressDialog.setMessage("�α��� ����");
				break;
			case QuickWriter.WRITE_WRITING:
				mProgressDialog.setMessage("����� ���� �Է� ");
				break;
			case QuickWriter.WRITE_SUCCESS:
				mProgressDialog.dismiss(); // ProgressDialog ����
		    	EditText editText = (EditText)findViewById(R.id.EditTextRecordContents);
		    	EditText editMoney = (EditText)findViewById(R.id.EditTextRecordMoney);
				editText.setText("");
				editMoney.setText("");
				alert = new AlertDialog.Builder(activity);
				alert.setTitle( "�Է� ����" );
				alert.setMessage( "����Ǿ����ϴ�" );
				break;
			case QuickWriter.WRITE_LOGIN_FAIL:
				mProgressDialog.dismiss(); // ProgressDialog ����
				alert = new AlertDialog.Builder(activity);
				alert.setTitle( "�α��� ����" );
				alert.setMessage( "���̵� ��ȣ�� Ȯ���� �ּ���" );
				break;
			case QuickWriter.WRITE_FAIL:
				mProgressDialog.dismiss(); // ProgressDialog ����
				alert = new AlertDialog.Builder(activity);
				alert.setTitle( "���� ����" );
				alert.setMessage( "�ٽ� �õ��� �ּ��� \n���� �����Կ� ������� �ʽ��ϴ�." );
				break;
			case QuickWriter.WRITE_FAIL_REGISTER:
				mProgressDialog.dismiss(); // ProgressDialog ����
				alert = new AlertDialog.Builder(activity);
				alert.setTitle( "����� ���� �ȵ�" );
				alert.setMessage( "���� ���� �ݰ� ����� ��/PC �� ���� ������Ǹ� ó���ϰ� ������ �ּ���." );
				break;
			default:
				break;
			}
			if( alert != null ){
				alert.setPositiveButton(
					 "�ݱ�", new DialogInterface.OnClickListener() {
					    public void onClick( DialogInterface dialog, int which) {
					        dialog.dismiss();   //�ݱ�
					    }
					});
				alert.show();
			}
		}
	};
    
    // check network        
    public boolean checkNetwork() 
    {
        boolean result = true;
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        // boolean isWifiAvail = ni.isAvailable();
        boolean isWifiConn = ni.isConnected();
        ni = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        // boolean isMobileAvail = ni.isAvailable();
        boolean isMobileConn = ni.isConnected();
        if (isWifiConn == false && isMobileConn == false)
            result = false;
        return result;
    }
    
    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
        case DATE_DIALOG_ID:
            return new DatePickerDialog(this,
                        mDateSetListener,
                        mYear, mMonth, mDay);
        }
        return null;
    }
    
    // updates the date we display in the TextView
    private void updateDisplay() {
    	Button recordDate = (Button)findViewById(R.id.EditTextRecordDate);
    	recordDate.setText(
            new StringBuilder()
                    // Month is 0 based so add 1
            		.append(mYear).append("�� ")
                    .append(mMonth + 1).append("�� ")
                    .append(mDay).append("��")
                    );
    }

    @Override
    public void onTabChanged(String tabId) {
    	if( tabId.equals("tabNaverView") ){
    		WebView wb = (WebView)findViewById(R.id.naverView);
    		if( wb.willNotDraw() )
    			updateNaverView();
    	}
    	else if( tabId.equals("tabRewrite") ){
			SharedPreferences prefs = getSharedPreferences("NaverMoneySync", Context.MODE_PRIVATE);
			updateRewriteView(prefs);
    	}
   	    	
    }

	@SuppressWarnings("null")
	private void updateNaverView() {
		mTabHost.setCurrentTab(1);

		SharedPreferences prefs = getSharedPreferences("NaverMoneySync", Context.MODE_PRIVATE);
		String id = prefs.getString("naverID", null);
		String passwd = prefs.getString("naverPasswd", null);
		boolean hasError = false;
		AlertDialog.Builder alert = null;
		if( id == null || passwd == null || id.length() == 0 || passwd.length() == 0 ){
			alert = new AlertDialog.Builder(this);
			alert.setTitle( "���� ���� ����" );
			alert.setMessage( "���� ������ �Է��� �ּ���." );
			hasError = true;
			return;
		}
		if( checkNetwork() == false ){
			alert = new AlertDialog.Builder(this);
			alert.setTitle( "��� �Ұ���" );
			alert.setMessage( "DB�� ����˴ϴ�." );
			hasError = true;
			return;
		}
		if( hasError ){
			try{
				alert.setPositiveButton(
						 "�ݱ�", new DialogInterface.OnClickListener() {
						    public void onClick( DialogInterface dialog, int which) {
						    	dialog.dismiss();   //�ݱ�
						    }
						});
				alert.show();
			}
			catch (NullPointerException e){
				e.printStackTrace();
			}
			catch (Exception e){
				e.printStackTrace();
			}
			return;
		}
		WebView wb = (WebView)findViewById(R.id.naverView);
		wb.loadUrl("https://nid.naver.com/nidlogin.login?svctype=262144&url=http://beta.moneybook.naver.com/m/view.nhn?method=monthly");
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
	     if (keyCode == KeyEvent.KEYCODE_BACK && mTabHost.getCurrentTab() == 1 ) {
	    	 Builder alert = new AlertDialog.Builder(this);
	    	 alert.setTitle( "�� ����" );
	    	 alert.setMessage( "���� ���� �մϱ�?" );
	    	 alert.setPositiveButton(
					 "����", new DialogInterface.OnClickListener() {
					    public void onClick( DialogInterface dialog, int which) {
					    	finish();
					    }
					});
	    	 alert.setNegativeButton( "���̹� �ڷ� ����", new DialogInterface.OnClickListener() {
					    public void onClick( DialogInterface dialog, int which) {
					    	// ���̹� �ڷΰ���
					    	WebView wb = (WebView)findViewById(R.id.naverView);
					    	if( wb.canGoBack() )
					    		wb.goBack();
					    }
					});
			alert.show();
	    	 return true;
	     }
	     return super.onKeyDown(keyCode, event);    
	}
    
    private DatePickerDialog.OnDateSetListener mDateSetListener =
            new DatePickerDialog.OnDateSetListener() {
                public void onDateSet(DatePicker view, int year, 
                                      int monthOfYear, int dayOfMonth) {
                    mYear = year;
                    mMonth = monthOfYear;
                    mDay = dayOfMonth;
                    updateDisplay();
                }
            };
}