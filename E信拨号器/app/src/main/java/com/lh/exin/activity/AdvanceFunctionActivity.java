package com.lh.exin.activity;

import android.os.*;
import android.support.v7.app.*;
import android.view.*;
import android.view.View.*;
import android.widget.*;
import com.lh.exin.account.*;
import com.lh.exin.message.*;
import com.lh.exin.routdata.*;
import com.lh.exin.toolbar.*;
import com.lh.exin.*;

public class AdvanceFunctionActivity extends AppCompatActivity
{
	private EditText edStartHour,edStrartMin,edEndHour,edEndMin;
	private Button btnSet;
	private Rout login;
	private ToolbarControl toolbar;
	private MessageHandler handler;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_advance);
		init();
	}
	
	public void init()
	{
		toolbar=new ToolbarControl(this);
		toolbar.initToolbarNoDrawer("定时连接");
		btnSet=(Button)findViewById(R.id.adv_button_save);
		edStartHour=(EditText) findViewById(R.id.adv_in_hour_start);
		edStrartMin=(EditText) findViewById(R.id.adv_in_minute_start);
		edEndHour=(EditText) findViewById(R.id.adv_in_hour_end);
		edEndMin=(EditText) findViewById(R.id.adv_in_minute_end);
		btnSet.setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View p1)
				{
					readTime();
				}
			});
	}
	public void readTime()
	{
		RoutData.startHour=edStartHour.getText().toString();
		RoutData.startMin=edStrartMin.getText().toString();
		RoutData.endHour=edEndHour.getText().toString();
		RoutData.endMin=edEndMin.getText().toString();
	}
}
