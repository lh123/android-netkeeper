package com.lh.exin.message;

public class MessageStatus
{
	public static boolean isLogin=false;
	public static final int LOGIN_ING=1;
	public static final int LOGIN_FAIL=2;
	public static final int LOGIN_SUCCESS=0;
	public static final int GET_WIFI_INFO_SUCCESS=3;
	public static final int WAN_SUCCESS=4;
	public static final int GET_WAN_HTML_SUCCESS=5;
	public static final int WANT_GET_WANINFO=6;
	public static final int WANT_LOGIN=7;
	public static final int WANT_GET_HTML=8;
	public static final int NEED_LOGIN=9;
	public static final int CONNECT_TIMEOUT=10;
	public static final int CONNECT_FAIL=11;
	public static final int NEED_UPDATE=12;
	public static final int CONFIRM_UPDATE=13;
	public static final int DOWN_SUCCESS=14;
	public static final int DOWN_FAILED=15;
}
