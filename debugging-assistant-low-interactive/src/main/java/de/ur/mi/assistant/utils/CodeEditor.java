package de.ur.mi.assistant.utils;

import com.intellij.codeInsight.template.Template;
import com.intellij.codeInsight.template.TemplateEditingListener;
import com.intellij.codeInsight.template.TemplateManager;
import com.intellij.codeInsight.template.impl.TemplateState;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.markup.EffectType;
import com.intellij.openapi.editor.markup.HighlighterLayer;
import com.intellij.openapi.editor.markup.TextAttributes;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.codeStyle.CodeStyleManager;
import com.intellij.psi.codeStyle.JavaCodeStyleManager;
import com.intellij.ui.JBColor;
import de.ur.mi.assistant.utils.strings.Strings;
import org.apache.xmlgraphics.util.i18n.LocaleGroup;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.Collections;

/**
 * Manipulates the editor with highlights and templates
 */
public class CodeEditor implements Strings {

    private static final String TAG = CodeEditor.class.getSimpleName();

    private static final Color HIGHLIGHT_METHOD_AND_CLASS = new JBColor(new Color(18, 130, 70),
            new Color(21, 179, 95));

    /**
     * Highlights the Parent-Class and Parent-Method of the selected {@param el}
     *
     * @param el current selected inputElement
     * @param document current focussed document
     */
    public static void addMethodAndClassHighlights(CodeInspector.InspectedElement el, Document document) {
        try {
            final FileEditorManager editorManager = FileEditorManager.getInstance(el.getContainingProject());
            final Editor editor = editorManager.getSelectedTextEditor();
            if (editor == null) return;

            int lineNum = document.getLineNumber(el.getInspectedMethod().getTextOffset());
            int lineNumClass = document.getLineNumber(el.getContainingClass().getTextOffset());

            final TextAttributes textAttributesMethod = new TextAttributes(null, null,
                    HIGHLIGHT_METHOD_AND_CLASS, EffectType.ROUNDED_BOX, Font.PLAIN);

            final TextAttributes textAttributesClass = new TextAttributes(null, null,
                    HIGHLIGHT_METHOD_AND_CLASS, EffectType.ROUNDED_BOX, Font.PLAIN);

            editor.getMarkupModel().addLineHighlighter(lineNumClass, HighlighterLayer.CARET_ROW, textAttributesClass);
            editor.getMarkupModel().addLineHighlighter(lineNum, HighlighterLayer.CARET_ROW, textAttributesMethod);
        } catch (Exception e) {
            InternLogger.error(TAG, e.getMessage());
        }
    }

    /**
     * Fills the generated template to the opened file/editor
     *
     * @param project current opened project
     * @param editor current opened editor
     * @param el inputElement whom the template was generated for
     * @param templateManager current instance
     * @param tempTemplate generated template
     */
    public static void fillTemplate(Project project, Editor editor, CodeInspector.InspectedElement el,
                                    TemplateManager templateManager, Template tempTemplate) {

        Document document = editor.getDocument();
        NotificationUtils.notifyInformation(project, NOTIFICATION_LOG_INSERTED);
        templateManager.startTemplate(editor, tempTemplate, new TemplateEditingListener() {
            @Override
            public void beforeTemplateFinished(@NotNull TemplateState state, Template template) {
                PsiDocumentManager.getInstance(project).doPostponedOperationsAndUnblockDocument(document);
                PsiDocumentManager.getInstance(project).commitDocument(document);
            }

            @Override
            public void templateFinished(@NotNull Template template, boolean brokenOff) {
                //Remove Code-Highlights when Template editing is finished
                editor.getMarkupModel().removeAllHighlighters();
            }

            @Override
            public void templateCancelled(Template template) {
                editor.getMarkupModel().removeAllHighlighters();
            }

            @Override
            public void currentVariableChanged(@NotNull TemplateState templateState, Template template, int oldIndex, int newIndex) {

            }

            @Override
            public void waitingForInput(Template template) {
            }
        });
        PsiDocumentManager.getInstance(project).doPostponedOperationsAndUnblockDocument(document);
        PsiDocumentManager.getInstance(project).commitDocument(document);
        JavaCodeStyleManager.getInstance(project).shortenClassReferences(el.getContainingFile());

        CodeStyleManager manager = CodeStyleManager.getInstance(project);
        manager.reformatText(el.getContainingFile(), Collections.singletonList(el.getInspectedMethod()
                .getTextRange()));
    }
}
