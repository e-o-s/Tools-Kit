package com.eos.colortransfer;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;

import android.annotation.SuppressLint;
import android.os.Build;
import android.text.ClipboardManager;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.example.colortransfer.R;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity implements View.OnFocusChangeListener, View.OnTouchListener, View.OnClickListener {
    private EditText rVal,gVal,bVal,aVal;
    private LinearLayout mainView;
    private TextView toneView;
    private ImageView colorExample;
    private boolean isTransparent;
    private static final String TAG = "XXXMainActivity";

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @SuppressLint({"ClickableViewAccessibility", "ResourceAsColor"})
    private void init() {
        rVal = findViewById(R.id.r);
        gVal = findViewById(R.id.g);
        bVal = findViewById(R.id.b);
        aVal = findViewById(R.id.a);
        Button exchangeBt = findViewById(R.id.exchange);
        mainView = findViewById(R.id.main_layout);
        toneView = findViewById(R.id.tone);
        colorExample = findViewById(R.id.color_display);

        //设置EditView焦点监听
        rVal.setOnFocusChangeListener(this);
        gVal.setOnFocusChangeListener(this);
        bVal.setOnFocusChangeListener(this);
        aVal.setOnFocusChangeListener(this);
        mainView.setOnTouchListener(this);
        exchangeBt.setOnClickListener(this);
        toneView.setOnClickListener(this);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        switch (v.getId()) {
            case R.id.r:
                setHintText(rVal,hasFocus);
                break;
            case R.id.g:
                setHintText(gVal,hasFocus);
                break;
            case R.id.b:
                setHintText(bVal,hasFocus);
                break;
            case R.id.a:
                setHintText(aVal,hasFocus);
                break;
            default:
                break;
        }
    }

    private void setHintText(EditText editText,boolean hasFocus) {
        String hintText;
        if(hasFocus) {
            hintText = editText.getHint().toString();
            editText.setTag(hintText);
            Log.d(TAG,"有聚焦设置的Tag的值为" + editText.getTag().toString());
            editText.setHint("");
        } else {
            Log.d(TAG,"无聚焦获取的Tag的值为" + editText.getTag().toString());
            hintText = editText.getTag().toString();
            editText.setHint(hintText);
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        mainView.setFocusable(true);
        mainView.setFocusableInTouchMode(true);
        mainView.requestFocus();
        return false;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.exchange) {
            //计算颜色值并更新TextView和实例ImageView
            String tone = calTone();
            if (isTransparent) {
                mainView.setBackgroundColor(Color.parseColor("#000000"));
                isTransparent = false;
            }else
                mainView.setBackgroundColor(Color.parseColor("#ffffff"));
            toneView.setTextColor(Color.parseColor(tone));
            toneView.setText(tone);
            colorExample.setBackgroundColor(Color.parseColor(tone));
        }
        if(v.getId() == R.id.tone) {
            ClipboardManager cm = (ClipboardManager)getApplicationContext().getSystemService(Context.CLIPBOARD_SERVICE);
            Log.d(TAG,"获取的色值为：" + toneView.getText().toString());
            String tempTone = toneView.getText().toString().equals("色值")?"#ff000000":toneView.getText().toString();
            cm.setText(tempTone);
            Toast.makeText(getApplicationContext(), "色值已复制到剪切板", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 计算十六进制色值
     * @return
     */
    private String calTone() {
         String r = setRGBAValue(rVal.getText().toString(),true),
                g = setRGBAValue(gVal.getText().toString(),true),
                b = setRGBAValue(bVal.getText().toString(),true),
                a = setRGBAValue(aVal.getText().toString(),false);
         if(r.equals("ff") && g.equals("ff") && b.equals("ff"))
             isTransparent = true;
         return "#" + a + r + g + b;
    }

    /**
     * 根据获取到edit文本框的内容来设置实际的r,g,b,a值
     * @param text 文本输入框的实际值
     * @return
     */
    private String setRGBAValue(String text, boolean isRGBorA) {
        if(isRGBorA) {
            if (canParseInt(text)) {
                int val = Integer.parseInt(text);
                return decToHex(val);
            }
            return "00";
        } else {
            if(canParseDouble(text)) {
                double val = Float.parseFloat(text);
                int tempVal = (int) (val*255);
                return decToHex(tempVal);
            }
            return "ff";
        }
    }

    /**
     * 十进制整数转十六进制
     * @param val
     * @return
     */
    private String decToHex(int val) {
        String tempVal = "";
        if (val >= 0 && val <= 255) {
            tempVal = Integer.toHexString(val);
            if(val<=15)
                tempVal = "0"+tempVal;
        } else
            tempVal = "00";
        return tempVal;
    }

    /**
     * 使用正则表达式判断该字符串是否为数字，第一个\是转义符，\d+表示匹配1个或
     * 多个连续数字，"+"和"*"类似，"*"表示0个或多个`
     * @param str
     * @return
     */
    public boolean canParseInt(String str){
        if(str == null || str.length()==0){
            return false;
        }
        return str.matches("\\d+");
    }

    /**
     * 判断一个字符串是否是一个0~1之间的两位小数
     * @param str
     * @return
     */
    private boolean canParseDouble(String str) {
        if(str == null || str.length()==0) {
            return false;
        }
        //特殊处理
        if(str.equals("1.0"))
            return true;
        Pattern p2 = Pattern.compile("\\b(0(\\.\\d{1,2})?)|1\\b");
        Matcher m = p2.matcher(str);
        return m.matches();
    }

}