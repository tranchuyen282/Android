package foodguide.md06.vtca.foodguide.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.makeramen.roundedimageview.RoundedTransformationBuilder;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import java.util.ArrayList;

import foodguide.md06.vtca.foodguide.R;
import foodguide.md06.vtca.foodguide.model.Recipes;
import foodguide.md06.vtca.foodguide.model.Restaurant;

/**
 * Created by PDNghiaDev on 5/12/2015.
 */
public class MoreRestaurantAdapter extends BaseAdapter {

    private LayoutInflater inflater;
    private Context context;
    private ArrayList<Restaurant> lvdata;
    private Restaurant mRestaurant;

    public MoreRestaurantAdapter(Context context, ArrayList<Restaurant> lvdata) {
        this.context = context;
        this.lvdata = lvdata;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return lvdata.size();
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
            convertView = inflater.inflate(R.layout.sample_item, null);
            viewHolder.imvItem = (ImageView) convertView.findViewById(R.id.image_sample);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        mRestaurant = lvdata.get(position);

        Transformation transformation = new RoundedTransformationBuilder()
                .borderColor(Color.WHITE)
                .borderWidthDp(3)
                .cornerRadiusDp(40)
                .oval(false)
                .build();

        Picasso.with(context)
                .load(mRestaurant.getImage())
                .fit()
                .transform(transformation)
                .into(viewHolder.imvItem);

        return convertView;
    }

    private class ViewHolder {
        private ImageView imvItem;

    }
}
