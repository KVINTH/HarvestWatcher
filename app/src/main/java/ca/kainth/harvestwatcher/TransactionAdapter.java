package ca.kainth.harvestwatcher;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import ca.kainth.harvestwatcher.db.Transaction;

public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.TransactionViewHolder> {

    private List<Transaction> transactions;

    class TransactionViewHolder extends RecyclerView.ViewHolder {
        TextView tvTransactionId, tvTimeStamp, tvTransactionValue;

        TransactionViewHolder(View view) {
            super(view);
            tvTransactionId = view.findViewById(R.id.tvTransactionId);
            tvTimeStamp = view.findViewById(R.id.tvTimeStamp);
            tvTransactionValue = view.findViewById(R.id.tvTransactionValue);
        }
    }

    TransactionAdapter(List<Transaction> transactions) {
        this.transactions = transactions;
    }

    public void setAdapterItems(List<Transaction> transactions) {
        this.transactions = transactions;
    }

    @Override
    public TransactionViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.transaction_list_row, parent, false);
        final TransactionViewHolder mViewHolder = new TransactionViewHolder(itemView);
     return mViewHolder;
    }

    @Override
    public void onBindViewHolder(TransactionViewHolder holder, int position) {
        Transaction transaction = transactions.get(position);
        holder.tvTransactionId.setText(transaction.getTransactionId().substring(0, 5));
        //holder.tvTimeStamp.setText(String.valueOf(transaction.getUnixTimeStamp()));
        holder.tvTransactionValue.setText(String.valueOf(transaction.getValue()));
    }

    @Override
    public int getItemCount() {
        return transactions.size();
    }
}
