package com.service.serveigopartner;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class ItemListDialogFragment extends BottomSheetDialogFragment {

    EditText editText;
    Button buttonCancel,butonSave;
    String str;
    private ItemClickListener mListener;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View v = getLayoutInflater().inflate(R.layout.fragment_item_list_dialog, container, false);
        butonSave=v.findViewById(R.id.buttonSave);
        buttonCancel=v.findViewById(R.id.buttonCancel);
        editText=v.findViewById(R.id.editTextDetailedit);

        Bundle bundle = getArguments();
        String editTextData = bundle.getString("data","");

        editText.setText(editTextData);

        butonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                str=editText.getText().toString();
                mListener= (ItemClickListener) getContext();
                mListener.onItemClick(str);
                dismiss();
            }
        });
        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
        return v;
    }

    @SuppressLint("RestrictedApi")
    @Override
    public void setupDialog(final Dialog dialog, int style) {
        super.setupDialog(dialog, style);
        View contentView = View.inflate(getContext(), R.layout.fragment_item_list_dialog, null);
        dialog.setContentView(contentView);
    }

    public interface ItemClickListener {
        void onItemClick(String item);
    }

}
