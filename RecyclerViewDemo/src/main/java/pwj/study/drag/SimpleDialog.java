package pwj.study.drag;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;

import pwj.study.R;

public class SimpleDialog extends DialogFragment {

    private static SimpleDialog sFragment;

    public static SimpleDialog getInstance(String s) {
        if (sFragment == null) {
            sFragment = new SimpleDialog();
        }
        Bundle bundle = new Bundle();
        bundle.putSerializable("content", s);
        sFragment.setArguments(bundle);
        return sFragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        String titleContent = (String) getArguments().get("content");
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_order_two_btn, null);
        TextView title = view.findViewById(R.id.content);
        if (!TextUtils.isEmpty(titleContent)) {
            title.setText(titleContent);
        }
        builder.setView(view);
        return builder.create();
    }

//    @Override
//    public void onResume() {
//        super.onResume();
//        getDialog().getWindow().setLayout(DisplayUtil.dip2px(getActivity(), 300), ViewGroup.LayoutParams.WRAP_CONTENT);
//    }
}
