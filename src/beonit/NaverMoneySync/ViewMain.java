package beonit.NaverMoneySync;

import java.util.Calendar;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
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
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TabHost;
import android.widget.TextView;

public class ViewMain extends TabActivity {
    /** Called when the activity is first created. */
	
	final static int NOTI_ID = 1159;
	
	public void setSuccessNotify(){
    	Notification notification = new Notification(R.drawable.icon, "���̹� ����ο� �Է� �Ϸ�", 0);
    	notification.flags |= Notification.FLAG_AUTO_CANCEL;
    	Intent successIntent = new Intent();
    	PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, successIntent, 0);
    	notification.setLatestEventInfo(this, "���� ���� ����", "���ڳ���", pendingIntent);
    	NotificationManager nm = (NotificationManager)getSystemService(NOTIFICATION_SERVICE); // NotificationManager�� �ҷ��ɴϴ�.
    	nm.notify(NOTI_ID, notification);
	}
	
	//	static final String logTag = "SmsReceiver";

	
	TabHost mTabHost = null;
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        mTabHost = getTabHost();
        
        mTabHost.addTab(mTabHost.newTabSpec("tab_test1")
        		.setIndicator("���� ���")
        		.setContent(R.id.viewRecord)
        		);
        mTabHost.addTab(mTabHost.newTabSpec("tab_test2")
        		.setIndicator("���̹� ����")
        		.setContent(R.id.viewAccount)
        		);
        mTabHost.addTab(mTabHost.newTabSpec("tab_test3")
        		.setIndicator("������")
        		.setContent(R.id.viewRewrite)
        		);
        
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND,
                WindowManager.LayoutParams.FLAG_BLUR_BEHIND);
        
        SharedPreferences prefs = getSharedPreferences("NaverMoneySync", Context.MODE_PRIVATE);
        EditText editTextNaverId = (EditText)findViewById(R.id.EditTextAccount);
        EditText editTextNaverPasswd = (EditText)findViewById(R.id.EditTextPasswd);
        editTextNaverId.setText(prefs.getString("naverID", ""));
        editTextNaverPasswd.setText(prefs.getString("naverPasswd", ""));
        updateRewriteView(prefs);
        
        Button recordDate = (Button)findViewById(R.id.EditTextRecordDate);
        // get the current date
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

    	// ���� ������ ����.
		String id = prefs.getString("naverID", null);
		String passwd = prefs.getString("naverPasswd", null);
		if( id == null || passwd == null || id.length() == 0 || passwd.length() == 0 ){
			mTabHost.setCurrentTab( 1 );
		}
    	
		// Ư�� �������� �̵��ؾ� �Ѵٸ�...
    	int tab = 0;
    	try{
    		tab = savedInstanceState.getInt("goto", 0);
    		mTabHost.setCurrentTab( tab );
    	}catch(Exception e){
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

	public void onSubmitAccount(View view){
        EditText editTextNaverId = (EditText)findViewById(R.id.EditTextAccount);
        EditText editTextNaverPasswd = (EditText)findViewById(R.id.EditTextPasswd);

        SharedPreferences prefs = getSharedPreferences("NaverMoneySync", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("naverID", editTextNaverId.getText().toString());
		editor.putString("naverPasswd", editTextNaverPasswd.getText().toString());
		editor.commit();
		
		// TODO. �α��� üũ
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
    	if( doSubmit(items) ){
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

		return doSubmit(items);
    }
    
    public boolean doSubmit(String items){
		String id, passwd;
    	// ���̹� ���� ����
    	SharedPreferences prefs = getSharedPreferences("NaverMoneySync", Context.MODE_PRIVATE);
		id = prefs.getString("naverID", null);
		passwd = prefs.getString("naverPasswd", null);
		if( id == null || passwd == null || id.length() == 0 || passwd.length() == 0 ){
			AlertDialog.Builder alert = new AlertDialog.Builder(this);
			alert.setTitle( "���� ����" );
			alert.setMessage( "���̹� ������ ������ �ּ���" );
			alert.setPositiveButton(
					 "�ݱ�", new DialogInterface.OnClickListener() {
					    public void onClick( DialogInterface dialog, int which) {
					        dialog.dismiss();   //�ݱ�
					    }
					});
			alert.show();
			mTabHost.setCurrentTab(1);
			return false;
		}
		
		// network state check
    	if( checkNetwork() == false ){
    		AlertDialog.Builder alert = new AlertDialog.Builder(this);
			alert.setTitle( "��� �Ұ���" );
			alert.setMessage( "DB�� ����˴ϴ�." );
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
		QuickWriter writer = new QuickWriter(id, passwd, this);
		writer.setResultNoti(false);
		progressThread = new ProgressThread(mHandler, writer, items, this);
		progressThread.start();
		return true;
	}
    
    private class ProgressThread extends Thread {
    	Handler mHandler;
    	QuickWriter writer;
    	String items;
        ProgressThread(Handler h, QuickWriter writer, String items, Context context) {
        	this.writer = writer;
        	this.items = items;
            mHandler = h;
        }
        
        public void run() {
        	writer.quickWrite(items);
        	int state = QuickWriter.WRITE_READY;
        	int newState = QuickWriter.WRITE_READY;
        	for( int i=0; i<100; i++ ){
        		try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				newState = writer.getSendState();
        		if( state != newState ){
        			state = newState;
        			mHandler.sendEmptyMessage(state);
        		}
        		if( state == QuickWriter.WRITE_SUCCESS || state == QuickWriter.WRITE_FAIL ){
        			return;
        		}
        	}
        	mHandler.sendEmptyMessage(QuickWriter.WRITE_FAIL);
       }
    }
    
    // send
    private Handler mHandler = new Handler() {
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
				SharedPreferences prefs = getSharedPreferences("NaverMoneySync", Context.MODE_PRIVATE);
				updateRewriteView(prefs);
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
				alert.setMessage( "�ٽ� �õ��� �ּ���" );
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