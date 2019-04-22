package com.ttsbco.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.akexorcist.roundcornerprogressbar.RoundCornerProgressBar;
import com.ttsbco.models.PowerMeterJsonModel;
import com.ttsbco.pmmonitoring.ActivityHistory;
import com.ttsbco.pmmonitoring.ActivityMain;
import com.ttsbco.pmmonitoring.ActivitySettings;
import com.ttsbco.pmmonitoring.R;

import java.util.ArrayList;

public class PowerMeterAdapter extends RecyclerView.Adapter<PowerMeterAdapter.ViewHolder>
{
    Context context;
    ArrayList<PowerMeterJsonModel> list;

    public PowerMeterAdapter(Context context, ArrayList<PowerMeterJsonModel> list)
    {
        this.context = context;
        this.list = list;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View v;
        if (viewType == 1/*power meter layout*/)
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_power_meter, parent, false);
        else // register layout
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_register, parent, false);

        return new ViewHolder(v, viewType, context);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position)
    {
        final PowerMeterJsonModel model = list.get(position);

        if (model.isPowerMeter())
        {
            if (!model.hasError())
                holder.txtError.setVisibility(View.GONE);
            else
            {
                holder.txtError.setVisibility(View.VISIBLE);
                holder.txtError.setText(model.getMessage());
            }

            holder.txtPowerMeterName.setText(model.getName());
        }
        else
        {
            holder.btnShowHistory.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    Intent intent = new Intent(context, ActivityHistory.class);
                    Bundle bundle = new Bundle();
                    bundle.putInt("id", model.getId());
                    bundle.putInt("address", model.getAddress());
                    bundle.putString("title", "سوابق " + model.getPowerMeterName() + " » پارامتر " + model.getName());
                    intent.putExtras(bundle);
                    context.startActivity(intent);
                }
            });
            holder.txtRegisterName.setText(model.getName());
            holder.txtRegisterValue.setText(String.valueOf(model.getVal()));
            holder.register_progress.setMax((float) model.getMax());
            holder.register_progress.setProgress((float) model.getVal());

            if (model.getVal() > model.getMax() || model.getVal() < model.getMin())
                holder.register_progress.setProgressColor(Color.rgb(0xB3, 0x08, 0x08));
            else
                holder.register_progress.setProgressColor(Color.rgb(0x7A, 0xA4, 0x10));
        }
    }

    @Override
    public int getItemViewType(int position)
    {
        return list.get(position).isPowerMeter() ? 1 : 0;
    }

    @Override
    public int getItemCount()
    {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder
    {
        ImageButton btnShowHistory;
        Context context;
        TextView txtPowerMeterName, txtError, txtRegisterName, txtRegisterValue;
        RoundCornerProgressBar register_progress;

        public ViewHolder(View itemView, int ViewType, Context c)
        {
            super(itemView);
            this.context = c;
            this.txtPowerMeterName = (TextView) itemView.findViewById(R.id.txtPowerMeterName);
            this.txtRegisterName = (TextView) itemView.findViewById(R.id.txtRegisterName);
            this.txtRegisterValue = (TextView) itemView.findViewById(R.id.txtValue);
            this.txtError = (TextView) itemView.findViewById(R.id.txtError);
            this.register_progress = (RoundCornerProgressBar) itemView.findViewById(R.id.register_progress);
            this.btnShowHistory = (ImageButton) itemView.findViewById(R.id.btnViewHistory);
        }
    }
}
