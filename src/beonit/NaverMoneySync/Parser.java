package beonit.NaverMoneySync;

import java.util.StringTokenizer;

public class Parser {
	public static String Parse(String sms) throws Exception{
		/*
		 * [KBī��] �̽��Ѵ� 8*9*ī�� 03��06��01:01 3000�� �Ѹ��̸���Ʈ ��ź ���
		 * �Ｚī�� 03/06 14:26 11���� 39,320�� �Ͻúһ�� �����մϴ�
    	 * http://moneybook.naver.com/m/write.nhn?method=quick
    	 * �Ʒ� �������� �����Ӱ� �������� ����ϼ���.
    	 * ��¥ v ��볻�� v ī�� or ���� v �ݾ� (v=����)
    	 * ������ �Է� �� ; �����ݷ����� ���� �մϴ�.
    	 * ī�� ���� SMS�� �����Ͽ� �ٿ� �Է� �� �� �־��.
    	 * ��) 06/05 ��� 1,500��; ��� 2,800��
    	 */
		return new Parser(sms).toString();
	}
	
	public String cardCompany;
	public String date;
	public String store;
	public String money;
	
	public String toString(){
		// ��¥ v ��볻�� v ī�� or ���� v �ݾ� (v=����)
		return date + " " + store + " " + cardCompany + " " + money;
	}
	
	public Parser(String sms) throws Exception{
		if( sms == null )
			return;
		StringTokenizer tokens = new StringTokenizer(sms);
		if( !tokens.hasMoreTokens() )
			throw new Exception("sms content parse fail");
		cardCompany = tokens.nextToken();	// [KBī��], �Ｚī��
		if( cardCompany.equals("�Ｚī��"))
			parseSamsung(tokens);
		else if( cardCompany.equals("[KBī��]") ){
			cardCompany = "KBī��";
			parseKB(tokens);
		}
		else{
			throw new Exception("not supported card type");
		}
	}

	private void parseSamsung(StringTokenizer tokens) throws Exception {
//		 �Ｚī�� 03/06 14:26 11���� 39,320�� �Ͻúһ�� �����մϴ�
		if( !tokens.hasMoreTokens() )
			throw new Exception("sms content parse fail");
		date = tokens.nextToken();
		if( !tokens.hasMoreTokens() )
			throw new Exception("sms content parse fail");
		tokens.nextToken();
		if( !tokens.hasMoreTokens() )
			throw new Exception("sms content parse fail");
		store = tokens.nextToken();
		if( !tokens.hasMoreTokens() )
			throw new Exception("sms content parse fail");
		money = tokens.nextToken();
	}

	private void parseKB(StringTokenizer tokens) throws Exception {
//		[KBī��] �̽��Ѵ� 8*9*ī�� 03��06��01:01 3000�� �Ѹ��̸���Ʈ ��ź ���
		if( !tokens.hasMoreTokens() )
			throw new Exception("sms content parse fail");
		tokens.nextToken();
		if( !tokens.hasMoreTokens() )
			throw new Exception("sms content parse fail");
		tokens.nextToken();
		if( !tokens.hasMoreTokens() )
			throw new Exception("sms content parse fail");
		date = tokens.nextToken();  //03��06��01:01
		date = date.substring(0, 6);
		if( !tokens.hasMoreTokens() )
			throw new Exception("sms content parse fail");
		money = tokens.nextToken();
		store = tokens.nextToken();
		while( tokens.hasMoreTokens() )
			store += " " + tokens.nextToken();
	}
}
