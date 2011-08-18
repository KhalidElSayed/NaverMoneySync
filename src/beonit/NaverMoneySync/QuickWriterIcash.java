package beonit.NaverMoneySync;

import java.util.ArrayList;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class QuickWriterIcash extends QuickWriter {
	public QuickWriterIcash(String id, String passwd, Context context){
		super(id, passwd, context);
		mWebView.setWebViewClient(new ICashViewClient());
	}
	
	public boolean quickWrite(ArrayList<String> items){
		super.quickWrite(items, "http://m.icashhouse.co.kr");
		mWebView.addJavascriptInterface(new JSInterfaceICash(), "HTMLOUT");
		mWebView.setWillNotDraw(true);
        return true;
	}
	
	class ICashViewClient extends WebViewClient{
    	@Override
    	public void onPageFinished(WebView view, String url){
    		Log.i("beonit", "onPageFinished : " + url);
    		if( url.equals("http://m.icashhouse.co.kr/")){
    			switch( writeState ){
    			case WRITE_READY:
        			Log.v("beonit", "onPageFinished, WRITE_READY");
    				view.loadUrl("javascript:Username.value='" + id + "'");
    				view.loadUrl("javascript:Password.value='" + passwd + "'");
    				view.loadUrl("javascript:login.submit( check_login( document.getElementById('frm_login') ) )");
    				writeState = QuickWriter.WRITE_LOGIN_ATTEMPT;
    				view.reload();
    				break;
    			case WRITE_LOGIN_ATTEMPT:
    				Log.v("beonit", "onPageFinished, WRITE_LOGIN_ATTEMPT");
    				view.loadUrl("javascript:window.HTMLOUT.checkLoginResult( document.getElementById('others').innerHTML );");
    				break;
    			}
    		}
    		else if( url.equals("http://m.icashhouse.co.kr/tra_insert.php") ){
    			switch(writeState){
    			case WRITE_LOGIN_SUCCESS:
        			view.loadUrl("javascript:date_r_.value=" + "2011-08-18" );
        			view.loadUrl("javascript:item.value=" + items.get(0) );
        			view.loadUrl("javascript:money.value=" + 100 );
        			view.loadUrl("javascript:insert.submit( check_insert('', document.getElementsByName('insert')[0] ) )");
        			writeState = WRITE_WRITING;
        			break;
    			case WRITE_WRITING:
    				writeState = WRITE_SUCCESS;
    			}
    			sendSuccess();
    		}
    		else{
    			Log.e("boenit", "fail : " + url);
    			view.destroy();
    			sendFail("������ ��");
    			writeState = WRITE_FAIL;
    		}
    	}
    }
	
	final class JSInterfaceICash {
		
		public void checkLoginResult(final String html){
			Log.i("beonit", "checkLogin : " + html);
			if( html.contains("��Ÿ����") ){
				writeState = WRITE_LOGIN_SUCCESS;
				mWebView.loadUrl("http://m.icashhouse.co.kr/tra_insert.php");
			}
			else{
				writeState = WRITE_LOGIN_FAIL;
			}
			return;
		}
	}
	
	///////////////////////////////////////////////////////////////////////////////////
	// �̰����� ���� ���п� ���� notify, ��������� �Ѵ�.
	// notify �� ��� �� ����� ����Ʈ���� ���� ������ �پ��� �� �ֱ� ������ �� ����Ʈ Ư���� �Ļ���Ų Ŭ�������� �� �־�� �Ѵ�.
    ///////////////////////////////////////////////////////////////////////////////////
	
	private boolean isResultNoti = true;
	public void setResultNoti( boolean noti ){
		this.isResultNoti = noti;
	}

	public void sendFail(String cause) {
		super.sendFail(); 
		// ����� notify �Ѵ�.
		if( isResultNoti ){
			// result notify  
			Context context = mWebView.getContext();
			Notification notification = new Notification(R.drawable.icon, "����� �Է� ����", 0);
			notification.flags |= Notification.FLAG_AUTO_CANCEL;
			Intent failIntent = new Intent(context, ViewMain.class);
			failIntent.putExtra("goto", 2);
			PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, failIntent, 0);
			notification.setLatestEventInfo(context, "icashhouse ��볻�� ���� ����", cause, pendingIntent);
			NotificationManager nm = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
			nm.notify(ViewMain.NOTI_ID, notification);
		}
	}

	private void sendSuccess() {
		// ����� notify �Ѵ�.
		if( isResultNoti ){
			Context context = mWebView.getContext();
	    	Notification notification = new Notification(R.drawable.icon, "icashhouse�� �Է� �Ϸ�", 0);
	    	notification.flags |= Notification.FLAG_AUTO_CANCEL;
	    	Intent successIntent = new Intent();
	    	PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, successIntent, 0);
			Log.v("beonit", "write item : " + items.get(0) );
	    	notification.setLatestEventInfo(context, "��� �Ϸ�", items.get(0), pendingIntent);
			NotificationManager nm = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
	    	nm.notify(ViewMain.NOTI_ID, notification);
		}
	}
}
