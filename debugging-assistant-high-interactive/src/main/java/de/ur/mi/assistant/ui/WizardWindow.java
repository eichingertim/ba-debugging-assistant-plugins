package de.ur.mi.assistant.ui;

import com.android.tools.idea.logcat.LogcatPanel;
import com.android.tools.idea.logcat.RegexFilterComponent;
import com.intellij.execution.ProgramRunnerUtil;
import com.intellij.execution.RunManager;
import com.intellij.execution.RunnerAndConfigurationSettings;
import com.intellij.execution.executors.DefaultRunExecutor;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.markup.RangeHighlighter;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.psi.*;
import com.intellij.ui.SearchTextFieldWithStoredHistory;
import com.intellij.ui.content.ContentManagerEvent;
import com.intellij.ui.content.ContentManagerListener;
import de.ur.mi.assistant.services.ProjectService;
import de.ur.mi.assistant.utils.*;
import de.ur.mi.assistant.utils.strings.Strings;
import de.ur.mi.assistant.utils.strings.WizardWindowStrings;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.plaf.basic.BasicTabbedPaneUI;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.*;

public class WizardWindow implements WizardWindowStrings, Strings {
    private static final String TAG = WizardWindow.class.getSimpleName();

    private JPanel container;
    private JTabbedPane tabbedPane;
    private JPanel tabMethodInfo;
    private JPanel tabClassInfo;
    private JPanel tabTagAndMessage;
    private JPanel tabOverview;
    private JButton btnContinueMethodInfo;
    private JButton btnCancelMethodInfo;
    private JButton btnBackClassInfo;
    private JButton btnCancelClassInfo;
    private JButton btnContinueClassInfo;
    private JButton btnBackTag;
    private JButton btnCancelTag;
    private JButton btnContinueTag;
    private JButton btnFill;
    private JButton btnCancelFinish;
    private JButton btnFillAndExecute;
    private JButton btnBackFinish;
    private JCheckBox cbReturnType;
    private JCheckBox cbMethodName;
    private JLabel tvReturnType;
    private JLabel tvMethodName;
    private JPanel containerParams;
    private JCheckBox cbClassName;
    private JLabel tvClassName;
    private JPanel containerClassFields;
    private JRadioButton rbTagFileName;
    private JRadioButton rbTagIndividual;
    private JLabel tvFileName;
    private JTextField tfTag;
    private JTextField tfMessage;
    private JLabel tvTagErrorMessage;
    private JLabel tvErrorMethodNotSelected;
    private JLabel tvGlobalMethodName;
    private JLabel tvGlobalClass;
    private JLabel tvGlobalLineNumber;
    private JButton btnBackMethod;
    private JRadioButton rbFillAtSelected;
    private JRadioButton rbFillAtStart;
    private JLabel tvLogPreview;
    private JPanel containerParamsRight;
    private JPanel containerParamsLeft;
    private JPanel containerClassFieldsLeft;
    private JPanel containerClassFieldRight;
    private JLabel tvMoreInfoParam;
    private JLabel tvMoreInfoTagNeccessaryInfo;
    private JPanel tvMoreInfoIndividualMsg;
    private JLabel tvMoreInfoClass;
    private JLabel tvMoreInfoClassFields;
    private JProgressBar pbSteps;
    private JLabel tvSteps;
    private JLabel tvEmptyParameter;
    private JLabel tvEmptyClassFields;

    private final ToolWindow toolWindow;

    private CodeInspector.InspectedElement wizardInputItem;
    private CodeInspector.InspectedElement wizardOutputItem;

    private final Map<PsiElement, RangeHighlighter> highlightedElements;

    public WizardWindow(ToolWindow toolWindow) {
        this.toolWindow = toolWindow;

        highlightedElements = new HashMap<>();

        setupUI();
        setupListener();
    }

    private void setupUI() {
        removeTopTabBar();
        addRadioButtonsToGroup();
    }

    /**
     * Removes the TopAppBar from TabbedPane to make sure the user cannot
     * skip steps. Additionally better looking
     */
    private void removeTopTabBar() {
        tabbedPane.setUI(new BasicTabbedPaneUI() {
            @Override
            protected int calculateTabAreaHeight(int tabPlacement, int horizRunCount, int maxTabHeight) {
                return -5;
            }

            @Override
            protected void paintContentBorder(Graphics g, int tabPlacement, int selectedIndex) {
                //super should not be called
            }
        });
    }

    /**
     * Adds all Radio-Buttons used in the wizard to the corresponding group to make it toggle-able
     */
    private void addRadioButtonsToGroup() {
        ButtonGroup buttonGroup = new ButtonGroup();
        buttonGroup.add(rbTagFileName);
        buttonGroup.add(rbTagIndividual);

        ButtonGroup buttonGroupFillAt = new ButtonGroup();
        buttonGroupFillAt.add(rbFillAtSelected);
        buttonGroupFillAt.add(rbFillAtStart);
    }

    // ================================================ Setup Listeners ================================================

    private void setupListener() {
        toolWindow.addContentManagerListener(new ContentManagerListener() {
            @Override
            public void contentAdded(@NotNull ContentManagerEvent event) {
                ContentManagerListener.super.contentAdded(event);
                removeTopTabBar();
            }
        });

        setupTagPageListeners();
        setupMethodPageListeners();
        setupClassPageListeners();
        setupFinishPageListeners();

        setupMoreInfoListeners();
    }

    /**
     * Registers all listeners on elements from the Wizard's Tag-Page (position=0)
     */
    private void setupTagPageListeners() {
        btnCancelTag.addActionListener(e -> resetWizard(true));
        btnContinueTag.addActionListener(e -> {
            if (resolveTagMessageInfoFromWizard()) {
                tabbedPane.setSelectedIndex(1);
                updateSteps();
                tvTagErrorMessage.setVisible(false);
            } else {
                tvTagErrorMessage.setVisible(true);
            }
        });

        rbTagFileName.addActionListener(e -> {
            wizardOutputItem.getOutput().setTAG(wizardOutputItem.getContainingFile().getName());
            StatementGenerator.updateLogPreview(wizardOutputItem, tvLogPreview);
        });

        rbTagIndividual.addActionListener(e -> {
            wizardOutputItem.getOutput().setTAG(tfTag.getText());
            StatementGenerator.updateLogPreview(wizardOutputItem, tvLogPreview);
        });

        tfTag.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                onTagUpdate();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                onTagUpdate();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                onTagUpdate();
            }
        });

        tfMessage.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                onMessageUpdate();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                onMessageUpdate();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                onMessageUpdate();
            }
        });
    }

    /**
     * Updates the current progress.
     * 1. Update textual visualization
     * 2. Update ProgressBar
     */
    private void updateSteps() {
        tvSteps.setText(String.format("Schritt %d von 4", tabbedPane.getSelectedIndex() + 1));
        pbSteps.setValue(tabbedPane.getSelectedIndex() + 1);
    }

    private void onMessageUpdate() {
        wizardOutputItem.getOutput().setIndividualMessage(tfMessage.getText());
        StatementGenerator.updateLogPreview(wizardOutputItem, tvLogPreview);
    }

    private void onTagUpdate() {
        if (rbTagIndividual.isSelected()) {
            wizardOutputItem.getOutput().setTAG(tfTag.getText());
            StatementGenerator.updateLogPreview(wizardOutputItem, tvLogPreview);
        }
    }

    /**
     * Registers all listeners on elements from the Wizard's Method-Page (position=1)
     */
    private void setupMethodPageListeners() {
        btnContinueMethodInfo.addActionListener(e -> {
            if (wizardInputItem == null) {
                tvErrorMethodNotSelected.setVisible(true);
            } else {
                tvErrorMethodNotSelected.setVisible(false);
                resolveMethodInfoFromWizard();
                tabbedPane.setSelectedIndex(2);
                updateSteps();
            }
        });
        btnBackMethod.addActionListener(e -> {
            tabbedPane.setSelectedIndex(0);
            updateSteps();
        });
        btnCancelMethodInfo.addActionListener(e -> resetWizard(true));
        cbMethodName.addActionListener(e -> {
            if (((JCheckBox) e.getSource()).isSelected()) {
                wizardOutputItem.setName(wizardInputItem.getInspectedMethod().getName());
            } else {
                wizardOutputItem.setName(null);
            }

            StatementGenerator.updateLogPreview(wizardOutputItem, tvLogPreview);
        });
        cbReturnType.addActionListener(e -> {
            if (((JCheckBox) e.getSource()).isSelected()) {
                wizardOutputItem.setReturnType(wizardInputItem.getReturnType());
            } else {
                wizardOutputItem.setReturnType(null);
            }

            StatementGenerator.updateLogPreview(wizardOutputItem, tvLogPreview);
        });
    }

    /**
     * Registers all listeners on elements from the Wizard's Class-Page (position=2)
     */
    private void setupClassPageListeners() {
        btnContinueClassInfo.addActionListener(e -> {
            resolveClassInfoFromWizard();
            tabbedPane.setSelectedIndex(3);
            updateSteps();
        });
        btnBackClassInfo.addActionListener(e -> {
            tabbedPane.setSelectedIndex(1);
            updateSteps();
        });
        btnCancelClassInfo.addActionListener(e -> resetWizard(true));
        cbClassName.addActionListener(e -> {
            if (((JCheckBox) e.getSource()).isSelected()) {
                wizardOutputItem.setContainingClass(wizardInputItem.getContainingClass());
            } else {
                wizardOutputItem.setContainingClass(null);
            }

            StatementGenerator.updateLogPreview(wizardOutputItem, tvLogPreview);
        });
    }

    /**
     * Registers all listeners on elements from the Wizard's Finish-Page (position=3)
     */
    private void setupFinishPageListeners() {
        btnBackFinish.addActionListener(e -> {
            tabbedPane.setSelectedIndex(2);
            updateSteps();
        });
        btnCancelFinish.addActionListener(e -> resetWizard(true));
        btnFill.addActionListener(e -> resolveFillOptionsAndFill());
    }

    /**
     * Reads the selected position where the user wants to insert the log-statement
     * from the last fragment and fills it to the editor.
     */
    private void resolveFillOptionsAndFill() {
        FileViewProvider fileViewProvider = wizardOutputItem.getContainingFile().getViewProvider();
        Document document = fileViewProvider.getDocument();

        resolveFillAtInformation();

        Runnable runnable = () -> {
            if (document != null) {
                NotificationUtils.notifyInformation(wizardInputItem.getContainingProject(), NOTIFICATION_LOG_INSERTED);
                CodeEditor.fillLogString(document, wizardOutputItem);
            }
        };

        WriteCommandAction.runWriteCommandAction(wizardOutputItem.getContainingProject(), runnable);
        showWizardFinishDialog();
    }

    /**
     * Shows Dialog where the user can choose to either build more statements or
     * execute the application and open logcat.
     */
    private void showWizardFinishDialog() {
        JLabel jLabel = new JLabel(WIZARD_COMPLETE_MESSAGE);
        jLabel.setSize(400, 150);
        jLabel.setPreferredSize(new Dimension(400, 150));

        int result = JOptionPane.showOptionDialog(null, jLabel, WIZARD_COMPLETE_TITLE,
                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null,
                new String[]{WIZARD_COMPLETE_OPTION_EXECUTE_APP, WIZARD_COMPLETE_OPTION_ADD_MORE}, null);

        if (result == 0) {
            executeAppAndOpenLogcat();
        } else {
            resetWizard(true);
        }
    }

    /**
     * Runs the current buildConfiguration and opens the logcat
     */
    private void executeAppAndOpenLogcat() {
        try {
            RunnerAndConfigurationSettings runSettings = RunManager.getInstance(wizardInputItem.getContainingProject())
                    .getSelectedConfiguration();
            if (runSettings != null) {
                ProgramRunnerUtil.executeConfiguration(runSettings, DefaultRunExecutor.getRunExecutorInstance());
            }
            openLogcatAndAddFilterTag();
            resetWizard(true);
        } catch (Exception e) {
            resetWizard(true);
        }

    }

    /**
     * Registers a listener on a checkbox which is responsible for including a passed method-parameter into the
     * log-string
     *
     * @param checkbox     Generated for given parameter
     * @param psiParameter One Parameter of inspected method
     */
    private void setupLiveCheckBoxParamsListener(JCheckBox checkbox, PsiParameter psiParameter) {
        checkbox.addActionListener(e -> {
            List<PsiParameter> p = wizardOutputItem.getParams() != null ?
                    new ArrayList<>(Arrays.asList(wizardOutputItem.getParams())) : new ArrayList<>();
            if (((JCheckBox) e.getSource()).isSelected()) {
                p.add(psiParameter);
                highlightedElements.put(psiParameter, CodeEditor.highlightElement(psiParameter, true));
            } else {
                p.remove(psiParameter);
                CodeEditor.removeHighlight(psiParameter, highlightedElements.get(psiParameter));
            }
            wizardOutputItem.setParams(p.toArray(new PsiParameter[0]));
            StatementGenerator.updateLogPreview(wizardOutputItem, tvLogPreview);
        });
    }

    /**
     * Registers a listener on a checkbox which is responsible for including a passed class-fields into the
     * log-string
     *
     * @param checkbox Generated for given field
     * @param psiField One field of inspected method's class
     */
    private void setupLiveCheckBoxClassFieldsListener(JCheckBox checkbox, PsiField psiField) {
        checkbox.addActionListener(e -> {
            List<PsiField> p = wizardOutputItem.getClassFields() != null ?
                    new ArrayList<>(Arrays.asList(wizardOutputItem.getClassFields())) : new ArrayList<>();
            if (((JCheckBox) e.getSource()).isSelected()) {
                p.add(psiField);
                highlightedElements.put(psiField, CodeEditor.highlightElement(psiField, true));
            } else {
                p.remove(psiField);
                CodeEditor.removeHighlight(psiField, highlightedElements.get(psiField));
            }
            wizardOutputItem.setClassFields(p.toArray(new PsiField[0]));
            StatementGenerator.updateLogPreview(wizardOutputItem, tvLogPreview);
        });
    }

    /**
     * Adds Listener to every "More Information" Links inside the wizard.
     */
    private void setupMoreInfoListeners() {
        tvMoreInfoParam.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                showInfoDialog("Methoden-Parameter", MORE_INFO_PARAMS_MESSAGE);
            }
        });

        tvMoreInfoTagNeccessaryInfo.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                showInfoDialog("Notwendige Informationen", MORE_INFO_TAG_MESSAGE);
            }
        });

        tvMoreInfoIndividualMsg.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                showInfoDialog("Individuelle Nachricht", MORE_INFO_INDIV_MSG_MESSAGE);
            }
        });

        tvMoreInfoClassFields.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                showInfoDialog("Variablen/Felder Klasse", MORE_INFO_CLASS_FIELD_MESSAGE);
            }
        });

        tvMoreInfoClass.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                showInfoDialog("Klassename", MORE_INFO_CLASS_NAME);
            }
        });
    }

    // ================================== Extracting selected information from wizard ==================================

    private void resolveFillAtInformation() {
        wizardOutputItem.getOutput().setShouldFillAtStart(rbFillAtStart.isSelected());
    }

    private boolean resolveTagMessageInfoFromWizard() {
        wizardOutputItem.setName(wizardInputItem.getInspectedMethod().getName());
        if (rbTagFileName.isSelected()) {
            wizardOutputItem.getOutput().setTAG(wizardInputItem.getContainingFile().getName());
        } else if (tfTag.getText() != null && !tfTag.getText().isEmpty()) {
            wizardOutputItem.getOutput().setTAG(tfTag.getText());
        } else {
            return false;
        }

        if (tfMessage.getText() != null && !tfMessage.getText().isEmpty()) {
            wizardOutputItem.getOutput().setIndividualMessage(tfMessage.getText());
        }


        StatementGenerator.updateLogPreview(wizardOutputItem, tvLogPreview);
        return true;
    }

    private void resolveClassInfoFromWizard() {
        if (cbClassName.isSelected()) {
            wizardOutputItem.setContainingClass(wizardInputItem.getContainingClass());
        }

        List<PsiField> selectedClassFields = new ArrayList<>();
        selectedClassFields.addAll(retrieveSelectedClassFields(containerClassFieldsLeft.getComponents()));
        selectedClassFields.addAll(retrieveSelectedClassFields(containerClassFieldRight.getComponents()));
        wizardOutputItem.setClassFields(selectedClassFields.toArray(new PsiField[0]));

        StatementGenerator.updateLogPreview(wizardOutputItem, tvLogPreview);
    }

    private void resolveMethodInfoFromWizard() {
        if (cbMethodName.isSelected()) {
            wizardOutputItem.setName(wizardInputItem.getName());
        }

        if (cbReturnType.isSelected()) {
            wizardOutputItem.setReturnType(wizardInputItem.getReturnType());
        }

        List<PsiParameter> selectedParams = new ArrayList<>();
        selectedParams.addAll(retrieveSelectedParams(containerParamsLeft.getComponents()));
        selectedParams.addAll(retrieveSelectedParams(containerParamsRight.getComponents()));
        wizardOutputItem.setParams(selectedParams.toArray(new PsiParameter[0]));

        StatementGenerator.updateLogPreview(wizardOutputItem, tvLogPreview);
    }

    private List<? extends PsiParameter> retrieveSelectedParams(Component[] components) {
        List<PsiParameter> selectedParams = new ArrayList<>();
        if (components != null && components.length > 0) {
            for (Component component : components) {
                JCheckBox checkBox = (JCheckBox) component;
                if (checkBox.isSelected())
                    selectedParams.add(findParamWithName(checkBox.getText()));
            }
        }
        return selectedParams;
    }

    private PsiParameter findParamWithName(String name) {
        for (PsiParameter p : wizardInputItem.getParams()) if (p.getName().equals(name)) return p;
        return null;
    }

    private List<? extends PsiField> retrieveSelectedClassFields(Component[] components) {
        List<PsiField> selectedParams = new ArrayList<>();
        if (components != null && components.length > 0) {
            for (Component component : components) {
                JCheckBox checkBox = (JCheckBox) component;
                if (checkBox.isSelected())
                    selectedParams.add(findClassFieldWithName(checkBox.getText()));
            }
        }
        return selectedParams;
    }

    private PsiField findClassFieldWithName(String name) {
        for (PsiField p : wizardInputItem.getClassFields()) if (p.getName().equals(name)) return p;
        return null;
    }

    // ======================================== PreviewUpdate and Wizard Reset =========================================


    /**
     * Resets the wizard process
     */
    private void resetWizard(boolean shouldClose) {

        tvMethodName.setText("");
        cbMethodName.setSelected(false);
        cbReturnType.setSelected(false);
        tvReturnType.setText("");

        rbTagFileName.setSelected(true);
        tfTag.setText("");
        tfMessage.setText("");

        pbSteps.setValue(1);

        tvErrorMethodNotSelected.setVisible(false);

        tvLogPreview.setText("");
        tabbedPane.setSelectedIndex(0);
        containerParamsRight.removeAll();
        containerParamsLeft.removeAll();
        containerClassFieldRight.removeAll();
        containerClassFieldsLeft.removeAll();

        Project project = null;
        try {
            project = wizardOutputItem.getContainingProject();
            final FileEditorManager editorManager = FileEditorManager.getInstance(project);
            final Editor editor = editorManager.getSelectedTextEditor();

            if (editor != null)
                editor.getMarkupModel().removeAllHighlighters();
        } catch (Exception e) {
            System.out.println(e.getLocalizedMessage());
        }

        wizardInputItem = null;
        wizardOutputItem = null;

        if (shouldClose) {
            ProjectService projectService = ProjectService.getInstance(project);
            projectService.removeWizard();
        }
    }

    private void openLogcatAndAddFilterTag() {

        Project p = ProjectManager.getInstance().getOpenProjects()[0];
        ToolWindow logcat = ToolWindowManager.getInstance(p).getToolWindow("Logcat");
        if (logcat != null) {
            logcat.show();
            try {
                LogcatPanel panel = (LogcatPanel) logcat.getContentManager().getContents()[0].getComponent();
                RegexFilterComponent filterComp = (RegexFilterComponent) panel.getLogcatView().getLogConsole().getTextFilterComponent();
                ((SearchTextFieldWithStoredHistory) filterComp.getSearchTextField()).getTextEditor().setText(wizardOutputItem.getOutput().getTAG());
            } catch (Exception e) {
                InternLogger.error(TAG, e.getMessage());
            }
        } else {
            System.out.println(Arrays.toString(ToolWindowManager.getInstance(p).getToolWindowIdSet().toArray()));
        }
    }

    // =========================== Starting Wizard for new Inspected Element ===========================================

    /**
     * Highlight Inspected Method and Class in Code
     * Fills data from prepared InspectedElement to the corresponding fields from the wizardWindow
     *
     * @param el inspected element
     */
    public void setInspectedElement(CodeInspector.InspectedElement el) {
        resetWizard(false);

        PsiDocumentManager psiDocumentManager = PsiDocumentManager.getInstance(el.getContainingProject());
        Document document = psiDocumentManager.getDocument(el.getContainingFile());

        if (document == null) return;

        CodeEditor.addMethodAndClassHighlights(el, document);

        fillWizard(el, document);

        wizardInputItem = el;
        wizardOutputItem = new CodeInspector.InspectedElement();
        wizardOutputItem.setClickedOffset(el.getClickedOffset())
                .setInspectedMethod(el.getInspectedMethod())
                .setContainingFile(el.getContainingFile())
                .setContainingProject(el.getContainingProject())
                .getOutput().setTAG(el.getContainingFile().getName());


        StatementGenerator.updateLogPreview(wizardOutputItem, tvLogPreview);

        updateSteps();

    }

    /**
     * Fills data from prepared InspectedElement to the corresponding fields from the wizardWindow
     *
     * @param el       current inspected element
     * @param document current document from editor
     */
    private void fillWizard(CodeInspector.InspectedElement el, Document document) {
        tvGlobalClass.setText(String.format("<html>%s</html>", el.getContainingClass().getName()));
        tvGlobalMethodName.setText(String.format("<html>%s</html>", el.getName()));

        tvGlobalLineNumber.setText(String.format("<html>%s</html>", document.getLineNumber(el.getClickedOffset()) + 1));

        //Method Information
        tvMethodName.setText(String.format("<html>%s</html", el.getName()));
        cbMethodName.setSelected(true);
        tvReturnType.setText(String.format("<html>%s</html>", el.getReturnType().getPresentableText()));

        containerParamsLeft.removeAll();
        containerParamsRight.removeAll();

        GridLayout gridLayout = new GridLayout(0, 1);
        GridLayout gridLayout1 = new GridLayout(0, 1);
        containerParamsLeft.setLayout(gridLayout);
        containerParamsRight.setLayout(gridLayout1);

        int counter = 0;
        for (PsiParameter psiParameter : el.getParams()) {
            JCheckBox checkBox = new JCheckBox();
            checkBox.setName(psiParameter.getName());
            checkBox.setText(psiParameter.getName());
            setupLiveCheckBoxParamsListener(checkBox, psiParameter);
            if (counter % 2 == 0) containerParamsLeft.add(checkBox);
            else containerParamsRight.add(checkBox);
            counter++;
        }

        if (counter == 0) {
            tvEmptyParameter.setVisible(true);
            containerParams.setVisible(false);
        } else {
            tvEmptyParameter.setVisible(false);
            containerParams.setVisible(true);
        }

        //Class Information
        tvClassName.setText(String.format("<html>%s</html>", el.getContainingClass().getName()));

        containerClassFieldsLeft.removeAll();
        containerClassFieldRight.removeAll();
        containerClassFieldsLeft.setLayout(gridLayout);
        containerClassFieldRight.setLayout(gridLayout1);

        int counterFields = 0;
        for (PsiField psiField : el.getClassFields()) {
            JCheckBox checkBox = new JCheckBox();
            checkBox.setName(psiField.getName());
            checkBox.setText(psiField.getName());
            setupLiveCheckBoxClassFieldsListener(checkBox, psiField);
            if (counterFields % 2 == 0) containerClassFieldsLeft.add(checkBox);
            else containerClassFieldRight.add(checkBox);
            counterFields++;
        }

        if (counterFields == 0) {
            tvEmptyClassFields.setVisible(true);
            containerClassFields.setVisible(false);
        } else {
            tvEmptyClassFields.setVisible(false);
            containerClassFields.setVisible(true);
        }

        //Tag Message Information
        tvFileName.setText(String.format("<html>%s</html>", el.getContainingFile().getName()));
    }

    public ToolWindow getToolWindow() {
        return toolWindow;
    }

    public JPanel getContent() {
        return container;
    }

    public void showInfoDialog(String title, String text) {
        JLabel jLabel = new JLabel(text);
        JOptionPane.showMessageDialog(null, jLabel, title, JOptionPane.INFORMATION_MESSAGE);
    }

    public void onStateChanged(ToolWindowManager toolWindowManager) {
        removeTopTabBar();
    }
}
