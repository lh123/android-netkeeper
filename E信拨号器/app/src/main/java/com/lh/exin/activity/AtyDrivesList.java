package com.lh.exin.activity;
import android.os.*;
import android.support.v4.widget.*;
import android.support.v7.app.*;
import android.widget.*;
import com.lh.exin.*;
import com.lh.exin.control.*;
import com.lh.exin.listdata.*;
import com.lh.exin.routdata.*;
import com.lh.exin.toolbar.*;
import java.util.*;
import android.view.*;
import com.nineoldandroids.animation.*;
import android.view.animation.*;

public class AtyDrivesList extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener
{
	private RoutControl routControl;
	private SwipeRefreshLayout swipelayout;
	private ListView list;
	private DrivesListAdapter adapter;
	private LayoutAnimationController lac;
	private ToolbarControl toolbar;
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_driveslist);
		swipelayout=(SwipeRefreshLayout) findViewById(R.id.swipe_refresh);
		swipelayout.setColorSchemeResources(R.color.dark_blue);
		swipelayout.setOnRefreshListener(this);
		toolbar=new ToolbarControl(this);
		toolbar.initToolbarNoDrawer("设备列表");
		routControl=RoutData.routControl;
		list=(ListView) findViewById(R.id.drivesList);
		Animation aa=AnimationUtils.loadAnimation(this,android.R.anim.slide_in_left);
		//aa.setRepeatMode(Animation.REVERSE);
		lac=new LayoutAnimationController(aa,0.5f);
		list.setLayoutAnimation(lac);
		swipelayout.post(new Runnable(){

				@Override
				public void run()
				{
					swipelayout.setRefreshing(true);
					onRefresh();
				}
			});
	}

	@Override
	public void onRefresh()
	{
		swipelayout.setRefreshing(true);
		getArray();
	}
	
	
	Object[] o;
	
	public void getArray()
	{
		new Thread(new Runnable(){

				@Override
				public void run()
				{
					o=routControl.getDrivesList();
					swipelayout.post(new Runnable(){

							@Override
							public void run()
							{
								if((Boolean)o[0]==false)
								{
									Toast.makeText(getApplicationContext(),"加载失败",Toast.LENGTH_SHORT).show();
								}
								list.startLayoutAnimation();
								setAdapter((ArrayList<DrivesInfo>)o[1]);
								if(o[1]==null)
								{
									findViewById(R.id.empty_view).setVisibility(View.VISIBLE);
								}
								else
								{
									findViewById(R.id.empty_view).setVisibility(View.INVISIBLE);
								}
								swipelayout.setRefreshing(false);
							}
						});
				}
			}).start();
	}
	public void setAdapter(ArrayList<DrivesInfo> array)
	{
		adapter=new DrivesListAdapter(this,array);
		list.setAdapter(adapter);
	}
}
