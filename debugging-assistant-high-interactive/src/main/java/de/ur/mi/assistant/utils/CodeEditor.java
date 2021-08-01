package de.ur.mi.assistant.utils;

import com.intellij.execution.ProgramRunnerUtil;
import com.intellij.execution.RunManager;
import com.intellij.execution.RunnerAndConfigurationSettings;
import com.intellij.execution.executors.DefaultRunExecutor;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.markup.*;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiParameter;
import com.intellij.psi.codeStyle.CodeStyleManager;
import com.intellij.psi.codeStyle.JavaCodeStyleManager;
import com.intellij.ui.JBColor;

import java.awt.*;
import java.util.Arrays;
import java.util.Collections;

public class CodeEditor {

    private static final Color HIGHLIGHT_METHOD_AND_CLASS = new JBColor(new Color(18, 130, 70),
            new Color(21, 179, 95));

    private static final Color HIGHLIGHT_METHOD_PARAM = new JBColor(new Color(189, 126, 40),
            new Color(235, 158, 52));
    private static final Color HIGHLIGHT_CLASS_FIELDS = new JBColor(new Color(30, 156, 135),
            new Color(43, 214, 186));

    /**
     * Highlights the Parent-Class and Parent-Method of the selected {@param el}
     *
     * @param el current selected inputElement
     * @param document current focussed document
     */
    public static void addMethodAndClassHighlights(CodeInspector.InspectedElement el, Document document) {
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
    }

    /**
     * Fills the generated log-string to the opened file/editor
     * @param document current focussed document
     * @param element generated outputElement
     */
    public static void fillLogString(Document document, CodeInspector.InspectedElement element) {
        int offset = CodeInspector.getFillOffset(document, element);
        Project project = element.getContainingProject();

        document.insertString(offset, StatementGenerator.getFillLogStringWithIndent(document, offset, element));

        PsiDocumentManager.getInstance(project).doPostponedOperationsAndUnblockDocument(document);
        PsiDocumentManager.getInstance(project).commitDocument(document);
        JavaCodeStyleManager.getInstance(project).shortenClassReferences(element.getContainingFile());

        CodeStyleManager manager = CodeStyleManager.getInstance(project);
        manager.reformatText(element.getContainingFile(), Collections.singletonList(element.getInspectedMethod()
                .getTextRange()));
    }

    /**
     * Highlights a psiElement in the editor with a colored box
     * @param psiElement element to be highlighted
     * @param isParam true when psiElement is psiParam
     * @return generated Highlighter instance
     */
    public static RangeHighlighter highlightElement(PsiElement psiElement, boolean isParam) {
        final FileEditorManager editorManager = FileEditorManager.getInstance(psiElement.getProject());
        final Editor editor = editorManager.getSelectedTextEditor();
        if (editor == null) return null;

        final TextAttributes textAttributes = new TextAttributes(null, null,
                isParam ? HIGHLIGHT_METHOD_PARAM : HIGHLIGHT_CLASS_FIELDS, EffectType.ROUNDED_BOX, Font.PLAIN);

        return editor.getMarkupModel().addRangeHighlighter(psiElement.getTextRange().getStartOffset(),
                psiElement.getTextRange().getEndOffset(), HighlighterLayer.CARET_ROW, textAttributes,
                HighlighterTargetArea.EXACT_RANGE);
    }

    /**
     * Removes rangeHighlighter from editor
     * @param psiElement highlighted element
     * @param rangeHighlighter instance generated when highlighted psiElement
     */
    public static void removeHighlight(PsiElement psiElement, RangeHighlighter rangeHighlighter) {
        final FileEditorManager editorManager = FileEditorManager.getInstance(psiElement.getProject());
        final Editor editor = editorManager.getSelectedTextEditor();
        if (editor == null) return;

        editor.getMarkupModel().removeHighlighter(rangeHighlighter);
    }
}
