package beonit.NaverMoneySync;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class NaverViewClient extends WebViewClient {
	/**
	 * 
	 */
	private String id;
	private String passwd;
	private ProgressDialog mProgressDialog = null;
	
	public NaverViewClient(String id, String passwd) {
		this.id = id;
		this.passwd = passwd;
	}
	
	public void onPageStarted(WebView view, String url, Bitmap favicon){
		if( view.willNotDraw() && mProgressDialog == null )
			try{
				mProgressDialog = ProgressDialog.show(view.getContext(), "����� �ε�", "�α��� ������ �ε�", false);
				mProgressDialog.setCancelable(true);
			}catch(Exception e){
				Log.e("beonit", "dialog error");
				e.printStackTrace();
			}
	}
	
	
	public void onPageFinished(WebView view, String url){
		Log.i("beonit", url);
		if( id == null || passwd == null || id.length() == 0 || passwd.length() == 0 )
			return;
		if( url.equals("https://nid.naver.com/nidlogin.login?svctype=262144&url=http://beta.moneybook.naver.com/m/view.nhn?method=monthly")){
			view.loadUrl("javascript:id.value='"+ id +"'");
			view.loadUrl("javascript:pw.value='"+ passwd +"'");
			view.loadUrl("javascript:loginform.submit()");
			MyJavaScriptInterface iJS = new MyJavaScriptInterface();
			view.addJavascriptInterface(iJS, "HTMLOUT");
			if( mProgressDialog != null )
				mProgressDialog.setMessage("�α��� �õ�");
		}
		else if( url.equals("https://nid.naver.com/nidlogin.login?svctype=262144") ){
			view.loadUrl("javascript:window.HTMLOUT.showHTML('' + document.body.getElementsByTagName('span')[3].innerHTML);");
			if( mProgressDialog != null )
				mProgressDialog.setMessage("�α��� ó��");
			view.setWillNotDraw(false);
		}else if( url.contains("http://static.nid.naver.com/login/sso/finalize.nhn") ){
			if( mProgressDialog != null )
				mProgressDialog.setMessage("����� �ε� ��");
		}else if( url.equals("http://beta.moneybook.naver.com/m/view.nhn?method=monthly") ){
			closeDialog();
		}else if( url.equals("http://beta.moneybook.naver.com/m/mbookUser.nhn")){
			closeDialog();
    		AlertDialog.Builder alert = new AlertDialog.Builder(view.getContext());
			alert.setTitle( "����� ���� �ȵ�" );
			alert.setMessage( "���� ���� �ݰ� ����� ��/PC �� ���� ������Ǹ� ó���ϰ� ������ �ּ���." );
			alert.setPositiveButton(
					 "�ݱ�", new DialogInterface.OnClickListener() {
					    public void onClick( DialogInterface dialog, int which) {
					        dialog.dismiss();   //�ݱ�
					    }
					});
			alert.show();
		}else{
			closeDialog();
		}
	}
	
	public void closeDialog(){
		if( mProgressDialog != null ){
			mProgressDialog.dismiss();
			mProgressDialog = null;
		}
	}
	
	final class MyJavaScriptInterface {
	    public void showHTML(String html) {
	        if( html.contains("����") ){
	        	mProgressDialog.dismiss();
	        	mProgressDialog = null;
	        }
	    }  
	}  
}