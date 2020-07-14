package foodguide.md06.vtca.foodguide.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import foodguide.md06.vtca.foodguide.R;
import foodguide.md06.vtca.foodguide.model.Categories;

public class CategoryAdapter extends BaseAdapter {

    private LayoutInflater inflater;
    private Context mContext;
    private Categories mCategories;
    private ArrayList<Categories> mList;

    public CategoryAdapter(Context context, ArrayList<Categories> list) {
        this.mContext = context;
        this.mList = list;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = inflater.inflate(R.layout.category_grid_item, null);
            viewHolder.imgCategory = (ImageView) convertView.findViewById(R.id.imageRestaurant);
            viewHolder.tvNameCategory = (TextView) convertView.findViewById(R.id.textNameCategory);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        mCategories = mList.get(position);
        Picasso.with(mContext).load(mCategories.getImage()).into(viewHolder.imgCategory);
        viewHolder.tvNameCategory.setText(mCategories.getName());

        return convertView;
    }


    private class ViewHolder {
        private ImageView imgCategory;
        private TextView tvNameCategory;
    }
}
