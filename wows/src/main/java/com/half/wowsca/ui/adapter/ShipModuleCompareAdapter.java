package com.half.wowsca.ui.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.recyclerview.widget.RecyclerView;

import com.half.wowsca.ui.views.ShipModuleView;

import java.util.List;

/**
 * Created by slai4 on 6/29/2017.
 */

public class ShipModuleCompareAdapter extends RecyclerView.Adapter<ShipModuleCompareAdapter.ViewHolder> {

    private final Context ctx;
    private List<Long> ids;

    public ShipModuleCompareAdapter(List<Long> ids, Context ctx) {
        this.ids = ids;
        this.ctx = ctx;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ShipModuleView view = new ShipModuleView(ctx);
        view.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        ViewHolder holder = new ViewHolder(new ShipModuleView(ctx));
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        long id = ids.get(position);
        holder.view.setShipID(id);
        holder.view.initView();
    }

    @Override
    public int getItemCount() {
        try {
            return ids.size();
        } catch (Exception e) {
            return 0;
        }
    }

    public void setIds(List<Long> ids) {
        this.ids = ids;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private final ShipModuleView view;

        public ViewHolder(View itemView) {
            super(itemView);
            view = (ShipModuleView) itemView;
        }
    }
}
