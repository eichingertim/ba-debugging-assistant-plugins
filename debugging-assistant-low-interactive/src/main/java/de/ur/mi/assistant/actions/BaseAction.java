package de.ur.mi.assistant.actions;

import com.intellij.codeInsight.template.TemplateManager;
import com.intellij.codeInsight.template.impl.TemplateImpl;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Caret;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.util.PsiTreeUtil;
import de.ur.mi.assistant.services.ProjectService;
import de.ur.mi.assistant.utils.CodeEditor;
import de.ur.mi.assistant.utils.CodeInspector;
import de.ur.mi.assistant.utils.InternLogger;
import de.ur.mi.assistant.utils.StatementGenerator;
import org.jetbrains.annotations.NotNull;

/**
 * Base Class for a Log-Statement Creation Action
 * Every Action has the same process except the information that is generated for the statment,
 * which is gathered through the abstract method {@link #getStatementHolder(CodeInspector.InspectedElement)}
 */
public abstract class BaseAction extends AnAction {

    private static final String TAG = BaseAction.class.getSimpleName();

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
            final Project project = e.getRequiredData(CommonDataKeys.PROJECT);
            final Caret caret = e.getRequiredData(CommonDataKeys.CARET);
            final PsiFile file = e.getRequiredData(CommonDataKeys.PSI_FILE);
            final Editor editor = e.getRequiredData(CommonDataKeys.EDITOR);

            //inputElement is generated from selected caret
            CodeInspector.InspectedElement el = CodeInspector.prepareInspectedElement(caret, file);

            //log-statement with placeholder and variables is generated
            final StatementGenerator.Holder holder = getStatementHolder(el);

            TemplateManager templateManager = TemplateManager.getInstance(project);

            //Template is created
            TemplateImpl tempTemplate = StatementGenerator.createTemplate(holder.getStatement(), holder.getVariables());
            if (el != null) {
                //Parent-Method and Parent-Class are highlighted in the editor
                CodeEditor.addMethodAndClassHighlights(el, editor.getDocument());

                //Template-String gets inserted into the editor
                Runnable runnable = () ->
                        CodeEditor.fillTemplate(project, editor, el, templateManager, tempTemplate);

                WriteCommandAction.runWriteCommandAction(project, runnable);
            }


        } catch (Exception ex) {
            InternLogger.error(TAG, "actionPerformed: error: " + ex.getLocalizedMessage());
        }

    }

    /**
     * Generates all required information for the specific log-statement template
     *
     * @param el current input element, which was produced out of the current position where the template should
     *           be inserted
     * @return a Log-Statement string with placeholders and a list of variables which replace the placeholders on
     * insertion
     */
    protected abstract StatementGenerator.Holder getStatementHolder(CodeInspector.InspectedElement el);


}
