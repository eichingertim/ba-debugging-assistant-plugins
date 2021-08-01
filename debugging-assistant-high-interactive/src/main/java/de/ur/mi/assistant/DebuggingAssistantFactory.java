package de.ur.mi.assistant;

import com.intellij.ide.projectView.ProjectView;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.openapi.wm.ToolWindowManager;
import de.ur.mi.assistant.services.ProjectService;
import de.ur.mi.assistant.ui.InstructionWindow;
import de.ur.mi.assistant.ui.WizardWindow;
import org.jetbrains.annotations.NotNull;

public class DebuggingAssistantFactory implements ToolWindowFactory {

    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {

        InstructionWindow instructionWindow = new InstructionWindow(toolWindow);
        WizardWindow wizardWindow = new WizardWindow(toolWindow);

        ProjectService projectService = ProjectService.getInstance(project);
        projectService.setInstructionWindow(instructionWindow);
        projectService.setWizardWindow(wizardWindow);

        openProjectStructure(project);
    }

    private void openProjectStructure(Project project) {

        ToolWindow toolWindow = ToolWindowManager.getInstance(project).getToolWindow("Project");
        if (toolWindow != null) {
            toolWindow.show();
            ProjectView.getInstance(project).setShowModules("Android", true);
        }
    }

}