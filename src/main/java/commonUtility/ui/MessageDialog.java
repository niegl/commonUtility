package commonUtility.ui;


import commonUtility.context.GUIState;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.stage.Modality;
import javafx.stage.Window;

import java.util.Optional;

public final class MessageDialog {
    private static Alert createAlert(Alert.AlertType type) {
        Window owner = GUIState.getStage();
        Alert dlg = new Alert(type, "");
        dlg.initModality(Modality.WINDOW_MODAL);
        dlg.initOwner(owner);
        return dlg;
    }

    /**
     * 弹出一个通用的确定对话框
     * @param p_header 对话框的信息标题
     * @param p_message 对话框的信息
     * @return 用户点击了是或否
     */
    public static boolean confirmDialog(String p_header, String p_message){
        Alert alert = createAlert(Alert.AlertType.CONFIRMATION);
        alert.getButtonTypes().addAll(new ButtonType("取消", ButtonBar.ButtonData.NO),
                new ButtonType("确定", ButtonBar.ButtonData.YES));
        alert.setTitle("确认");
        alert.setHeaderText(p_header);
        alert.setContentText(p_message);
        alert.initOwner(GUIState.getStage());
        // showAndWait() 将在对话框消失以前不会执行之后的代码
        Optional<ButtonType> _buttonType = alert.showAndWait();
        if(_buttonType.get().getButtonData().equals(ButtonBar.ButtonData.YES)){
            return true;
        }
        else {
            return false;
        }
    }

    //    弹出一个信息对话框
    public static void informationDialog(String p_header, String p_message){
        Alert _alert = createAlert(Alert.AlertType.INFORMATION);
        _alert.setTitle("信息");
        _alert.setHeaderText(p_header);
        _alert.setContentText(p_message);
        _alert.initOwner(GUIState.getStage());
        _alert.show();
    }
}
