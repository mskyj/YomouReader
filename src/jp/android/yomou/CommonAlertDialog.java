package jp.android.yomou;

import jp.android.yomou.R;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.TextView;

// 注意：リスナーでダイアログをdissmissしてください
// 対応レイアウト  dialog_normal, dialog_finish

public class CommonAlertDialog extends Dialog {
	
	private String title = "";
	private String message = "";
	private View.OnClickListener yesListener = null;
	private View.OnClickListener noListener = null;
	private int layoutID = R.layout.dialog_normal;
	
	public CommonAlertDialog(Context context) {
		super(context,R.style.MyAlertDialog);
	}
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		// タイトルなし
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        
        // layoutを設定
        setContentView(layoutID);
        
        // 画面の大きさに合わせる
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        getWindow().setAttributes(lp);
        
        if( layoutID == R.layout.dialog_normal ){
	        ((TextView)findViewById(R.id.dialog_title_txt)).setText(title);
	        ((TextView)findViewById(R.id.dialog_message_txt)).setText(message);
	        if( yesListener != null )
	        	((ImageButton)findViewById(R.id.dialog_yes_btn)).setOnClickListener(yesListener);
	        else
	        	((ImageButton)findViewById(R.id.dialog_yes_btn)).setVisibility(View.GONE);
	        if( noListener != null )
	        	((ImageButton)findViewById(R.id.dialog_no_btn)).setOnClickListener(noListener);
	        else
	        	((ImageButton)findViewById(R.id.dialog_no_btn)).setVisibility(View.GONE);
	        
	        // リスナーが一つの場合、「閉じる」ボタンのみにする
	        if( yesListener == null || noListener == null ){
	        	((View)findViewById(R.id.dialog_space)).setVisibility(View.GONE);
	        	if( yesListener != null )
	        		((ImageButton)findViewById(R.id.dialog_yes_btn)).setImageResource(R.drawable.dialog_btn_close);
	        	else if( noListener != null )
	        		((ImageButton)findViewById(R.id.dialog_no_btn)).setImageResource(R.drawable.dialog_btn_close);
	        }
        }
        
	}
	
	public void setTitle( String title ){
		this.title = title;
	}
	public void setMessage( String message ){
		this.message = message;
	}
	public void setPositiveButton( View.OnClickListener listener ){
		yesListener = listener;
	}
	public void setNegativeButton( View.OnClickListener listener ){
		noListener = listener;
	}
	public void setLayout( int layout ){
		layoutID = layout;
	}
}
