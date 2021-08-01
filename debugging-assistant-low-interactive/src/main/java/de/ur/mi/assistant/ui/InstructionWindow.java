package de.ur.mi.assistant.ui;

import com.intellij.ide.BrowserUtil;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowManager;
import de.ur.mi.assistant.utils.Utils;
import de.ur.mi.assistant.utils.strings.InstructionWindowStrings;

import javax.swing.*;
import java.util.Arrays;

public class InstructionWindow implements InstructionWindowStrings {

    private JPanel contentContainer;
    private JPanel headerContainer;
    private JButton btnOpenLogcat;
    private JLabel heading;
    private JLabel subHeading;
    private JLabel firstStepMsg;
    private JLabel secondStepMsg;
    private JLabel thirdStepMsg;
    private JLabel fourthStepMsg;
    private JLabel useFulActionsMsg;
    private JLabel firstStepTitle;
    private JLabel secondStepTitle;
    private JLabel thirdStepTitle;
    private JLabel fourthStepTitle;
    private JLabel useFulActionTitle;

    private ToolWindow toolWindow;
    private JPanel panel1;

    public InstructionWindow(ToolWindow toolWindow) {
        this.toolWindow = toolWindow;
        setupJLabels();
        setupListener();
    }

    /**
     * Set the Strings to the corresponding JLabels
     */
    private void setupJLabels() {
        heading.setText(TITLE);
        subHeading.setText(IW_SUB_HEADING);
        firstStepTitle.setText(IW_TITLE_STEP_1);
        firstStepMsg.setText(IW_DESCRIPTION_STEP_1);
        secondStepTitle.setText(IW_TITLE_STEP_2);
        secondStepMsg.setText(IW_DESCRIPTION_STEP_2);
        thirdStepTitle.setText(IW_TITLE_STEP_3);
        thirdStepMsg.setText(IW_DESCRIPTION_STEP_3);
        fourthStepTitle.setText(IW_TITLE_STEP_4);
        fourthStepMsg.setText(IW_DESCRIPTION_STEP_4);
        useFulActionTitle.setText(IW_TITLE_USEFULLY_ACTIONS);
        useFulActionsMsg.setText(IW_DESCRIPTION_USEFULLY_ACTIONS);
    }

    public ToolWindow getToolWindow() {
        return toolWindow;
    }

    private void setupListener() {
        btnOpenLogcat.addActionListener(e -> openLogcat());
    }

    private void openLogcat() {
        ToolWindow logcat = ToolWindowManager.getInstance(Utils.getCurrentProject(getContent()))
                .getToolWindow(TOOL_WINDOW_LOG_CAT);
        if (logcat != null) {
            logcat.show();
        }
    }

    public JPanel getContent() {
        return contentContainer;
    }

}
