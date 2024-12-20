package com.example.uptodo.adapter;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.uptodo.R;
import com.example.uptodo.dao.CategoryDAO;
import com.example.uptodo.model.Category;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class CategoryAdapter extends BaseAdapter {
    private final List<Category> categoryList;
    private final CategoryDAO categoryDAO;
    private final Context context;

    public CategoryAdapter(Context context, List<Category> categoryList, CategoryDAO categoryDAO) {
        this.context = context;
        this.categoryList = categoryList;
        this.categoryDAO = categoryDAO;
    }

    @Override
    public int getCount() {
        return categoryList.size();
    }

    @Override
    public Object getItem(int position) {
        return categoryList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return categoryList.get(position).getCategoryId();  // Đảm bảo trả về long
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_category, parent, false);
        }

        ImageView iconView = convertView.findViewById(R.id.categoryIcon);
        TextView nameView = convertView.findViewById(R.id.categoryName);
        ImageView deleteIcon = convertView.findViewById(R.id.deleteIcon);
        ImageView editIcon = convertView.findViewById(R.id.editIcon);

        Category category = categoryList.get(position);

        // Kiểm tra và lấy icon từ drawable bằng tên icon lưu trong cơ sở dữ liệu
        if (category.getIcon() != null && !category.getIcon().isEmpty()) {
            int iconResId = context.getResources().getIdentifier(category.getIcon(), "drawable", context.getPackageName());

            if (iconResId != 0) {  // Kiểm tra nếu icon tồn tại trong drawable
                iconView.setImageResource(iconResId);
            } else {
                // Nếu không tìm thấy, sử dụng placeholder
                iconView.setImageResource(R.drawable.ic_block);
            }
        } else {
            // Nếu không có icon, sử dụng placeholder
            iconView.setImageResource(R.drawable.ic_block);
        }

        // Cập nhật tên danh mục với màu sắc
        nameView.setText(category.getName());

        // Kiểm tra nếu có màu sắc, nếu không thì dùng màu mặc định
        if (category.getColor() != null && !category.getColor().isEmpty()) {
            try {
                nameView.setTextColor(Color.parseColor(category.getColor())); // Áp dụng màu cho tên
            } catch (IllegalArgumentException e) {
                nameView.setTextColor(Color.BLACK); // Nếu không hợp lệ, dùng màu đen
            }
        } else {
            nameView.setTextColor(Color.BLACK); // Màu mặc định nếu không có giá trị màu
        }
        // Xử lý sự kiện nhấn vào nút xóa
        deleteIcon.setOnClickListener(v -> {
            removeCategory(position);
        });

        // Xử lý sự kiện nhấn vào nút sửa
        editIcon.setOnClickListener(v -> {
            showEditCategoryDialog(category, position);
        });

        return convertView;
    }

    // Xử lý xóa danh mục
    private void removeCategory(int position) {
        new android.app.AlertDialog.Builder(context)
                .setTitle("Xóa danh mục")
                .setMessage("Bạn có chắc chắn muốn xóa danh mục này?")
                .setPositiveButton("Xóa", (dialog, which) -> {
                    Category category = categoryList.get(position);
                    int result = categoryDAO.deleteCategory(category.getCategoryId());

                    if (result > 0) {
                        categoryList.remove(position);
                        notifyDataSetChanged();
                        Toast.makeText(context, "Danh mục đã được xóa", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(context, "Xóa không thành công", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Hủy", (dialog, which) -> {
                    dialog.dismiss();
                })
                .show();
    }

    //Xử lý sửa danh mục
    private void showEditCategoryDialog(Category category, int position) {
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_add_category);

        // Ánh xạ các phần tử trong layout
        ImageView closeButton = dialog.findViewById(R.id.cancelButton);
        TextView createText = dialog.findViewById(R.id.createText);
        EditText editCategoryName = dialog.findViewById(R.id.editCategoryName);

        LinearLayout colorSelectionLayout = dialog.findViewById(R.id.colorSelectionLayout);
        ImageView color1 = dialog.findViewById(R.id.color1);
        ImageView color2 = dialog.findViewById(R.id.color2);
        ImageView color3 = dialog.findViewById(R.id.color3);
        ImageView color4 = dialog.findViewById(R.id.color4);
        ImageView color5 = dialog.findViewById(R.id.color5);

        LinearLayout iconSelectionLayout = dialog.findViewById(R.id.iconSelectionLayout);
        ImageView icon1 = dialog.findViewById(R.id.icon1);
        ImageView icon2 = dialog.findViewById(R.id.icon2);
        ImageView icon3 = dialog.findViewById(R.id.icon3);
        ImageView icon4 = dialog.findViewById(R.id.icon4);
        ImageView icon5 = dialog.findViewById(R.id.icon5);

        FloatingActionButton fabSaveCategory = dialog.findViewById(R.id.fabSave);

        // Mặc định là màu và icon của danh mục đã chọn
        final String[] selectedColor = {category.getColor() != null ? category.getColor() : "#FFFFFF"};
        final String[] selectedIcon = {category.getIcon() != null ? category.getIcon() : "baseline_home_24"};

        // Điền sẵn tên danh mục vào EditText
        editCategoryName.setText(category.getName());

        // Sự kiện cho các ô màu sắc
        color1.setOnClickListener(v -> selectedColor[0] = "#FF0000"); // Màu đỏ
        color2.setOnClickListener(v -> selectedColor[0] = "#33B5E5"); // Màu xanh
        color3.setOnClickListener(v -> selectedColor[0] = "#99CC00"); // Màu xanh lá
        color4.setOnClickListener(v -> selectedColor[0] = "FFBB33"); // Màu cam
        color5.setOnClickListener(v -> selectedColor[0] = "#AA66CC"); // Màu tím

        // Sự kiện cho các biểu tượng
        icon1.setOnClickListener(v -> selectedIcon[0] = "baseline_home_24");
        icon2.setOnClickListener(v -> selectedIcon[0] = "ic_work");
        icon3.setOnClickListener(v -> selectedIcon[0] = "ic_personal");
        icon4.setOnClickListener(v -> selectedIcon[0] = "ic_cart");
        icon5.setOnClickListener(v -> selectedIcon[0] = "ic_question");

        // Khi nhấn nút lưu, thực hiện cập nhật danh mục
        fabSaveCategory.setOnClickListener(view -> {
            String categoryName = editCategoryName.getText().toString();
            if (categoryName.isEmpty()) {
                Toast.makeText(context, "Vui lòng nhập tên danh mục", Toast.LENGTH_SHORT).show();
                return;
            }

            // Cập nhật đối tượng Category
            category.setName(categoryName);
            category.setColor(selectedColor[0]);
            category.setIcon(selectedIcon[0]);

            // Cập nhật vào cơ sở dữ liệu
            int result = categoryDAO.updateCategory(category);

            if (result > 0) {
                Toast.makeText(context, "Danh mục đã được cập nhật", Toast.LENGTH_SHORT).show();
                categoryList.set(position, category);  // Cập nhật danh mục trong danh sách
                notifyDataSetChanged();  // Cập nhật giao diện
            } else {
                Toast.makeText(context, "Cập nhật không thành công", Toast.LENGTH_SHORT).show();
            }

            dialog.dismiss();
        });

        // Khi nhấn nút đóng, đóng dialog
        closeButton.setOnClickListener(view -> dialog.dismiss());

        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.getWindow().setGravity(Gravity.BOTTOM);
    }

}
