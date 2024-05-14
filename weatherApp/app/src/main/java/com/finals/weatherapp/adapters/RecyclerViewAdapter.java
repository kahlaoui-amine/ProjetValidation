package com.finals.weatherapp.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.finals.weatherapp.models.Forecast;
import com.finals.weatherapp.R;

import java.util.List;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.RecyclerViewVH> {
    List<Forecast> forecastList;

    public RecyclerViewAdapter(List<Forecast> forecastList) {
        this.forecastList = forecastList;
    }

    public List<Forecast> getForecastList() {
        return forecastList;
    }

    @NonNull
    @Override
    public RecyclerViewVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_view_forecast, parent, false);
        return new RecyclerViewVH(view);
    }

    // Sets the value on the RecyclerView
    @Override
    public void onBindViewHolder(@NonNull RecyclerViewVH holder, int position) {
        Forecast forecast = forecastList.get(position);
        holder.rvDate.setText(forecast.getDate());
        holder.rvDescription.setText(forecast.getDescription());
        holder.rvMinMax.setText(String.format("Min | Max\n%s", forecast.getMinMax()));
        holder.rvFeelsLike.setText(String.format("Feels Like\n%s", forecast.getFeelsLike()));
        holder.rvHumidity.setText(String.format("Humidity\n%s", forecast.getHumidity()));
        holder.rvWindSpeed.setText(String.format("Wind Speed\n%s", forecast.getWindSpeed()));
        holder.rvSunrise.setText(String.format("Sunrise\n%s", forecast.getSunrise()));
        holder.rvSunset.setText(String.format("Sunset\n%s", forecast.getSunset()));
        holder.rvIcon.setImageResource(forecast.getIcon());
        boolean isExpanded = forecastList.get(position).isExpanded();
        holder.rvMore.setRotation(isExpanded ? 180 : 0);
        holder.expandableLayout.setVisibility(isExpanded ? View.VISIBLE : View.GONE);
    }

    @Override
    public int getItemCount() {
        return forecastList.size();
    }

    public class RecyclerViewVH extends RecyclerView.ViewHolder {
        TextView rvDate, rvDescription, rvMinMax, rvFeelsLike, rvHumidity, rvWindSpeed, rvSunrise, rvSunset;
        ImageView rvIcon, rvMore;
        LinearLayout hiddenLayout;
        GridLayout expandableLayout;

        public RecyclerViewVH(@NonNull View itemView) {
            super(itemView);
            rvDate = itemView.findViewById(R.id.rvDate);
            rvDescription = itemView.findViewById(R.id.rvDescription);
            rvMinMax = itemView.findViewById(R.id.rvMinMax);
            rvFeelsLike = itemView.findViewById(R.id.rvFeelsLike);
            rvHumidity = itemView.findViewById(R.id.rvHumidity);
            rvWindSpeed = itemView.findViewById(R.id.rvWindSpeed);
            rvSunrise = itemView.findViewById(R.id.rvSunrise);
            rvSunset = itemView.findViewById(R.id.rvSunset);
            rvIcon = itemView.findViewById(R.id.rvIcon);
            rvMore = itemView.findViewById(R.id.rvMore);
            hiddenLayout = itemView.findViewById(R.id.hiddenLayout);
            expandableLayout = itemView.findViewById(R.id.expandableLayout);

            // Hides expandableLayout when clicked
            hiddenLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Forecast forecast = forecastList.get(getAdapterPosition());
                    forecast.setExpanded(!forecast.isExpanded());
                    notifyItemChanged(getAdapterPosition());
                }
            });
        }
    }
}
