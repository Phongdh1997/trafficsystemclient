package com.hcmut.admin.bktrafficsystem.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProviders;
import androidx.paging.PagedList;
import androidx.paging.PagedListAdapter;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.snackbar.Snackbar;
import com.hcmut.admin.bktrafficsystem.R;
import com.hcmut.admin.bktrafficsystem.databinding.VoucherListItemBinding;
import com.hcmut.admin.bktrafficsystem.model.Voucher;
import java.text.DecimalFormat;

public class VoucherAdapter extends PagedListAdapter<Voucher, VoucherAdapter.VoucherViewHolder> {
    private Context mContext;
    public static Voucher voucher;
//    private AddFavoriteViewModel addFavoriteViewModel;
//    private RemoveFavoriteViewModel removeFavoriteViewModel;
//    private ToCartViewModel toCartViewModel;
//    private FromCartViewModel fromCartViewModel;
//    private ToHistoryViewModel toHistoryViewModel;

    // Create a final private MovieAdapterOnClickHandler called mClickHandler
    private VoucherAdapterOnClickHandler clickHandler;


    /**
     * The interface that receives onClick messages.
     */
    public interface VoucherAdapterOnClickHandler {
        void onClick(Voucher voucher);
    }

    public VoucherAdapter(Context mContext) {
        super(DIFF_CALLBACK);
        this.mContext = mContext;
//        this.clickHandler = clickHandler;
//        addFavoriteViewModel = ViewModelProviders.of((FragmentActivity) mContext).get(AddFavoriteViewModel.class);
//        removeFavoriteViewModel = ViewModelProviders.of((FragmentActivity) mContext).get(RemoveFavoriteViewModel.class);
//        toCartViewModel = ViewModelProviders.of((FragmentActivity) mContext).get(ToCartViewModel.class);
//        fromCartViewModel = ViewModelProviders.of((FragmentActivity) mContext).get(FromCartViewModel.class);
//        toHistoryViewModel = ViewModelProviders.of((FragmentActivity) mContext).get(ToHistoryViewModel.class);
    }

    @NonNull
    @Override
    public VoucherViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        VoucherListItemBinding  voucherListItemBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.voucher_list_item, parent, false);
        return new VoucherViewHolder(voucherListItemBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull VoucherViewHolder holder, int position) {
        holder.setIsRecyclable(false);
        voucher = getItem(position);

        if (voucher != null) {
            String voucherName = voucher.getVoucherName();
            holder.binding.txtVoucherName.setText(voucherName);

            DecimalFormat formatter = new DecimalFormat("#,###,###");
            String formattedPrice = formatter.format(voucher.getVoucherValue());
            holder.binding.txtVoucherValue.setText(formattedPrice + " điểm");

            // Load the Product image into ImageView
            String imageUrl =voucher.getVoucherImage().replaceAll("\\\\", "/");
            Glide.with(mContext)
                    .load(imageUrl)
                    .into(holder.binding.imgVoucherImage);

            Log.d("imageUrl", imageUrl);

//            holder.binding.imgShare.setOnClickListener(v -> shareProduct(mContext, productName, imageUrl));

            // If product is inserted
//            if (product.isFavourite() == 1) {
//                holder.binding.imgFavourite.setImageResource(R.drawable.ic_favorite_pink);
//            }
//
//            // If product is added to cart
//            if (product.isInCart() == 1) {
//                holder.binding.imgCart.setImageResource(R.drawable.ic_shopping_cart_green);
//            }

        } else {
            Toast.makeText(mContext, "Voucher is null", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public PagedList<Voucher> getCurrentList() {
        return super.getCurrentList();
    }

    public Voucher getProductAt(int position) {
        return getItem(position);
    }

    public void notifyOnInsertedItem(int position) {
        notifyItemInserted(position);
        notifyItemRangeInserted(position, getCurrentList().size()-1);
        notifyDataSetChanged();
    }

    // It determine if two list objects are the same or not
    private static DiffUtil.ItemCallback<Voucher> DIFF_CALLBACK = new DiffUtil.ItemCallback<Voucher>() {
        @Override
        public boolean areItemsTheSame(@NonNull Voucher oldProduct, @NonNull Voucher newProduct) {
            return oldProduct.getVoucherName().equals(newProduct.getVoucherName());
        }

        @SuppressLint("DiffUtilEquals")
        @Override
        public boolean areContentsTheSame(@NonNull Voucher oldProduct, @NonNull Voucher newProduct) {
            return oldProduct.equals(newProduct);
        }
    };

    class VoucherViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        // Create view instances
        private final VoucherListItemBinding binding;

        private VoucherViewHolder(VoucherListItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            // Register a callback to be invoked when this view is clicked.
            itemView.setOnClickListener(this);
//            binding.imgFavourite.setOnClickListener(this);
//            binding.imgCart.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            // Get position of the product
            voucher = getItem(position);

//            switch (v.getId()) {
//                case R.id.card_view:
//                    // Send product through click
//                    clickHandler.onClick(voucher);
//                    insertProductToHistory();
//                    break;
//                case R.id.imgFavourite:
//                    toggleFavourite();
//                    break;
//                case R.id.imgCart:
//                    toggleProductsInCart();
//                    break;
//            }
        }

//        private void toggleFavourite() {
//            // If favorite is not bookmarked
//            if (product.isFavourite() != 1) {
//                binding.imgFavourite.setImageResource(R.drawable.ic_favorite_pink);
//                insertFavoriteProduct(() -> {
//                    product.setIsFavourite(true);
//                    notifyDataSetChanged();
//                });
//                showSnackBar("Bookmark Added");
//            } else {
//                binding.imgFavourite.setImageResource(R.drawable.ic_favorite_border);
//                deleteFavoriteProduct(() -> {
//                    product.setIsFavourite(false);
//                    notifyDataSetChanged();
//                });
//                showSnackBar("Bookmark Removed");
//            }
//        }

//        private void toggleProductsInCart() {
//            // If Product is not added to cart
//            if (product.isInCart() != 1) {
//                binding.imgCart.setImageResource(R.drawable.ic_shopping_cart_green);
//                insertToCart(() -> {
//                    product.setIsInCart(true);
//                    notifyDataSetChanged();
//                });
//                showSnackBar("Added To Cart");
//            } else {
//                binding.imgCart.setImageResource(R.drawable.ic_add_shopping_cart);
//                deleteFromCart(() -> {
//                    product.setIsInCart(false);
//                    notifyDataSetChanged();
//                });
//                showSnackBar("Removed From Cart");
//            }
//        }

//        private void showSnackBar(String text) {
//            Snackbar.make(itemView, text, Snackbar.LENGTH_SHORT).show();
//        }
//
//        private void insertFavoriteProduct(RequestCallback callback) {
//            Favorite favorite = new Favorite(LoginUtils.getInstance(mContext).getUserInfo().getId(), product.getProductId());
//            addFavoriteViewModel.addFavorite(favorite,callback);
//        }
//
//        private void deleteFavoriteProduct(RequestCallback callback) {
//            removeFavoriteViewModel.removeFavorite(LoginUtils.getInstance(mContext).getUserInfo().getId(), product.getProductId(),callback);
//        }
//
//        private void insertToCart(RequestCallback callback) {
//            Cart cart = new Cart(LoginUtils.getInstance(mContext).getUserInfo().getId(), product.getProductId());
//            toCartViewModel.addToCart(cart, callback);
//        }
//
//        private void deleteFromCart(RequestCallback callback) {
//            fromCartViewModel.removeFromCart(LoginUtils.getInstance(mContext).getUserInfo().getId(), product.getProductId(),callback);
//        }
//
//        private void insertProductToHistory() {
//            History history = new History(LoginUtils.getInstance(mContext).getUserInfo().getId(), product.getProductId());
//            toHistoryViewModel.addToHistory(history);
//        }
    }
}
