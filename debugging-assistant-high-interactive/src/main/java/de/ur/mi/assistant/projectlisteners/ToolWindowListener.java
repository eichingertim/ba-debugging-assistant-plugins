package de.ur.mi.assistant.projectlisteners;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.openapi.wm.ex.ToolWindowManagerListener;
import de.ur.mi.assistant.services.ProjectService;
import org.jetbrains.annotations.NotNull;

public class ToolWindowListener implements ToolWindowManagerListener {

    /**
     * Is fired when toolWindows are opened oor closed
     *
     * This is necessary to tell the wizard window when its state changes to adjust the UI.
     */
    @Override
    public void stateChanged(@NotNull ToolWindowManager toolWindowManager) {
        ToolWindowManagerListener.super.stateChanged(toolWindowManager);
        Project project = ProjectManager.getInstance().getOpenProjects()[0];
        ProjectService projectService = ProjectService.getInstance(project);
        projectService.onToolWindowStateChanged(toolWindowManager);
    }
}
