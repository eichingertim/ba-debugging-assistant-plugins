package de.ur.mi.assistant.utils;

import com.intellij.ide.DataManager;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.project.Project;

import java.awt.*;

public final class Utils {

    public static Project getCurrentProject(Component component) {
        DataContext dataContext = DataManager.getInstance().getDataContext(component);
        return dataContext.getData(CommonDataKeys.PROJECT);
    }

}
