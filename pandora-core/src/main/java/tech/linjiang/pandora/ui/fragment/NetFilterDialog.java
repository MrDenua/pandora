package tech.linjiang.pandora.ui.fragment;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;

import tech.linjiang.pandora.core.R;
import tech.linjiang.pandora.util.Config;
import tech.linjiang.pandora.util.NetFilter;

/**
 * <pre>
 * author : ZH-301-001
 * e-mail : denua@foxmail.com
 * time   : 2019/10/3
 * desc   :
 * </pre>
 */
public class NetFilterDialog extends Dialog {

    private Switch aSwitch;
    private Button button;
    private EditText editText;
    private OnChangeListener onChangeListener;

    private NetFilterDialog(@NonNull Context context) {
        super(context);
        setContentView(R.layout.fragment_net_filter);
        aSwitch = findViewById(R.id.sw_enable);
        button = findViewById(R.id.bt_ok);
        editText = findViewById(R.id.et_filter);

        editText.setText(Config.getNetFilterStr());
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String s = editText.getText().toString();
                Config.setNetFilter(s);
                NetFilter.setup();
                if (onChangeListener!= null){
                    onChangeListener.onChange();
                }
                dismiss();
            }
        });
        aSwitch.setChecked(NetFilter.isEnable());
        aSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    NetFilter.enable();
                } else {
                    NetFilter.disable();
                }
                if (onChangeListener!= null){
                    onChangeListener.onChange();
                }
            }
        });
    }

    public static NetFilterDialog create(Context context){
        return new NetFilterDialog(context);
    }

    public NetFilterDialog setOnChangeListener(OnChangeListener onOkListener){
        this.onChangeListener = onOkListener;
        return this;
    }

    public interface OnChangeListener{
        void onChange();
    }
}
