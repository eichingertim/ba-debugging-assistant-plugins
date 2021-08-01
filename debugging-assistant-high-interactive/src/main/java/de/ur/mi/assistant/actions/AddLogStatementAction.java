package de.ur.mi.assistant.actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.editor.Caret;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.util.PsiTreeUtil;
import de.ur.mi.assistant.services.ProjectService;
import de.ur.mi.assistant.utils.CodeInspector;
import de.ur.mi.assistant.utils.InternLogger;
import org.jetbrains.annotations.NotNull;

/**
 * Entrypoint for Debugging Assistant Wizard. Here the selected caret is processed and a inputElement
 * (InspectedElement) is generated for the selected caret and passed to the wizard window.
 */
public class AddLogStatementAction extends AnAction {

    private static final String TAG = AddLogStatementAction.class.getSimpleName();

    @Override
    public void update(@NotNull AnActionEvent e) {

        final Caret caret = e.getRequiredData(CommonDataKeys.CARET);
        final PsiFile file = e.getRequiredData(CommonDataKeys.PSI_FILE);

        PsiElement element = file.findElementAt(caret.getOffset());
        PsiMethod method = PsiTreeUtil.getParentOfType(element, PsiMethod.class);

        e.getPresentation().setEnabledAndVisible(element != null && method != null);
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {

        try {
            final Caret caret = e.getRequiredData(CommonDataKeys.CARET);
            final PsiFile file = e.getRequiredData(CommonDataKeys.PSI_FILE);

            //inputElement is generated from selected caret
            CodeInspector.InspectedElement el = CodeInspector.prepareInspectedElement(caret, file);

            if (el != null) {
                ProjectService projectService = ProjectService.getInstance(file.getProject());

                //Wizard becomes visible
                projectService.showWizard(file.getProject());

                //input Element is passed to the wizard window
                projectService.getWizardWindow().setInspectedElement(el);
            } else {
                InternLogger.warn(TAG, "inspected element is null");
            }

        } catch (Exception ex) {
            InternLogger.error(TAG, ex.getMessage());
        }



    }
}
