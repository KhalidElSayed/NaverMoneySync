package beonit.NaverMoneySync;

import java.io.InputStream;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.content.Context;

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
		uri = uri.append("?mb_id=beonit").append("&mb_password=akdma59")
					.append("&date=2011-12-01").append("&item=").append("itemsStr")
					.append("&money=1000").append("&l_acc_type=")
					.append("&l_acc_id=").append("&r_acc_type=")
					.append("&r_acc_id=");
		try {
			executeHttpGet(uri.toString());
		} catch (Exception e) {
			e.printStackTrace();
			writeState = WRITE_FAIL;
			return false;
		}
        return true;
	}
	
	public InputStream executeHttpGet(String url) throws Exception {
		InputStream content = null;
		try {
			// TODO. encode uri
			HttpClient httpclient = new DefaultHttpClient();
			HttpResponse response = httpclient.execute(new HttpGet(url));
			content = response.getEntity().getContent();
		} catch (Exception e) {
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
