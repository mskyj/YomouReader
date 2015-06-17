package jp.android.yomou;

import jp.android.yomou.R;
import android.app.Dialog;
import android.content.Context;

public class CommonProgressDialog extends Dialog {

	public CommonProgressDialog(Context context) {
		super(context, R.style.MyProgressDialog);
		setContentView(R.layout.dialog_progress);
	}

}
