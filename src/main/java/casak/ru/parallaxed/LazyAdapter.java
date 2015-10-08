package casak.ru.parallaxed;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class LazyAdapter extends BaseAdapter {


    private static LayoutInflater inflater = null;
    private Frame[] data = null;

    public LazyAdapter(Context context, Frame[] data){
        this.data = data;
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
        /*View vi=convertView;
        if(convertView==null)
            vi = inflater.inflate(R.layout.frame, parent, false);

        TextView title = (TextView)vi.findViewById(R.id.title);
        TextView artist = (TextView)vi.findViewById(R.id.artist);
        TextView views = (TextView)vi.findViewById(R.id.views);
        ImageView image = (ImageView)vi.findViewById(R.id.image);

        title.setText(data[position].getTitle());
        artist.setText(data[position].getName());
        views.setText("" + data[position].getViews());

        image.setImageBitmap(data[position].getImage());
        return vi;*/


        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.frame, parent, false);

            viewHolder = new ViewHolder();
            viewHolder.title = (TextView) convertView.findViewById(R.id.title);
            viewHolder.artist = (TextView) convertView.findViewById(R.id.artist);
            viewHolder.views = (TextView) convertView.findViewById(R.id.views);
            viewHolder.itemView = convertView;
            viewHolder.setBackgroundImage((ParallaxImageView) convertView.findViewById(R.id.parallaxImageView));
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.getBackgroundImage().setImageBitmap(data[position].getImage());
        viewHolder.title.setText(data[position].getTitle());
        viewHolder.artist.setText(data[position].getName());
        viewHolder.views.setText("" + data[position].getViews());

        // # CAUTION:
        // Important to call this method
        viewHolder.getBackgroundImage().reuse();

        return convertView;
    }

    static class ViewHolder extends ParallaxViewHolder {

        private TextView title;
        private TextView artist;
        private TextView views;

    }
}
