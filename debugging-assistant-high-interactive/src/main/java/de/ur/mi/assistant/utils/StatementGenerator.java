package de.ur.mi.assistant.utils;

import com.intellij.openapi.editor.Document;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiParameter;
import com.intellij.util.DocumentUtil;

import javax.swing.*;

/**
 * Generates Log-String and updates preview
 */
public class StatementGenerator {

    /**
     * Builds the Log-String for a generated outputElement
     * @param wizardOutputItem generated outputElement
     * @return built log-string
     */
    public static String buildLogStatement(CodeInspector.InspectedElement wizardOutputItem) {
        StringBuilder builder = new StringBuilder();

        if (wizardOutputItem.getContainingClass() != null)
            builder.append(String.format("class: %s, \" +\n \"", wizardOutputItem.getContainingClass().getName()));

        if (wizardOutputItem.getClassFields() != null && wizardOutputItem.getClassFields().length > 0) {
            builder.append("classFields: {");

            int counter = wizardOutputItem.getClassFields().length -1;
            for (PsiField field : wizardOutputItem.getClassFields()) {
                builder.append(String.format("%s = \" + String.valueOf(%s) + \"%s", field.getName(), field.getName(),
                        counter-- == 0 ? "" : ", "));
            }

            builder.append("}, \" +\n \"");
        }

        if (wizardOutputItem.getName() != null)
            builder.append(String.format("method: %s, \" +\n \"", wizardOutputItem.getName()));

        if (wizardOutputItem.getReturnType() != null)
            builder.append(String.format("returnType: %s, \" +\n \"", wizardOutputItem.getReturnType().getPresentableText()));

        if (wizardOutputItem.getParams() != null && wizardOutputItem.getParams().length > 0) {
            builder.append("params: {");

            int counter = wizardOutputItem.getParams().length-1;
            for (PsiParameter param : wizardOutputItem.getParams()) {
                builder.append(String.format("%s = \" + String.valueOf(%s) + \"%s", param.getName(), param.getName(),
                        counter-- == 0 ? "" : ", "));
            }

            if (wizardOutputItem.getOutput().getIndividualMessage() != null) builder.append("}, \" +\n \"");
            else builder.append("}");
        }

        if (wizardOutputItem.getOutput().getIndividualMessage() != null)
            builder.append(String.format("message: %s", wizardOutputItem.getOutput().getIndividualMessage()));

        return String.format("android.util.Log.d(\"%s\", \n\"%s\");", wizardOutputItem.getOutput().getTAG(), builder);
    }

    public static CharSequence getFillLogStringWithIndent(Document document, int offset, CodeInspector.InspectedElement el) {
        return "\n" + DocumentUtil.getIndent(document, offset) + el.getOutput().getResultLogString() + "\n";
    }

    /**
     * Builds the log statement and updates the preview with it.
     */
    public static void updateLogPreview(CodeInspector.InspectedElement el, JLabel tvPreview) {
        el.getOutput().setResultLogString(StatementGenerator.buildLogStatement(el));
        tvPreview.setText(String.format("<html>%s</html>", el.getOutput().getResultLogString()
                .replace("android.util.", "")));
    }

}
