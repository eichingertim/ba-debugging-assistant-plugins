package de.ur.mi.assistant.services;

import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import de.ur.mi.assistant.ui.InstructionWindow;
import de.ur.mi.assistant.ui.WizardWindow;
import de.ur.mi.assistant.utils.InternLogger;
import de.ur.mi.assistant.utils.strings.Strings;

/**
 * ProjectService lives through  a complete session and is registered in /resources/META-INF/plugin.xml
 */
public class ProjectService implements Strings {

    private static final String TAG = ProjectService.class.getSimpleName();

    private InstructionWindow instructionWindow;
    private WizardWindow wizardWindow;

    private final ContentFactory contentFactory = ContentFactory.SERVICE.getInstance();
    private Content wizardContent;
    private Content instructionContent;

    public static ProjectService getInstance(Project project) {
        return ServiceManager.getService(project, ProjectService.class);
    }

    public WizardWindow getWizardWindow() {
        return wizardWindow;
    }

    /**
     * Creates the required content for adding the wizardWindow to the Debugging-Assistant ToolWindow
     *
     * @param wizardWindow current instance of the wizard window
     */
    public void setWizardWindow(WizardWindow wizardWindow) {
        wizardContent = contentFactory.createContent(wizardWindow.getContent(), WIZARD_WINDOW_TITLE, false);
        this.wizardWindow = wizardWindow;
    }

    /**
     * Creates the required content for adding the instructionWindow to the Debugging-Assistant ToolWindow
     *
     * @param instructionWindow current instance of the instruction window
     */
    public void setInstructionWindow(InstructionWindow instructionWindow) {
        instructionContent = contentFactory.createContent(instructionWindow.getContent(), INSTRUCTION_WINDOW_TITLE, false);
        this.instructionWindow = instructionWindow;
        showInstructionWindow();
    }

    /**
     * Adds the Instruction-Window to the Debugging-Assistant ToolWindow
     */
    public void showInstructionWindow() {
        instructionWindow.getToolWindow().getContentManager().addContent(instructionContent);
        instructionWindow.getToolWindow().getContentManager().setSelectedContent(instructionContent, true);
    }

    /**
     * Adds the Wizard-Window to the Debugging-Assistant ToolWindow
     */
    public void showWizard(Project project) {
        ToolWindow toolWindow = ToolWindowManager.getInstance(project).getToolWindow(DEBUGGING_ASSISTANT_WINDOW_TITLE);
        if (toolWindow != null) {
            toolWindow.show();
            toolWindow.getContentManager().addContent(wizardContent);
            toolWindow.getContentManager().setSelectedContent(wizardContent, true);
        }

    }

    /**
     * Removes the Wizard-Window from the Debugging-Assistant ToolWindow
     */
    public void removeWizard() {
        ToolWindow toolWindow = getWizardWindow().getToolWindow();
        toolWindow.getContentManager().setSelectedContent(instructionContent, true);
        toolWindow.getContentManager().removeContent(wizardContent, true);
    }

    public void onToolWindowStateChanged(ToolWindowManager toolWindowManager) {
        try {
            getWizardWindow().onStateChanged(toolWindowManager);
        } catch (Exception e) {
            InternLogger.error(TAG, e.getMessage());
        }
    }



}
