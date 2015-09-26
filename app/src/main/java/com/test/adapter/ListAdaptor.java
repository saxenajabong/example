package com.test.adapter;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.test.R;
import com.test.volley.ListModel;
import com.test.volley.VolleyTest;

import java.util.ArrayList;
import java.util.List;

public class ListAdaptor<T extends ListModel> extends BaseAdapter implements Filterable {

    private Context context;
    private List<T> results;
    private List<T> objects;
    private ImageLoader loader;

    public ListAdaptor(Context context, List<T> results) {
        this.context = context;
        this.results = results;
        objects = results;
        loader = VolleyTest.getInstance(context).getImageLoader();
    }

    @Override
    public int getCount() {
        return results.size();
    }

    @Override
    public ListModel getItem(int position) {
        return results.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.list_item, null, false);
            ViewHolder holder = new ViewHolder();
            holder.title = (TextView) view.findViewById(R.id.list_item_text);
            holder.price = (TextView) view.findViewById(R.id.list_item_price);
            holder.resultImage = (NetworkImageView) view.findViewById(R.id.list_item_image);
            holder.delete = (ImageView) view.findViewById(R.id.list_item_delete);
            view.setTag(holder);
        }
        ListModel item = getItem(position);
        ViewHolder holder = (ViewHolder) view.getTag();
        holder.title.setText(Html.fromHtml(item.getDescription()));
        holder.price.setText(Html.fromHtml(item.getPrice()));
        holder.resultImage.setImageUrl(item.getImage(), loader);
        holder.delete.setOnClickListener(listener);
        holder.delete.setTag(position);
        return view;
    }

    /**
     * Listener for delete button.
     */
    private View.OnClickListener listener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            objects.remove((int) v.getTag());
            results = objects;
            notifyDataSetChanged();
        }
    };

    @Override
    public Filter getFilter() {
        return new ListFilter();
    }

    private static class ViewHolder {
        TextView title;
        NetworkImageView resultImage;
        ImageView delete;
        TextView price;
    }

    private final Object mLock = new Object();

    /**
     * <p>A List filter constrains the content of the list adapter with
     * a prefix. Each item that does not start with the supplied prefix
     * is removed from the list.</p>
     */
    private class ListFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence prefix) {
            FilterResults results1 = new FilterResults();

            if (prefix == null || prefix.length() == 0) {
                ArrayList<T> list;
                synchronized (mLock) {
                    list = new ArrayList<T>(objects);
                }
                results1.values = list;
                results1.count = list.size();
            } else {
                String prefixString = prefix.toString().toLowerCase();

                ArrayList<T> values;
                synchronized (mLock) {
                    values = new ArrayList<T>(objects);
                }

                final int count = values.size();
                final ArrayList<T> newValues = new ArrayList<T>();

                for (int i = 0; i < count; i++) {
                    final T value = values.get(i);
                    final String valueText = value.getDescription().toLowerCase();

                    // First match against the whole, non-splitted value
                    if (valueText.startsWith(prefixString)) {
                        newValues.add(value);
                    } else {
                        final String[] words = valueText.split(" ");
                        final int wordCount = words.length;

                        // Start at index 0, in case valueText starts with space(s)
                        for (int k = 0; k < wordCount; k++) {
                            if (words[k].startsWith(prefixString)) {
                                newValues.add(value);
                                break;
                            }
                        }
                    }
                }

                results1.values = newValues;
                results1.count = newValues.size();
            }

            return results1;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results1) {
            //noinspection unchecked
            results = (List<T>) results1.values;
            if (results1.count > 0) {
                notifyDataSetChanged();
            } else {
                notifyDataSetInvalidated();
            }
        }
    }
}
