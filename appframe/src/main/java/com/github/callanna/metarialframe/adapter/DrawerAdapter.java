package com.github.callanna.metarialframe.adapter;

/**
 * Created by Callanna on 2015/12/21.
 */

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.github.callanna.metarialframe.R;
import com.github.callanna.metarialframe.activity.MainActivity;
import com.github.callanna.metarialframe.util.LogUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

public class DrawerAdapter extends ArrayAdapter<String> {
    private final Context context;
    private final ArrayList<String> values;
    private RelativeLayout l;
    private MainActivity mainActivity;
    Float[] color;
    private SparseBooleanArray myChecked = new SparseBooleanArray();
    HashMap<String,Float[]> colors=new HashMap<String,Float[]>();

    public void toggleChecked(int position) {
        toggleChecked(false);
        myChecked.put(position, true);
        notifyDataSetChanged();
    }

    public void toggleChecked(boolean b) {

        for (int i = 0; i < values.size(); i++) {
            myChecked.put(i, b);
        }
        notifyDataSetChanged();
    }
    void putColor(String x,float a,float b,float c){colors.put(x,new Float[]{a,b,c});}
    void putColors(){
        putColor("#F44336",0.956862f,0.2627450f,0.21176470f);
        putColor("#e91e63",0.91372549f,0.11764706f,0.38823529f);
        putColor("#9c27b0",0.61176471f,0.15294118f,0.69019608f);
        putColor("#673ab7",0.40392157f,0.22745098f,0.71764706f);
        putColor("#3f51b5",0.24705882f,0.31764706f,0.70980392f);
        putColor("#2196F3",0.12941176f,0.58823529f,0.952941176470f);
        putColor("#03A9F4",0.01176470f,0.66274509f,0.9568627450f);
        putColor("#00BCD4",0.0f,0.73725490f,0.831372549f);
        putColor("#009688",0.0f,0.58823529f,0.53333f);
        putColor("#4CAF50",0.298039f,0.68627450f,0.31372549f);
        putColor("#8bc34a",0.54509804f,0.76470588f,0.29019608f);
        putColor("#FFC107",1.0f,0.7568627450f,0.0274509f);
        putColor("#FF9800",1.0f,0.596078f,0.0f);
        putColor("#FF5722",1.0f,0.341176470f,0.1333333f);
        putColor("#795548",0.4745098f,0.3333f,0.28235294f);
        putColor("#212121",0.12941176f,0.12941176f,0.12941176f);
        putColor("#607d8b",0.37647059f,0.49019608f,0.54509804f);
        putColor("#004d40",0.0f, 0.301960f, 0.250980f);

    }
    public DrawerAdapter(Context context, ArrayList<String> values,MainActivity main ) {
        super(context, R.layout.drawerrow, values);
        this.context = context;
        this.values = values;
        mainActivity = main;
        for (int i = 0; i < values.size(); i++) {
            myChecked.put(i, false);
        }
        putColors();
        color=colors.get("#e91e63");
    }
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.drawerrow, parent, false);
        TextView textView = (TextView) rowView.findViewById(R.id.firstline);
        final ImageView imageView = (ImageView) rowView.findViewById(R.id.icon);
        l = (RelativeLayout) rowView.findViewById(R.id.second);
        l.setBackgroundResource(R.drawable.safr_ripple_white);
        l.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mainActivity.selectMenu(position);
                LogUtil.d("duanyl======>mainActivity.selectMenu "+ position  );
            }
        });
        float[] src = {

                color[0], 0, 0, 0, 0,
                0, color[1], 0, 0, 0,
                0, 0,  color[2],0, 0,
                0, 0, 0, 1, 0
        };
        ColorMatrix colorMatrix = new ColorMatrix(src);
        ColorMatrixColorFilter colorMatrixColorFilter = new ColorMatrixColorFilter(colorMatrix);

        textView.setText( values.get(position)) ;

        if(myChecked.get(position)){
            rowView.setBackgroundColor(Color.parseColor("#ffeeeeee"));
            imageView.setColorFilter(colorMatrixColorFilter);
            imageView.setImageResource(R.mipmap.ic_launcher);
            imageView.setColorFilter(colorMatrixColorFilter);
        }
        else
        {
            textView.setTextColor(context.getResources().getColor(android.R.color.black));
            imageView.setImageResource(R.mipmap.ic_launcher);
        }

        return rowView;
    }
}
