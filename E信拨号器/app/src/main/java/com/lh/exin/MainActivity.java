package com.lh.exin;

import android.app.*;
import android.content.*;
import android.os.*;
import android.support.v4.app.*;
import android.support.v7.app.*;
import android.view.*;
import android.widget.*;
import android.widget.AdapterView.*;
import com.lh.exin.activity.*;
import com.lh.exin.listdata.*;
import com.lh.exin.toolbar.*;
import com.umeng.analytics.*;
import com.umeng.update.*;

import android.app.AlertDialog;
import android.support.v4.app.Fragment;
import com.lh.exin.control.*;
import com.lh.exin.routdata.*;

public class MainActivity extends AppCompatActivity implements OnItemClickListener
{
	private ListView listview;
	private ToolbarControl toolbar;
	private RoutControl routControl;
	private Intent i;
    @Override
    public void onCreate(Bundle savedInstanceState)
	{
        super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		MobclickAgent.updateOnlineConfig(this);
		listview = (ListView) findViewById(R.id.drawerlistView);
		listinit();
		toolbar = new ToolbarControl(this);
		toolbar.initToolbar();
		listview.setOnItemClickListener(this);
		initUpdate();
		UmengUpdateAgent.update(this);
	}

	@Override
	public void onItemClick(AdapterView<?> p1, View p2, int p3, long p4)
	{
		switch (p3)
		{
			case 0:
				i=new Intent();
				i.setClass(MainActivity.this, AdvanceFunctionActivity.class);
				startActivity(i);
				toolbar.closeDrawer();
				break;
			case 1:
				showRestartConfirmDialog();
				toolbar.closeDrawer();
				break;
			case 2:
				i=new Intent();
				i.setClass(MainActivity.this, AtyDrivesList.class);
				startActivity(i);
				toolbar.closeDrawer();
				break;
			case 3:
				UmengUpdateAgent.forceUpdate(getApplicationContext());
				break;
			case 4:
				i=new Intent();
				i.setClass(MainActivity.this, About.class);
				startActivity(i);
				toolbar.closeDrawer();
				break;
		}
	}
	public void listinit()
	{
		DrawerListAdapter adapter=new DrawerListAdapter(MainActivity.this);
		listview.setAdapter(adapter);
	}
	
	long last=-1,current=-1;

	@Override
	public void onBackPressed()
	{
		if(toolbar.drawerStatus()==true)
		{
			toolbar.closeDrawer();
			return;
		}
		if (last < 0)
		{
			last = System.currentTimeMillis();
			current = last;
			Toast.makeText(MainActivity.this, "再按一次退出", Toast.LENGTH_SHORT).show();
		}
		else
		{
			current = System.currentTimeMillis();
			if (current - last < 2000)
			{
				finish();
			}
			else
			{
				last = current;
				Toast.makeText(MainActivity.this, "再按一次退出", Toast.LENGTH_SHORT).show();
			}
		}
	}

	public void initUpdate()
	{
		UmengUpdateAgent.setUpdateListener(new UmengUpdateListener(){

				@Override
				public void onUpdateReturned(int p1, UpdateResponse p2)
				{
					if(p1==UpdateStatus.No)
						Toast.makeText(getApplication(),"已是最新版",Toast.LENGTH_SHORT).show();
					else if(p1==UpdateStatus.Yes)
						Toast.makeText(getApplication(),"发现新版本",Toast.LENGTH_SHORT).show();
					else if(p1==UpdateStatus.NoneWifi)
						Toast.makeText(getApplication(),"没有连接到wifi",Toast.LENGTH_SHORT).show();
				}
			});
	}
	
	public void showRestartConfirmDialog()
	{
		routControl=RoutData.routControl;
		AlertDialog.Builder build=new AlertDialog.Builder(this);
		build.setTitle("提示");
		build.setMessage("确认重启?");
		build.setPositiveButton("重启", new DialogInterface.OnClickListener(){

				@Override
				public void onClick(DialogInterface p1, int p2)
				{
					routControl.restartRout();
				}
			});
		build.setNegativeButton("取消",null).show();
	}
	
	@Override
	protected void onPause()
	{
		super.onPause();
		MobclickAgent.onPause(this);
	}

	@Override
	protected void onResume()
	{
		super.onResume();
		MobclickAgent.onResume(this);
	}
	
}
