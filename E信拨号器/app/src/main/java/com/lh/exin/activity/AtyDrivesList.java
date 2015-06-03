package com.lh.exin.activity;
import android.app.*;
import android.os.*;
import com.lh.exin.control.*;
import com.lh.exin.routdata.*;
import com.lh.exin.*;
import android.widget.*;
import com.lh.exin.listdata.*;
import java.util.*;
import com.lh.exin.toolbar.*;
import android.support.v7.app.*;

public class AtyDrivesList extends AppCompatActivity
{
	private RoutControl routControl;
	private ArrayList<DrivesInfo> array;
	private ListView list;
	private ProgressDialog pd;
	private DrivesListAdapter adapter;
	private ToolbarControl toolbar;
	private Handler handler=new Handler(){

		@Override
		public void handleMessage(Message msg)
		{
			switch(msg.what)
			{
				case 1:
					pd=new ProgressDialog(AtyDrivesList.this);
					pd.setTitle("正在处理");
					pd.setMessage("正在获取设备列表");
					pd.show();
					break;
				case 2:
					setAdapter((ArrayList<DrivesInfo>)msg.obj);;
					pd.cancel();
					break;
			}
		}
		
	};
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_driveslist);
		toolbar=new ToolbarControl(this);
		toolbar.initToolbarNoDrawer("设备列表");
		routControl=RoutData.routControl;
		list=(ListView) findViewById(R.id.drivesList);
		getArray();
		
	}
	public void getArray()
	{
		handler.sendEmptyMessage(1);
		new Thread(new Runnable(){

				@Override
				public void run()
				{
					array=routControl.getDrivesList();
					handler.sendMessage(handler.obtainMessage(2,array));
				}
			}).start();
	}
	public void setAdapter(ArrayList<DrivesInfo> array)
	{
		adapter=new DrivesListAdapter(this,array);
		list.setAdapter(adapter);
	}
}
