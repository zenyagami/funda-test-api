package com.zenkun.fundatest.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.zenkun.fundatest.R;
import com.zenkun.fundatest.model.ModelHouse;

import java.util.ArrayList;
import java.util.Locale;

/**
 * Created by Zen zenyagami@gmail.com on 24/03/2017.
 */

public class AdapterTopTenMakelar extends RecyclerView.Adapter<AdapterTopTenMakelar.Viewholder> {

    private ArrayList<ModelHouse> dataset;

    public AdapterTopTenMakelar(ArrayList<ModelHouse> dataset) {
        this.dataset = dataset;
    }
    public void changeDataset(ArrayList<ModelHouse> newDataset)
    {
        //change of dataSet
        this.dataset=newDataset;
        notifyDataSetChanged();
    }

    @Override
    public Viewholder onCreateViewHolder(ViewGroup parent, int viewType) {
        return  new Viewholder(LayoutInflater.from(parent.getContext()).inflate(R.layout.row_makelar_list,parent,false));
    }

    @Override
    public void onBindViewHolder(Viewholder holder, int position) {
        holder.bind(dataset.get(position));
    }

    @Override
    public int getItemCount() {
        return dataset==null?0 :dataset.size();
    }

    public class Viewholder extends RecyclerView.ViewHolder {
        private TextView makelarId;
        public Viewholder(View itemView) {
            super(itemView);
            makelarId = (TextView) itemView.findViewById(R.id.adapter_makelar_id);
        }

        public void bind(ModelHouse modelHouse) {
            makelarId.setText(String.format(Locale.getDefault(),makelarId.getContext().getString(R.string.makelar_row_desc),
                    modelHouse.makelarId,
                    modelHouse.makelarName,
                    modelHouse.amountOfSales));
        }
    }
}
