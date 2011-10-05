package beonit.NaverMoneySync;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.content.Context;
import android.util.Log;

public class QuickWriterIcash extends QuickWriter implements IQuickWriter {
	
	public QuickWriterIcash(String id, String passwd, Context context){
		super(id, passwd);
	}
	
	public boolean quickWrite(String itemsStr){
		// https://www.icashhouse.co.kr:50103/api_android/insert.php
		// GET or POST
		// mb_id : ����� ���̵�
		// mb_password : ����� ��й�ȣ
		// date : ��¥. ex) 2011-12-01
		// item : ǰ�� Ȥ�� �ŷ�ó. ex) ���(����ļ�����)
		// money : �ݾ�. ex) 21000
		// l_acc_type : ������ ����. ex) e
		// l_acc_id : ������ �׸� ������ȣ. ex) 917773
		// r_acc_type : �뺯�� ����. ex) a
		// r_acc_id : �뺯�� �׸� ������ȣ. ex) 827711
		StringBuilder uri = new StringBuilder();
    	String itemStr = null;
		try {
			itemStr = URLEncoder.encode("�ѱ��� �� ��������?" , "UTF-8");
		} catch (UnsupportedEncodingException e) {
			Log.e("beonit", "item str encode fail");
			e.printStackTrace();
			return false;
		}
		uri = uri.append("https://www.icashhouse.co.kr:50103/api_android/insert.php").append("?mb_id=beonit").append("&mb_password=akdma59")
					.append("&date=2011-9-30").append("&item=").append( itemStr )
					.append("&money=1,000")
					.append("&l_acc_type=e").append("&l_acc_id=917773")
					.append("&r_acc_type=e").append("&r_acc_id=827711");
		InputStream in = null;
		try {
			in = executeHttpGet( uri.toString() );
		} catch (Exception e) {
			e.printStackTrace();
			Log.e("beonit", "executeHttpGet fail");
			return false;
		}
		if( in == null ){
			Log.e("beonit", "input stream is null");
			return false;
		}
		InputStreamReader isr = new InputStreamReader(in);
	    BufferedReader br = new BufferedReader(isr);
	    String s;
	    try {
			while ((s = br.readLine()) != null) {
				Log.i("beonit", "result : " + s);
			}
			isr.close();
		} catch (IOException e) {
			Log.e("beonit", "input stream is null");
			e.printStackTrace();
		}

        return true;
	}
	
	public InputStream executeHttpGet(String url) throws Exception {
		Log.i("beonit", "request url : " + url );
		InputStream content = null;
		try {
			HttpClient httpclient = new DefaultHttpClient();
			HttpResponse response = httpclient.execute(new HttpGet(url));
			content = response.getEntity().getContent();
		} catch (Exception e) {
			Log.i("beonit", "http request fail");
			e.printStackTrace();
		}
		return content;
		
	}
	
	///////////////////////////////////////////////////////////////////////////////////
	// �̰����� ���� ���п� ���� notify, ��������� �Ѵ�.
	// notify �� ��� �� ����� ����Ʈ���� ���� ������ �پ��� �� �ֱ� ������ �� ����Ʈ Ư���� �Ļ���Ų Ŭ�������� �� �־�� �Ѵ�.
    ///////////////////////////////////////////////////////////////////////////////////
	
	@Override
	public void stop() {
		
	}
}
