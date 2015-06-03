package com.lh.exin.listdata;
import android.widget.*;
import android.view.*;
import android.content.*;
import com.lh.exin.*;
import java.util.*;

public class DrawerListAdapter extends BaseAdapter
{
	private Context context;
	private String[] sarray;
	private ArrayList<ItemData> array=new ArrayList<ItemData>();

	public DrawerListAdapter(Context context)
	{
		this.context = context;
		sarray=context.getResources().getStringArray(R.array.drawer);
		array.add(new ItemData(R.drawable.time,sarray[0]));
		array.add(new ItemData(R.drawable.advance,sarray[1]));
		array.add(new ItemData(R.drawable.list,sarray[2]));
		array.add(new ItemData(R.drawable.update,sarray[3]));
		array.add(new ItemData(R.drawable.phone_documents_about_support,sarray[4]));
	}
	@Override
	public int getCount()
	{
		return array.size();
	}

	@Override
	public ItemData getItem(int p1)
	{
		// TODO: Implement this method
		return array.get(p1);
	}

	@Override
	public long getItemId(int p1)
	{
		// TODO: Implement this method
		return p1;
	}

	@Override
	public View getView(int p1, View p2, ViewGroup p3)
	{
		if(p2==null)
		{
			p2=LayoutInflater.from(context).inflate(R.layout.listview_cell,p3,false);
		}
		ImageView i=(ImageView) p2.findViewById(R.id.listviewcellImageView);
		TextView t=(TextView) p2.findViewById(R.id.listviewcellText);
		i.setImageResource(getItem(p1).imageId);
		t.setText(getItem(p1).text);
		return p2;
	}
	
}
