package de.ur.mi.assistant.services;

import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.project.Project;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import de.ur.mi.assistant.ui.InstructionWindow;
import de.ur.mi.assistant.utils.InternLogger;
import de.ur.mi.assistant.utils.Utils;
import de.ur.mi.assistant.utils.strings.Strings;

/**
 * ProjectService lives through  a complete session and is registered in /resources/META-INF/plugin.xml
 */
public class ProjectService implements Strings {

    private static final String TAG = ProjectService.class.getSimpleName();

    private InstructionWindow instructionWindow;
    private final ContentFactory contentFactory = ContentFactory.SERVICE.getInstance();
    private Content instructionContent;

    public static ProjectService getInstance(Project project) {
        return ServiceManager.getService(project, ProjectService.class);
    }

    /**
     * Creates the required content for adding the instructionWindow to the Debugging-Assistant ToolWindow
     *
     * @param instructionWindow current instance of the instruction window
     */
    public void setInstructionWindow(InstructionWindow instructionWindow) {
        instructionContent = contentFactory.createContent(instructionWindow.getContent(),
                INSTRUCTION_WINDOW_TITLE, false);
        this.instructionWindow = instructionWindow;
        showInstructionWindow();
    }

    /**
     * Adds the Instruction-Window to the Debugging-Assistant ToolWindow
     */
    public void showInstructionWindow() {
        try {
            instructionWindow.getToolWindow().getContentManager().addContent(instructionContent);
            instructionWindow.getToolWindow().getContentManager().setSelectedContent(instructionContent, true);
        } catch (Exception e) {
            InternLogger.error(TAG, e.getMessage());
        }
    }

}
