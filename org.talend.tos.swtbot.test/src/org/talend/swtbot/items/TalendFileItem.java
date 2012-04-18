// ============================================================================
//
// Copyright (C) 2006-2012 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.swtbot.items;

import static org.eclipse.swtbot.swt.finder.matchers.WidgetMatcherFactory.allOf;
import static org.eclipse.swtbot.swt.finder.matchers.WidgetMatcherFactory.widgetOfType;
import static org.eclipse.swtbot.swt.finder.matchers.WidgetMatcherFactory.withStyle;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

import junit.framework.Assert;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swtbot.swt.finder.exceptions.WidgetNotFoundException;
import org.eclipse.swtbot.swt.finder.waits.DefaultCondition;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotButton;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotShell;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;
import org.hamcrest.Matcher;
import org.talend.swtbot.Utilities;
import org.talend.swtbot.Utilities.TalendItemType;

/**
 * DOC fzhong class global comment. Detailled comment
 */
public class TalendFileItem extends TalendMetadataItem {

    protected String filePath;

    private boolean isSetHeadingRowAsColumnName = false;

    public TalendFileItem(TalendItemType itemType, String filePath) {
        super(itemType);
        setFilePath(filePath);
        setExpectResultFromFile(filePath + ".result");
    }

    public TalendFileItem(String itemName, TalendItemType itemType, String filePath) {
        super(itemName, itemType);
        setFilePath(filePath);
        setExpectResultFromFile(filePath + ".result");
    }

    public String getFilePath() {
        return this.filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public File getSourceFile() throws IOException, URISyntaxException {
        return Utilities.getFileFromCurrentPluginSampleFolder(filePath);
    }

    public File getSourceFileOfResult() throws IOException, URISyntaxException {
        return Utilities.getFileFromCurrentPluginSampleFolder(filePath + ".result");
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public void finishCreationWizard(final SWTBotShell shell) {
        try {
            gefBot.waitUntil(new DefaultCondition() {

                public boolean test() throws Exception {
                    Matcher matcher = allOf(widgetOfType(Button.class), withStyle(SWT.PUSH, null));
                    SWTBotButton button = new SWTBotButton((Button) gefBot.widget(matcher, gefBot.cTabItem("Preview").widget));
                    boolean isPreviewDone = "Refresh Preview".equals(button.getText());
                    boolean isPreviewFail = gefBot.cTabItem("Output").isActive();
                    return (isPreviewDone || isPreviewFail);
                }

                public String getFailureMessage() {
                    return "refresh preview did not finish";
                }
            }, 60000);

            if (gefBot.cTabItem("Output").isActive()) {
                throw new Exception("Refresh preview fail - " + gefBot.styledText().getText());
            }

            if (isSetHeadingRowAsColumnName) {
                gefBot.checkBox("Set heading row as column names").click();
            }
            gefBot.button("Next >").click();
            gefBot.button("Finish").click();
        } catch (WidgetNotFoundException wnfe) {
            Assert.fail(wnfe.getCause().getMessage());
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        } finally {
            shell.close();
        }

        SWTBotTreeItem newItem = null;
        try {
            newItem = parentNode.expand().select(itemName + " 0.1");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            Assert.assertNotNull("item is not created", newItem);
        }

        setItem(parentNode.getNode(itemName + " 0.1"));
    }

    public void setHeadingRowAsColumnName() {
        isSetHeadingRowAsColumnName = true;
    }
}
