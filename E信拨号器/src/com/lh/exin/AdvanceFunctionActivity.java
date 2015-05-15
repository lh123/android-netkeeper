package com.lh.exin;
import android.app.*;
import android.os.*;
import android.view.*;
import android.view.View.*;
import android.widget.*;
import com.lh.exin.account.*;
import com.lh.exin.message.*;
import com.lh.exin.routdata.*;

public class AdvanceFunctionActivity extends Activity
{
	private EditText edStartHour,edStrartMin,edEndHour,edEndMin;
	private Button btnRestart,btnSet;
	private AccountLogin login;
	private MessageHandler handler;
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		getActionBar().hide();
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_advance);
		btnSet=(Button) findViewById(R.id.adv_button_save);
		btnRestart=(Button) findViewById(R.id.adv_button_restart);
		edStartHour=(EditText) findViewById(R.id.adv_in_hour_start);
		edStrartMin=(EditText) findViewById(R.id.adv_in_minute_start);
		edEndHour=(EditText) findViewById(R.id.adv_in_hour_end);
		edEndMin=(EditText) findViewById(R.id.adv_in_minute_end);
		btnSet.setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View p1)
				{
					readTime();
					//login = new AccountLogin(RoutInfo.exRelAccount, RoutInfo.exPassword, handler);
					login.setRoutInfo(RoutInfo.rAccount, RoutInfo.rPassword, RoutInfo.rIp);
					login.setLoginTime(RoutInfo.startHour,RoutInfo.startMin,RoutInfo.endHour,RoutInfo.endMin);
					//login.loginAtTime();
				}
			});
	}
	public void readTime()
	{
		RoutInfo.startHour=edStartHour.getText().toString();
		RoutInfo.startMin=edStrartMin.getText().toString();
		RoutInfo.endHour=edEndHour.getText().toString();
		RoutInfo.endMin=edEndMin.getText().toString();
	}
}
