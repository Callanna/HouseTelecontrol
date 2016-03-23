package com.github.callanna.metarialframe.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.github.callanna.metarialframe.R;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Callanna on 2015/12/21.
 */
public class TabFragment extends Fragment {

    private String content;
    private View view;
    TextInputLayout inputLayout;
    EditText et_email,et_pwd;
    private java.lang.String regex = "^(([\\w-]+\\.)+[\\w-]+|([a-zA-Z]{1}|[\\w-]{2,}))@"
            +"((([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
            +"[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\."
            +"([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
            +"[0-9]{1,2}|25[0-5]|2[0-4][0-9])){1}|"
            +"([a-zA-Z]+[\\w-]+\\.)+[a-zA-Z]{2,4})$";   ;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_tab, container,false);
        inputLayout = (TextInputLayout) view.findViewById(R.id.txt_layout);
        et_email= (EditText) view.findViewById(R.id.et_email);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        content = getArguments().getString("content");
        TextView tvContent = (TextView) view.findViewById(R.id.txt_tab_content);
        tvContent.setText(content + "");
        inputLayout.setHint("请输入邮箱！");
        et_email.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                Matcher matcher = Pattern.compile(regex).matcher(charSequence);
                if(!matcher.matches()){
                    inputLayout.setErrorEnabled(true);
                    inputLayout.setError("email格式不对！");
                }else{
                   final Snackbar snackbar = Snackbar.make(inputLayout,"确定注册？",Snackbar.LENGTH_LONG);
                    snackbar.show();
                    snackbar.setAction("取消",new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            snackbar.dismiss();
                        }
                    });
                }
            }
            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
    }
}
