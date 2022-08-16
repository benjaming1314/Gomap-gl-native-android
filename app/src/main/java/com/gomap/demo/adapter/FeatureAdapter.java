package com.gomap.demo.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.gomap.demo.R;
import com.gomap.demo.model.Feature;

import java.util.List;


/**
 * Adapter used for FeatureOverviewActivity.
 * <p>
 * Adapts a Feature to a visual representation to be shown in a RecyclerView.
 * </p>
 */
public class FeatureAdapter extends RecyclerView.Adapter<FeatureAdapter.ViewHolder> {

  private List<Feature> features;

  public static class ViewHolder extends RecyclerView.ViewHolder {

    public TextView labelView;
    public TextView descriptionView;

    public ViewHolder(View view) {
      super(view);
      labelView = (TextView) view.findViewById(R.id.nameView);
      descriptionView = (TextView) view.findViewById(R.id.descriptionView);
    }
  }

  public FeatureAdapter(List<Feature> features) {
    this.features = features;
  }

  @Override
  public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_main_feature, parent, false);
    return new ViewHolder(view);
  }

  @Override
  public void onBindViewHolder(ViewHolder holder, int position) {
    holder.labelView.setText(features.get(position).getLabel());
    holder.descriptionView.setText(features.get(position).getDescription());
  }

  @Override
  public int getItemCount() {
    return features.size();
  }
}
