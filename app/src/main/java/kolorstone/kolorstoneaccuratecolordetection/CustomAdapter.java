package kolorstone.kolorstoneaccuratecolordetection;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;


public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.ViewHolder> {

    private Context mContext;
    private ArrayList<Item> mList;

    public CustomAdapter(Context context, ArrayList<Item> list) {
        mContext = context;
        mList = list;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Item currentItem = mList.get(position);

        String rgb = currentItem.getRgb();
        String hex = currentItem.getHex();
        String hsl = currentItem.getHsl();
        String cmyk = currentItem.getCmyk();


        int color[]=new int[3];
        String colors[]=rgb.split(",");
        color[0]=Integer.parseInt(colors[0]);
        color[1]=Integer.parseInt(colors[1]);
        color[2]=Integer.parseInt(colors[2]);

        Log.i("RGB", ""+color[1]);



        //holder.card.setBackgroundColor(Color.parseColor("#ccc")); //set cclor

        holder.color.setBackgroundColor(Color.rgb(color[0],color[1],color[2]));
        Log.i("HEX", hex);

        holder.rgb.setText("RGB: "+rgb);
        holder.hex.setText("HEX: "+hex);
        holder.hsl.setText("HSL: "+hsl);
        holder.cmyk.setText("CMYK: "+cmyk);



    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView color;
        public TextView rgb, hex, hsl, cmyk;


        public ViewHolder(View itemView) {
            super(itemView);

            color = itemView.findViewById(R.id.image_view);
            rgb = itemView.findViewById(R.id.rgb);
            hex = itemView.findViewById(R.id.hex);
            hsl = itemView.findViewById(R.id.hsl);
            cmyk = itemView.findViewById(R.id.cmyk);

        }
    }
}
