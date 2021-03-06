package spork.android.support.test.bindclick.domain;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import spork.Spork;
import spork.android.BindClick;
import spork.android.BindView;
import spork.android.ViewProvider;
import spork.android.support.test.R;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.TestViewHolder> {
    private final List<String> items;
    private int clickCount = 0;

    public class TestViewHolder extends RecyclerView.ViewHolder implements ViewProvider {

        @BindView(R.id.textview)
        private TextView textView;

        public TestViewHolder(View itemView) {
            super(itemView);
            Spork.bind(this);
        }

        public void update(String text) {
            textView.setText(text);
        }

        @BindClick(R.id.textview)
        private void onClick() {
            clickCount++;
        }

        @Override
        public View getView() {
            return itemView;
        }
    }

    public RecyclerViewAdapter(List<String> items) {
        this.items = items;
    }

    @Override
    public TestViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_recyclerview_item, parent, false);

        return new TestViewHolder(view);
    }

    @Override
    public void onBindViewHolder(TestViewHolder holder, int position) {
        String item = items.get(position);

        holder.update(item);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public int getClickCount() {
        return clickCount;
    }
}

