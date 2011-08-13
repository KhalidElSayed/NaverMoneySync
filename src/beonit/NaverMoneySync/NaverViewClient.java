package beonit.NaverMoneySync;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
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
			mProgressDialog = ProgressDialog.show(view.getContext(), "����� �ε�", "�α��� ������ �ε�", false);
	}
	
	
	public void onPageFinished(WebView view, String url){
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
			if( mProgressDialog != null ){
				mProgressDialog.dismiss();
				mProgressDialog = null;
			}
		}else{
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