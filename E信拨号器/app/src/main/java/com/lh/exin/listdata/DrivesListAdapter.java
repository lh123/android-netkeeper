package com.lh.exin.listdata;
import android.widget.*;
import android.view.*;
import java.util.*;
import android.content.*;
import com.lh.exin.routdata.*;
import com.lh.exin.*;

public class DrivesListAdapter extends BaseAdapter
{
	private Context context;
	private ArrayList<DrivesInfo> drivesList;

	public DrivesListAdapter(Context context, ArrayList<DrivesInfo> drivesList)
	{
		this.context = context;
		this.drivesList = drivesList;
	}
	@Override
	public int getCount()
	{
		
		return drivesList.size();
	}

	@Override
	public DrivesInfo getItem(int p1)
	{
		return drivesList.get(p1);
	}

	@Override
	public long getItemId(int p1)
	{
		return p1;
	}

	@Override
	public View getView(int p1, View p2, ViewGroup p3)
	{
		if(p2==null)
		{
			p2=LayoutInflater.from(context).inflate(R.layout.single_drives,null);
		}
		TextView drivesName=(TextView) p2.findViewById(R.id.drivesName);
		TextView drivesIp=(TextView) p2.findViewById(R.id.drivesIp);
		drivesName.setText(getItem(p1).drivesName);
		drivesIp.setText(getItem(p1).drivesIp);
		return p2;
	}
	
}
