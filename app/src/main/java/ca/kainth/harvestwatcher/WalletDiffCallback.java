package ca.kainth.harvestwatcher;

import android.support.annotation.Nullable;
import android.support.v7.util.DiffUtil;

import java.util.List;

import ca.kainth.harvestwatcher.db.Wallet;


public class WalletDiffCallback extends DiffUtil.Callback {
    private final List<Wallet> oldWalletList;
    private final List<Wallet> newWalletList;

    public WalletDiffCallback(List<Wallet> oldWalletList, List<Wallet> newWalletList) {
        this.oldWalletList = oldWalletList;
        this.newWalletList = newWalletList;
    }

    @Override
    public int getOldListSize() {
        return oldWalletList.size();
    }

    @Override
    public int getNewListSize() {
        return newWalletList.size();
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        return oldWalletList.get(oldItemPosition).getId()
                == newWalletList.get(newItemPosition).getId();
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        final Wallet oldWallet = oldWalletList.get(oldItemPosition);
        final Wallet newWallet = newWalletList.get(newItemPosition);

        boolean isSame = true;
        // check if name is the same
        if (!oldWallet.getName().equals(newWallet.getName())) {
            isSame = false;
        }
        if (!oldWallet.getAddress().equals(newWallet.getAddress())) {
            isSame = false;
        }
        if (oldWallet.getBalance() != newWallet.getBalance()) {
            isSame = false;
        }

        return isSame;
    }

    @Nullable
    @Override
    public Object getChangePayload(int oldItemPosition, int newItemPosition) {
        return super.getChangePayload(oldItemPosition, newItemPosition);
    }
}
