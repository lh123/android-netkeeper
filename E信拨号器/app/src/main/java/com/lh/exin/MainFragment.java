package com.lh.exin;

import android.app.*;
import android.os.*;
import android.view.*;
import android.view.View.*;
import android.widget.*;
import com.lh.exin.*;
import com.lh.exin.authority.*;
import com.lh.exin.control.*;
import com.lh.exin.routinfospace.*;

public class MainFragment extends Fragment
{
	private View view;
    private EditText edExAccount,edExPassword,edRoutAccount,edRoutPassword,edRoutIp;
	private Button btnSet,btnInfo;
	private TextView tvStatus,tvTime;
	private CheckBox cbSave;
	private RoutControl routControl; 
	private RoutInfoSpace routInfo;
	private CheckAuthority auth;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		view=inflater.inflate(R.layout.main_fragment,container,false);
		init();
		return view;
	}
	public void init()
	{
		edExAccount = (EditText) view.findViewById(R.id.ex_account);
		edExPassword = (EditText) view.findViewById(R.id.ex_password);
		edRoutAccount = (EditText) view.findViewById(R.id.rout_account);
		edRoutPassword = (EditText) view.findViewById(R.id.rout_password);
		edRoutIp = (EditText) view.findViewById(R.id.rout_ip);
		btnSet = (Button) view.findViewById(R.id.but_set);
		btnInfo = (Button) view.findViewById(R.id.but_info);
		tvStatus = (TextView) view.findViewById(R.id.tv_status);
		tvTime=(TextView) view.findViewById(R.id.active_time);
		cbSave = (CheckBox) view.findViewById(R.id.cb_save);
		routControl = new RoutControl(getActivity(), edExAccount, edExPassword, edRoutAccount, edRoutPassword, edRoutIp, tvStatus,tvTime);
		routInfo = new RoutInfoSpace(cbSave, edExAccount, edExPassword, edRoutAccount, edRoutPassword, edRoutIp, getActivity());
		auth = new CheckAuthority(getActivity());
		routInfo.readInfo();
		routControl.getStatus();
		btnSet.setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View p1)
				{
					if (auth.checkAuthStatus())
					{
						routControl.login();
					}
					routInfo.saveInfo();
				}
			});
		btnInfo.setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View p1)
				{
					if (auth.checkAuthStatus())
					{
						routControl.getWanInfo();
					}
				}
			});
	}
}
