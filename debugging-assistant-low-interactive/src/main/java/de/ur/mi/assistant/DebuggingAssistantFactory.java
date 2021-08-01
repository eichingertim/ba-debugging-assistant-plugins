package de.ur.mi.assistant;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import de.ur.mi.assistant.services.ProjectService;
import de.ur.mi.assistant.ui.InstructionWindow;
import de.ur.mi.assistant.utils.Utils;
import org.jetbrains.annotations.NotNull;

public class DebuggingAssistantFactory implements ToolWindowFactory {

    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {

        InstructionWindow instructionWindow = new InstructionWindow(toolWindow);

        ProjectService projectService = ProjectService.getInstance(project);
        projectService.setInstructionWindow(instructionWindow);
    }

}