package casak.ru.parallaxed;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class LazyAdapter extends BaseAdapter {


    private static LayoutInflater inflater = null;
    private Frame[] data = null;
    private Context context = null;
    private Typeface font = null;

    public LazyAdapter(Context context, Frame[] data){
        this.data = data;
        this.context = context;
        inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public int getCount() {
        return data.length;
    }

    public Object getItem(int position) {
        return position;
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.frame, parent, false);

            viewHolder = new ViewHolder();
            viewHolder.title = (TextView) convertView.findViewById(R.id.title);
            viewHolder.artist = (TextView) convertView.findViewById(R.id.artist);
            viewHolder.views = (TextView) convertView.findViewById(R.id.views);
            font = Typeface.createFromAsset(context.getAssets(), "Roboto-Bold.ttf");
            viewHolder.title.setTypeface(font);
            font = Typeface.createFromAsset(context.getAssets(), "Roboto-Light.ttf");
            viewHolder.artist.setTypeface(font);
            font = Typeface.createFromAsset(context.getAssets(), "Roboto-Regular.ttf");
            viewHolder.views.setTypeface(font);


            viewHolder.itemView = convertView;
            viewHolder.setBackgroundImage((ParallaxImageView) convertView.findViewById(R.id.parallaxImageView));
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.getBackgroundImage().setImageBitmap(data[position].getImage());
        viewHolder.title.setText(data[position].getTitle());
        viewHolder.artist.setText(data[position].getName());
        viewHolder.views.setText("Просмотров: " + data[position].getViews());
        viewHolder.getBackgroundImage().reuse();

        return convertView;
    }

    static class ViewHolder extends ParallaxViewHolder {

        private TextView title;
        private TextView artist;
        private TextView views;

    }
}
