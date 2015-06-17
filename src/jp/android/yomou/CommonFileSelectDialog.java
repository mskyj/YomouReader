package jp.android.yomou;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;

public class CommonFileSelectDialog extends Activity implements OnClickListener {
	 
    /** �A�N�e�B�r�e�B */
    private Activity activity = null;
 
    /** ���X�i�[ */
    private OnFileSelectDialogListener listener = null;
 
    /** �ΏۂƂȂ�g���q */
    private String extension = "";
 
    /** �\�����̃t�@�C����񃊃X�g */
    private List<File> viewFileDataList = null;
 
    /** �\���p�X�̗��� */
    private List<String> viewPathHistory = null;
 
    /**
     * �R���g���N�g
     *
     * @param activity �A�N�e�B�r�e�B
     */
    public CommonFileSelectDialog(Activity activity) {
 
        this.activity = activity;
        this.viewPathHistory = new ArrayList<String>();
    }
 
    /**
     * �R���g���N�g
     *
     * @param activity �A�N�e�B�r�e�B
     * @param extension �ΏۂƂȂ�g���q
     */
    public CommonFileSelectDialog(Activity activity, String extension) {
 
        this.activity = activity;
        this.extension = extension;
        this.viewPathHistory = new ArrayList<String>();
    }
 
    /**
     * �I���C�x���g
     *
     * @param dialog �_�C�A���O
     * @param which �I���ʒu
     */
    @Override
    public void onClick(DialogInterface dialog, int which) {
 
        File file = this.viewFileDataList.get(which);
 
        // �f�B���N�g���̏ꍇ
        if (file.isDirectory()) {
 
            show(file.getAbsolutePath() + "/");
 
        } else {
 
            this.listener.onClickFileSelect(file);
        }
    }
 
    /**
     * �_�C�A���O��\��
     *
     * @param dirPath �f�B���N�g���̃p�X
     */
    public void show(final String dirPath) {
 
        // �ύX����̏ꍇ
        if (this.viewPathHistory.size() == 0 || !dirPath.equals(this.viewPathHistory.get(this.viewPathHistory.size() - 1))) {
 
            // ������ǉ�
            this.viewPathHistory.add(dirPath);
        }
 
        // �t�@�C�����X�g
        File[] fileArray = new File(dirPath).listFiles();
 
        // ���O���X�g
        List<String> nameList = new ArrayList<String>();
 
        if (fileArray != null) {
 
            // �t�@�C�����}�b�v
            Map<String, File> map = new HashMap<String, File>();
 
            for (File file : fileArray) {
 
                // �f�B���N�g���̏ꍇ
                if (file.isDirectory()) {
 
                    nameList.add(file.getName() + "/");
                    map.put(nameList.get(map.size()), file);
 
                // �ΏۂƂȂ�g���q�̏ꍇ
                } else if ("".equals(this.extension) || file.getName().matches("^.*" + this.extension + "$")) {
 
                    nameList.add(file.getName());
                    map.put(nameList.get(map.size()), file);
                }
            }
 
            // �\�[�g
            Collections.sort(nameList);
 
            // �t�@�C����񃊃X�g
            this.viewFileDataList = new ArrayList<File>();
 
            for (String name : nameList) {
 
                this.viewFileDataList.add(map.get(name));
            }
        }
 
        // �_�C�A���O�𐶐�
        AlertDialog.Builder dialog = new AlertDialog.Builder(this.activity);
        dialog.setTitle(dirPath);
        //dialog.setIcon(R.drawable.file);
        dialog.setItems(nameList.toArray(new String[0]), this);
 
        dialog.setPositiveButton("�� ��", new DialogInterface.OnClickListener() {
 
            @Override
            public void onClick(DialogInterface dialog, int value) {
 
                if (!"/".equals(dirPath)) {
 
                    String dirPathNew = dirPath.substring(0, dirPath.length() - 1);
                    dirPathNew = dirPathNew.substring(0, dirPathNew.lastIndexOf("/") + 1);
 
                    // ������ǉ�
                    CommonFileSelectDialog.this.viewPathHistory.add(dirPathNew);
 
                    // 1���
                    show(dirPathNew);
 
                } else {
 
                    // ����ێ�
                    show(dirPath);
                }
            }
        });
 
        dialog.setNeutralButton("�� ��", new DialogInterface.OnClickListener() {
 
            @Override
            public void onClick(DialogInterface dialog, int value) {
 
                int index = CommonFileSelectDialog.this.viewPathHistory.size() - 1;
 
                if (index > 0) {
 
                    // �������폜
                	CommonFileSelectDialog.this.viewPathHistory.remove(index);
 
                    // 1�O�ɖ߂�
                    show(CommonFileSelectDialog.this.viewPathHistory.get(index - 1));
 
                } else {
 
                    // ����ێ�
                    show(dirPath);
                }
            }
        });
 
        dialog.setNegativeButton("�L�����Z��", new DialogInterface.OnClickListener() {
 
            @Override
            public void onClick(DialogInterface dialog, int value) {
 
            	CommonFileSelectDialog.this.listener.onClickFileSelect(null);
            }
        });
 
        dialog.show();
    }
 
    /**
     * ���X�i�[��ݒ�
     *
     * @param listener �I���C�x���g���X�i�[
     */
    public void setOnFileSelectDialogListener(OnFileSelectDialogListener listener) {
 
        this.listener = listener;
    }
 
    /**
     * �{�^�������C���^�[�t�F�[�X
     */
    public interface OnFileSelectDialogListener {
 
        /**
         * �I���C�x���g
         *
         * @param file �t�@�C��
         */
        public void onClickFileSelect(File file);
    }
}