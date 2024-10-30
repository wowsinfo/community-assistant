package com.half.wowsca.ui.encyclopedia.tabs;

import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.util.Pair;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.half.wowsca.CAApp;
import com.half.wowsca.R;
import com.half.wowsca.alerts.Alert;
import com.half.wowsca.model.encyclopedia.holders.ExteriorHolder;
import com.half.wowsca.model.encyclopedia.items.ExteriorItem;
import com.half.wowsca.model.FlagClickedEvent;
import com.half.wowsca.ui.CAFragment;
import com.half.wowsca.ui.adapter.FlagsAdapter;

import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

/**
 * Created by slai4 on 4/25/2016.
 */
public class FlagsFragment extends CAFragment {

    RecyclerView recyclerView;
    GridLayoutManager layoutManager;
    FlagsAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.view_recycler_view, container, false);
        recyclerView = (RecyclerView) view;
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        CAApp.getEventBus().register(this);
        ExteriorHolder holder = CAApp.getInfoManager().getExteriorItems(getContext());
        if (holder != null && holder.getItems() != null && recyclerView.getAdapter() == null) {
            layoutManager = new GridLayoutManager(getContext(), getResources().getInteger(R.integer.shipopedia_upgrade_grid));
            layoutManager.setOrientation(GridLayoutManager.VERTICAL);
            recyclerView.setLayoutManager(layoutManager);

            List<ExteriorItem> flags = new ArrayList<>();
            flags.addAll(holder.getItems().values());

            Collections.sort(flags, new Comparator<ExteriorItem>() {
                @Override
                public int compare(ExteriorItem lhs, ExteriorItem rhs) {
                    return lhs.getName().compareToIgnoreCase(rhs.getName());
                }
            });
            Collections.sort(flags, new Comparator<ExteriorItem>() {
                @Override
                public int compare(ExteriorItem lhs, ExteriorItem rhs) {
                    return rhs.type.compareToIgnoreCase(lhs.type);
                }
            });

            adapter = new FlagsAdapter(flags, getContext());
            recyclerView.setAdapter(adapter);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        CAApp.getEventBus().unregister(this);
    }

    @Subscribe
    public void flagClickedEvent(FlagClickedEvent event) {
        ExteriorHolder holder = CAApp.getInfoManager().getExteriorItems(getContext());
        ExteriorItem item = holder.get(event.id);
        if (item != null) {
            StringBuilder sb = new StringBuilder();
            sb.append(item.description);
            if (item.coef != null && !item.coef.isEmpty()) {
                sb.append("\n\n");
                Iterator<String> iter = item.coef.keySet().iterator();
                while (iter.hasNext()) {
                    String key = iter.next();
                    Pair<String, Float> pair = item.coef.get(key);
                    sb.append(pair.first);
                    if (iter.hasNext()) {
                        sb.append("\n");
                    }
                }
            }
            Drawable d = ContextCompat.getDrawable(getContext(), R.drawable.ic_flags);
            if (!CAApp.isDarkTheme(getContext()))
                d.setColorFilter(ContextCompat.getColor(getContext(), R.color.top_background), PorterDuff.Mode.MULTIPLY);
            Alert.createGeneralAlert(getActivity(), item.getName(), sb.toString(), getString(R.string.dismiss), d);
        } else {
            Toast.makeText(getContext(), R.string.resources_error, Toast.LENGTH_SHORT).show();
        }

    }
}