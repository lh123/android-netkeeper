package com.lh.exin.activity;

import android.os.*;
import android.support.v4.app.*;
import android.text.*;
import android.view.*;
import android.view.View.*;
import android.widget.*;
import com.lh.exin.control.*;
import com.lh.exin.routinfospace.*;
import com.rengwuxian.materialedittext.*;
import com.rengwuxian.materialedittext.validation.*;
import com.umeng.analytics.*;
import android.graphics.*;

public class MainFragment extends Fragment
{
	private View view;
    private MaterialEditText edExAccount,edExPassword,edRoutAccount,edRoutPassword,edRoutIp;
	private Button btnSet,btnInfo;
	private TextView tvStatus;
	private CheckBox cbSave;
	private RoutControl routControl; 
	private RoutInfoSpace routInfo;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		view = inflater.inflate(R.layout.main_fragment, container, false);
		init();
		return view;
	}
	public void init()
	{
		edExAccount =  (MaterialEditText) view.findViewById(R.id.ex_account);
		edExPassword = (MaterialEditText) view.findViewById(R.id.ex_password);
		edRoutAccount = (MaterialEditText) view.findViewById(R.id.rout_account);
		edRoutPassword =(MaterialEditText) view.findViewById(R.id.rout_password);
		edRoutIp = (MaterialEditText) view.findViewById(R.id.rout_ip);
		btnSet = (Button) view.findViewById(R.id.but_set);
		btnInfo = (Button) view.findViewById(R.id.but_info);
		tvStatus = (TextView) view.findViewById(R.id.tv_status);
		cbSave = (CheckBox) view.findViewById(R.id.cb_save);
		routInfo = new RoutInfoSpace(cbSave, edExAccount, edExPassword, edRoutAccount, edRoutPassword, edRoutIp, getActivity());
		routInfo.readInfo();
		routControl = new RoutControl(getActivity(), edExAccount, edExPassword, edRoutAccount, edRoutPassword, edRoutIp, tvStatus);
		routControl.getStatus();
		btnSet.setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View p1)
				{
					routControl.login();
					routInfo.saveInfo();
				}
			});
		btnInfo.setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View p1)
				{
					long current=System.currentTimeMillis();
					routControl.getWanInfo();
					MobclickAgent.onEventDuration(getActivity(),"获取WAN信息",System.currentTimeMillis()-current);
				}
			});
	}
}
