package de.ur.mi.assistant.utils;

import com.intellij.codeInsight.template.impl.TemplateImpl;
import com.intellij.openapi.editor.Document;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiParameter;
import com.intellij.psi.PsiPrimitiveType;
import com.intellij.util.DocumentUtil;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Generates Log-String, Placeholder and Templates for all different template types
 */
public class StatementGenerator {

    public static Holder buildNormalStatement(CodeInspector.InspectedElement el) {
        StringBuilder builder = new StringBuilder();
        List<String> variables = new ArrayList<>();

        addTag(el, builder, variables);
        if (el.getName() != null) addName(el, builder, variables);
        addMessage(builder, variables);

        return new Holder(builder.toString(), variables);
    }

    public static Holder buildMethodStatement(CodeInspector.InspectedElement el) {
        StringBuilder builder = new StringBuilder();
        List<String> variables = new ArrayList<>();

        addTag(el, builder, variables);
        if (el.getName() != null) addName(el, builder, variables);
        if (el.getParams() != null) addMethodParams(el, builder, variables);
        addMessage(builder, variables);

        return new Holder(builder.toString(), variables);
    }

    public static Holder buildMethodClassLogStatement(CodeInspector.InspectedElement el) {
        StringBuilder builder = new StringBuilder();
        List<String> variables = new ArrayList<>();

        addTag(el, builder, variables);

        if (el.getContainingClass() != null)addClassName(el, builder, variables);
        if (el.getClassFields() != null) addClassFields(el, builder, variables);
        if (el.getName() != null) addName(el, builder, variables);
        if (el.getParams() != null) addMethodParams(el, builder, variables);
        addMessage(builder, variables);

        return new Holder(builder.toString(), variables);

    }

    /**
     * Adds the TAG to the Log-Statement String and appends the placeholder to variables list
     * @param el inputElement for selected caret
     * @param builder current log-statement stringBuilder
     * @param variables list of placeholder variables
     */
    public static void addTag(CodeInspector.InspectedElement el, StringBuilder builder, List<String> variables) {
        if (el.getContainingFile() != null) {
            builder.append(String.format("android.util.Log.d(\"$v%d$\",\n", 0));
            variables.add(el.getContainingFile().getName());
        } else {
            builder.append(String.format("android.util.Log.d(\"$v%d$\",\n", 0));
            variables.add("YourTAG");
        }
    }

    /**
     * Adds the class-name to the Log-Statement String and appends the placeholder to variables list
     * @param el inputElement for selected caret
     * @param builder current log-statement stringBuilder
     * @param variables list of placeholder variables
     */
    public static void addClassName(CodeInspector.InspectedElement el, StringBuilder builder, List<String> variables) {
        builder.append(String.format("\"$v%d$\"+\n", variables.size()));
        variables.add(String.format("class: %s, ", el.getContainingClass().getName()));
    }

    /**
     * Adds the class-fields to the Log-Statement String and appends the placeholder to variables list
     * @param el inputElement for selected caret
     * @param builder current log-statement stringBuilder
     * @param variables list of placeholder variables
     */
    public static void addClassFields(CodeInspector.InspectedElement el, StringBuilder builder, List<String> variables) {
        builder.append("\"classFields: {");

        int counter = el.getClassFields().length-1;
        for (PsiField field : el.getClassFields()) {

            builder.append(String.format("$v%d$\" + $v%d$ + \"%s", variables.size(), variables.size()+1,
                    counter-- == 0 ? "" : ", "));
            variables.add(String.format("%s = ", field.getName()));
            variables.add(String.format("String.valueOf(%s)", field.getName()));

        }

        builder.append("}, \"+\n");
    }

    /**
     * Adds the method-name to the Log-Statement String and appends the placeholder to variables list
     * @param el inputElement for selected caret
     * @param builder current log-statement stringBuilder
     * @param variables list of placeholder variables
     */
    public static void addName(CodeInspector.InspectedElement el, StringBuilder builder, List<String> variables) {
        builder.append(String.format("\"$v%d$\"+\n", variables.size()));
        variables.add(String.format("method: %s, ", el.getName()));
    }

    /**
     * Adds the method-params to the Log-Statement String and appends the placeholder to variables list
     * @param el inputElement for selected caret
     * @param builder current log-statement stringBuilder
     * @param variables list of placeholder variables
     */
    public static void addMethodParams(CodeInspector.InspectedElement el, StringBuilder builder, List<String> variables) {
        builder.append("\"params: {");

        int counter = el.getParams().length-1;
        for (PsiParameter param : el.getParams()) {

            builder.append(String.format("$v%d$\" + $v%d$ + \"%s", variables.size(), variables.size()+1,
                    counter-- == 0 ? "" : ", "));
            variables.add(String.format("%s = ", param.getName()));
            variables.add(String.format("String.valueOf(%s)", param.getName()));

        }

        builder.append("}, \"+\n");
    }

    /**
     * Adds the individual message placeholder to the Log-Statement String and appends the placeholder to variables list
     * closes the log-statement
     * @param builder current log-statement stringBuilder
     * @param variables list of placeholder variables
     */
    private static void addMessage(StringBuilder builder, List<String> variables) {
        builder.append(String.format("\"$v%d$\"+\n", variables.size()));
        variables.add(String.format("message: %s", ""));

        builder.append("\"\");");
    }

    public static class Holder {

        private final String statement;
        private final List<String> variables;

        public Holder(String statement, List<String> variables) {
            this.statement = statement;
            this.variables = variables;
        }

        public String getStatement() {
            return statement;
        }

        public List<String> getVariables() {
            return variables;
        }
    }

    /**
     * creates the template by inserting the placeholders from variables-list into the log-string ({@param text})
     * @param text generated log-string
     * @param variables placeholders
     * @return template instance
     */
    public static TemplateImpl createTemplate(String text, List<String> variables) {
        TemplateImpl tempTemplate = new TemplateImpl("", text, "other");

        tempTemplate.setKey("hi");
        tempTemplate.setDescription("sync edit template");
        tempTemplate.setToIndent(false);
        tempTemplate.setToReformat(false);
        tempTemplate.setToShortenLongNames(false);
        tempTemplate.setInline(false);

        final int variableCount = variables.size();
        for (int i = 0; i < variableCount; i++) {
            String value = variables.get(i);
            String name = getVariableName(i);
            String quotedValue = '"' + value + '"';
            tempTemplate.addVariable(name, quotedValue, quotedValue, true);
        }
        return tempTemplate;
    }

    private static String getVariableName(int i) {
        return 'v' + Integer.toString(i);
    }

}
