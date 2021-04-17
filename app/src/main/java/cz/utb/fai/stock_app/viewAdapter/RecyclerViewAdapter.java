package cz.utb.fai.stock_app.viewAdapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import cz.utb.fai.stock_app.models.Stock;
import cz.utb.fai.stock_app.R;
import cz.utb.fai.stock_app.ui.Detail.DetailActivity;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder>{

    private List<Stock> mStocks = new ArrayList<>();
    private Context mContext;

    public RecyclerViewAdapter(Context mContext,List<Stock> mStocks) {
        this.mStocks = mStocks;
        this.mContext = mContext;
    }

    private static final String TAG = "RecyclerViewAdapter";

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_listitems,parent,false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
              holder.ticker.setText(mStocks.get(position).getSymbol());
              holder.price.setText(mStocks.get(position).toStringPrice());
              holder.change.setText( mStocks.get(position).toStringChange());
              holder.changePercentage.setText(mStocks.get(position).getChangePercent());
              holder.parentLayout.setOnClickListener(new View.OnClickListener() {
                  @Override
                  public void onClick(View view) {
                      Intent I = new Intent(mContext, DetailActivity.class);
                      I.putExtra("selected stock",mStocks.get(position));
                      mContext.startActivity(I);
                  }
              });
    }

    @Override
    public int getItemCount() {
        return mStocks.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView ticker,price,change,changePercentage;
        ConstraintLayout parentLayout;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ticker = itemView.findViewById(R.id.textTickerList);
            price = itemView.findViewById(R.id.textPriceList);
            change = itemView.findViewById(R.id.textChangeList);
            changePercentage = itemView.findViewById(R.id.textChangePercentageList);
            parentLayout = itemView.findViewById(R.id.parent_layout);
        }
    }
}
