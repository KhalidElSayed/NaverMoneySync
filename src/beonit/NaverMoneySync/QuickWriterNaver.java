package beonit.NaverMoneySync;

import android.content.Context;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class QuickWriterNaver extends QuickWriter implements IQuickWriter {

	public QuickWriterNaver(String id, String passwd, Context context){
		super(id, passwd);
		mWebView = new WebView(context);
        mWebView.setWillNotDraw(true);
		mWebView.setWebViewClient(new NaverViewClient());
	}
	
	@Override
	public void stop(){
		if( mWebView != null )
			mWebView.destroy();
	}
	
	@Override
	public boolean quickWrite(String itemsStr){
		items = itemsStr;
		mWebView.setWillNotDraw(true);
		mWebView.loadUrl("https://nid.naver.com/nidlogin.login?svctype=262144&url=http://moneybook.naver.com/m/write.nhn?method=quick");
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.addJavascriptInterface(new JSInterfaceNaver(), "HTMLOUT");
        sendProgressNotify("�α��� �غ�");
        return true;
	}
	
	class NaverViewClient extends WebViewClient{
    	@Override
    	public void onPageFinished(WebView view, String url){
    		Log.i("beonit", url);
    		if( url.equals("https://nid.naver.com/nidlogin.login?svctype=262144&url=http://moneybook.naver.com/m/write.nhn?method=quick")){
    			Log.v("beonit", "page load, login attempt");
    			writeState = WRITE_READY;
    			Log.v("beonit", "login... attempt with id/passwd");
	    		view.loadUrl("javascript:id.value='"+ id +"'");
	    		view.loadUrl("javascript:pw.value='"+ passwd +"'");
	    		view.loadUrl("javascript:loginform.submit()");
	    		sendProgressNotify("�α��� �õ�");
    		}
    		else if( url.equals("https://nid.naver.com/nidlogin.login?svctype=262144") ){
    			writeState = WRITE_LOGIN_ATTEMPT;
    			view.loadUrl("javascript:window.HTMLOUT.showHTML('' + document.body.getElementsByTagName('span')[3].innerHTML);");
    			sendProgressNotify("�α��� ��");
    		}
    		else if( url.contains("http://static.nid.naver.com/login/sso/finalize.nhn") ){
    			Log.v("beonit", "login... success");
    			writeState = WRITE_LOGIN_SUCCESS;
    			sendProgressNotify("�α��� �α��� �Ϸ�");
    		}
    		else if( url.equals("http://moneybook.naver.com/m/write.nhn?method=quick") ){
    			view.loadUrl("javascript:window.HTMLOUT.showHTML(items.value)");
    			view.loadUrl("javascript:items.value='"+ items +"'");
    			view.loadUrl("javascript:window.HTMLOUT.showHTML(items.value)");
	    		view.loadUrl("javascript:writeForm.submit()");
	    		writeState = WRITE_WRITING;
	    		sendProgressNotify("����ο� ����");
    		}
    		else if( url.equals("http://moneybook.naver.com/m/smry.nhn")){
    			Log.v("beonit", "write finish");
    			sendSuccess();
    			view.destroy();
    			view = null;
    			writeState = WRITE_SUCCESS;
    			sendProgressNotify("���� �Ϸ�");
    		}
    		else if( url.equals("http://moneybook.naver.com/m/mbookUser.nhn")){
    			view.destroy();
    			view = null;
    			sendFail("����� ������� �ȵ�");
    			writeState = WRITE_FAIL_REGISTER;
    		}
    		else{
    			Log.e("boenit", "fail : " + url);
    			view.destroy();
    			view = null;
    			sendFail("������ ��");
    			writeState = WRITE_FAIL;
    		}
    	}
    }
	
	final class JSInterfaceNaver {
	    public void showHTML(String html) {
	    	Log.v("beonit", "show html : " + html);
	        if( html.contains("����") ){
	        	writeState = WRITE_LOGIN_FAIL;
	        	sendFail("�α��� ����");
	        }
	    }  
	}
	
}
