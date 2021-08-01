package de.ur.mi.assistant.utils;

import com.intellij.lang.jvm.JvmModifier;
import com.intellij.openapi.editor.Caret;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.DocumentUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Searches the PSITree (Code) to find all information required to build a InspectedElement from a selected caret
 */
public class CodeInspector {

    /**
     * Generates the InspectedElement from a selected caret inside a PsiFile
     *
     * @param caret selected position in editor
     * @param file  which is opened in the editor
     * @return generated inputElement (InspectedElement)
     */
    public static InspectedElement prepareInspectedElement(Caret caret, PsiFile file) {
        PsiElement element = file.findElementAt(caret.getOffset());
        PsiMethod method = PsiTreeUtil.getParentOfType(element, PsiMethod.class);

        if (method == null) return null;

        PsiClass psiClass = PsiTreeUtil.getTopmostParentOfType(element, PsiClass.class);

        if (psiClass == null) return null;

        InspectedElement result = new InspectedElement()
                .setInspectedMethod(method)
                .setName(method.getName())
                .setParams(method.getParameterList().getParameters())
                .setContainingFile(file)
                .setClassFields(psiClass.getFields())
                .setContainingClass(psiClass)
                .setContainingProject(method.getProject());

        //If Parent-Method is static then only static class fields can be used
        if (result.getInspectedMethod().hasModifier(JvmModifier.STATIC)) {
            List<PsiField> resultFields = new ArrayList<>();
            for (PsiField psiField : result.getClassFields()) {
                if (psiField.hasModifier(JvmModifier.STATIC)) resultFields.add(psiField);
            }
            result.setClassFields(resultFields.toArray(new PsiField[0]));
        }
        return result;
    }

    /**
     * Model for Information required to build a Log-Statement for a specific selected caret position
     */
    public static class InspectedElement {

        private PsiMethod inspectedMethod;
        private PsiFile containingFile;
        private PsiClass containingClass;
        private PsiField[] classFields;

        private Project containingProject;

        private String name;
        private PsiParameter[] params;


        public PsiFile getContainingFile() {
            return containingFile;
        }

        public InspectedElement setContainingFile(PsiFile containingFile) {
            this.containingFile = containingFile;
            return this;
        }

        public PsiClass getContainingClass() {
            return containingClass;
        }

        public InspectedElement setContainingClass(PsiClass containingClass) {
            this.containingClass = containingClass;
            return this;
        }

        public PsiField[] getClassFields() {
            return classFields;
        }

        public InspectedElement setClassFields(PsiField[] classFields) {
            this.classFields = classFields;
            return this;
        }

        public Project getContainingProject() {
            return containingProject;
        }

        public InspectedElement setContainingProject(Project containingProject) {
            this.containingProject = containingProject;
            return this;
        }

        public String getName() {
            return name;
        }

        public InspectedElement setName(String name) {
            this.name = name;
            return this;
        }

        public PsiParameter[] getParams() {
            return params;
        }

        public InspectedElement setParams(PsiParameter[] params) {
            this.params = params;
            return this;
        }

        public PsiMethod getInspectedMethod() {
            return inspectedMethod;
        }

        public InspectedElement setInspectedMethod(PsiMethod inspectedMethod) {
            this.inspectedMethod = inspectedMethod;
            return this;
        }


    }

}
