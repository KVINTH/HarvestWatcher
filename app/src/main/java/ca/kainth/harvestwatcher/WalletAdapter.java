package ca.kainth.harvestwatcher;

import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import ca.kainth.harvestwatcher.db.Wallet;

public class WalletAdapter extends RecyclerView.Adapter<WalletAdapter.WalletViewHolder> {

    private List<Wallet> walletList;

    class WalletViewHolder extends RecyclerView.ViewHolder {
        TextView tvWalletAlias, tvWalletAddress, tvWalletBalance;

        WalletViewHolder(View view) {
            super(view);
            tvWalletAlias = view.findViewById(R.id.tvWalletAlias);
            tvWalletAddress = view.findViewById(R.id.tvWalletAddress);
            tvWalletBalance = view.findViewById(R.id.tvWalletBalance);
        }
    }

    WalletAdapter(List<Wallet> walletList)
    {
        this.walletList = walletList;
    }

    public void setAdapterItems(List<Wallet> walletList) {
        this.walletList = walletList;
    }

    @Override
    public WalletViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.wallet_list_row, parent, false);

        return new WalletViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(WalletViewHolder holder, int position) {
        Wallet wallet = walletList.get(position);
        holder.tvWalletAlias.setText(wallet.getName());
        holder.tvWalletAddress.setText(wallet.getAddress());
        holder.tvWalletBalance.setText(String.valueOf(wallet.getBalance()));
    }

    @Override
    public int getItemCount() {
        return walletList.size();
    }

    public void updateWalletListItems(List<Wallet> walletList) {
        final WalletDiffCallback walletDiffCallback = new WalletDiffCallback(this.walletList, walletList);
        final DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(walletDiffCallback);

        this.walletList.clear();
        this.walletList.addAll(walletList);
        diffResult.dispatchUpdatesTo(this);
    }
}
