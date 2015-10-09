package com.byoutline.kickmaterial.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import com.byoutline.kickmaterial.R;
import com.byoutline.kickmaterial.fragments.KickMaterialFragment;
import com.byoutline.kickmaterial.fragments.ProjectsListFragment;
import com.byoutline.secretsauce.fragments.MenuOption;
import com.byoutline.secretsauce.views.CheckableCustomFontTextView;

public class MenuAdapter extends ArrayAdapter<MenuOption> {

    private final LayoutInflater inflater;

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public int getItemViewType(int position) {
        return 0;
    }

    public MenuAdapter(Context context) {
        super(context, android.R.layout.simple_list_item_1);
        inflater = LayoutInflater.from(getContext());

        //here we define drawer menu
        add(new MenuOption(context.getString(R.string.title_section1), ProjectsListFragment.class));
        add(new MenuOption(context.getString(R.string.title_section2), KickMaterialFragment.class));
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.menu_list_item, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        MenuOption item = getItem(position);
        holder.menuItemNameTv.setText(item.toString());

        return convertView;
    }

    public static class ViewHolder {
        public CheckableCustomFontTextView menuItemNameTv;
        public View root;

        public ViewHolder(View root) {
            menuItemNameTv = (CheckableCustomFontTextView) root.findViewById(R.id.menu_item_name_tv);
            this.root = root;
        }
    }
}
