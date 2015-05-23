package com.lh.exin;



import android.content.*;
import android.os.*;
import android.support.v7.app.*;
import android.view.*;
import android.widget.*;
import android.widget.AdapterView.*;
import com.lh.exin.*;
import com.lh.exin.listdata.*;
import com.lh.exin.toolbar.*;

public class MainActivity extends ActionBarActivity implements OnItemClickListener
{
	private ListView listview;
	private ToolbarControl toolbar;
    @Override
    public void onCreate(Bundle savedInstanceState)
	{
        super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		listview = (ListView) findViewById(R.id.drawerlistView);
		listinit();
		toolbar = new ToolbarControl(this);
		toolbar.initToolbar();
		listview.setOnItemClickListener(this);
	}

	@Override
	public void onItemClick(AdapterView<?> p1, View p2, int p3, long p4)
	{
		switch (p3)
		{
			case 0:
				Intent i=new Intent();
				i.setClass(MainActivity.this, AdvanceFunctionActivity.class);
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
}
