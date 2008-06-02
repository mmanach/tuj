// ============================================================================
//
// Copyright (C) 2006-2007 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.dataprofiler.core.ui.dialog;

import org.eclipse.jface.dialogs.TrayDialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.TreeEditor;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.swt.widgets.TreeItem;
import org.talend.dataprofiler.core.PluginConstant;
import org.talend.dataprofiler.core.model.ColumnIndicator;
import org.talend.dataprofiler.core.model.nodes.indicator.IIndicatorNode;
import org.talend.dataprofiler.core.model.nodes.indicator.IndicatorTreeModelBuilder;
import org.talend.dataprofiler.core.model.nodes.indicator.tpye.IndicatorEnum;
import org.talend.dataprofiler.core.ui.dialog.composite.TooltipTree;
import org.talend.dataprofiler.core.ui.utils.ColumnIndicatorRule;
import org.talend.dataquality.helpers.IndicatorDocumentationHandler;

/**
 * This dialog use to select the indictor object for different columns.
 * 
 */
public class IndicatorSelectDialog extends TrayDialog {

    // private Object[] tdColumns;

    private static final String INDICATORITEM = "_INDICATORITEM";

    private ColumnIndicator[] columnIndicators;

    /**
     * @param parentShell
     */
    public IndicatorSelectDialog(Shell parentShell, String title, ColumnIndicator[] columnIndicators) {
        super(parentShell);
        this.columnIndicators = columnIndicators;
        this.setShellStyle(SWT.MAX | SWT.RESIZE);
    }

    /**
     * @author rli
     * 
     */
    class TreeItemContainer extends TreeItem {

        private Button[] buttons;

        private final int initialCapacity;

        public TreeItemContainer(TreeItemContainer parentItem, int style, int initialCapacity) {
            super(parentItem, style);
            this.initialCapacity = initialCapacity;
        }

        public TreeItemContainer(Tree parent, int style, int initionSize) {
            super(parent, style);
            this.initialCapacity = initionSize;
        }

        public void setButton(int index, Button button) {
            if (buttons == null) {
                buttons = new Button[initialCapacity];
            }
            buttons[index] = button;
        }

        public Button getButton(int index) {
            return buttons == null ? null : buttons[index];
        }

        /*
         * Disable the judge of subclass.
         * 
         * @see org.eclipse.swt.widgets.TreeItem#checkSubclass()
         */
        protected void checkSubclass() {
        }
    }

    /**
     * DOC rli IndicatorSelectDialog class global comment. Detailled comment.
     */
    private final class ButtonSelectionListener extends SelectionAdapter {

        private final int index;

        private final TreeItemContainer treeItemContainer;

        private final IndicatorEnum indicatorEnum;

        private final ColumnIndicator currentColumnIndicator;

        private ButtonSelectionListener(int index, TreeItemContainer treeItemContainer, IndicatorEnum indicatorEnum,
                ColumnIndicator currentColumnIndicator) {
            this.index = index;
            this.treeItemContainer = treeItemContainer;
            this.indicatorEnum = indicatorEnum;
            this.currentColumnIndicator = currentColumnIndicator;
        }

        /*
         * (non-Javadoc)
         * 
         * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
         */
        public void widgetSelected(SelectionEvent e) {
            boolean selection = ((Button) e.getSource()).getSelection();
            Button itemButton;
            if (selection) {
                itemButton = treeItemContainer.getButton(index);
                if (itemButton.isEnabled() && indicatorEnum != null) {
                    currentColumnIndicator.addTempIndicatorEnum(indicatorEnum);
                }

            } else {
                currentColumnIndicator.removeTempIndicatorEnum(indicatorEnum);
            }
            processParentSelection(treeItemContainer, selection);
            processChildrenSelection(treeItemContainer, index, selection);
        }

        /**
         * handle the parent button selection..
         * 
         * @param selection
         */
        private void processParentSelection(TreeItemContainer treeItem, boolean selection) {
            TreeItem parentItem = treeItem.getParentItem();
            if (parentItem != null && selection) {
                boolean allSelection = true;
                for (TreeItem item : parentItem.getItems()) {
                    allSelection = ((TreeItemContainer) item).getButton(index).getSelection();
                    if (!allSelection) {
                        return;
                    }
                }
                Button parentItemButton = ((TreeItemContainer) parentItem).getButton(index);
                parentItemButton.setSelection(selection);
                IndicatorEnum enumData = (IndicatorEnum) parentItemButton.getData();
                if (enumData != null) {
                    currentColumnIndicator.addTempIndicatorEnum(enumData);
                }
                processParentSelection((TreeItemContainer) parentItem, selection);
            } else if (parentItem != null && !selection) {
                Button parentItemButton = ((TreeItemContainer) parentItem).getButton(index);
                parentItemButton.setSelection(selection);
                currentColumnIndicator.removeTempIndicatorEnum((IndicatorEnum) parentItemButton.getData());
                processParentSelection((TreeItemContainer) parentItem, selection);
            }
        }

        /**
         * handle the children button selection.
         */
        private void processChildrenSelection(final TreeItemContainer treeItem, final int index, boolean selection) {
            Button itemButton;
            for (TreeItem childItem : treeItem.getItems()) {
                itemButton = ((TreeItemContainer) childItem).getButton(index);
                if (itemButton.isEnabled()) {
                    itemButton.setSelection(selection);
                } else {
                    return;
                }
                if (selection) {
                    currentColumnIndicator.addTempIndicatorEnum((IndicatorEnum) itemButton.getData());

                } else {
                    currentColumnIndicator.removeTempIndicatorEnum((IndicatorEnum) itemButton.getData());
                }

                processChildrenSelection((TreeItemContainer) childItem, index, selection);
            }
        }
    }

    protected Control createDialogArea(Composite parent) {
        Composite comp = (Composite) super.createDialogArea(parent);
        Tree tree = new TooltipTree(comp, SWT.BORDER) {

            // protected boolean isValidItem(TreeItem item) {
            // return (item != null) && (item.getData(INDICATORITEM) != null);
            // }

            protected String getItemTooltipText(TreeItem item) {
                if (item.getData(INDICATORITEM) == null) {
                    return item.getText();
                }
                IndicatorEnum indicatorEnum = (IndicatorEnum) item.getData(INDICATORITEM);
                String description = IndicatorDocumentationHandler.getLongDescription(indicatorEnum.getIndicatorClassifierId());
                return description.equals(PluginConstant.EMPTY_STRING) ? item.getText() : description;
            }

        };
        GridDataFactory.fillDefaults().align(SWT.FILL, SWT.FILL).grab(true, true).applyTo(tree);
        ((GridData) tree.getLayoutData()).widthHint = 650;
        ((GridData) tree.getLayoutData()).heightHint = 380;
        createTreeStructure(tree);
        tree.pack();
        comp.layout();
        return comp;
    }

    private void createTreeStructure(Tree tree) {

        TreeColumn[] treeColumns = createTreeColumns(tree);

        IIndicatorNode[] branchNodes = IndicatorTreeModelBuilder.buildIndicatorCategory();

        createChildrenNode(tree, null, treeColumns, branchNodes);
    }

    private void createChildrenNode(Tree tree, TreeItemContainer parentItem, TreeColumn[] treeColumns,
            IIndicatorNode[] branchNodes) {
        for (int i = 0; i < branchNodes.length; i++) {
            final TreeItemContainer treeItem;
            if (parentItem == null) {
                treeItem = new TreeItemContainer(tree, SWT.NONE, treeColumns.length);
            } else {
                treeItem = new TreeItemContainer(parentItem, SWT.NONE, treeColumns.length);
            }
            TreeEditor editor;
            for (int j = 0; j < treeColumns.length; j++) {
                if (j == 0) {
                    treeItem.setText(0, branchNodes[i].getLabel());
                    if (branchNodes[i].getIndicatorEnum() != null) {
                        treeItem.setData(INDICATORITEM, branchNodes[i].getIndicatorEnum());
                    }
                    continue;
                }
                editor = new TreeEditor(tree);
                Button checkButton = new Button(tree, SWT.CHECK);
                checkButton.setData(branchNodes[i].getIndicatorEnum());

                if (((ColumnIndicator) treeColumns[j].getData()).contains(branchNodes[i].getIndicatorEnum())) {
                    checkButton.setSelection(true);
                }
                checkButton.pack();
                editor.minimumWidth = checkButton.getSize().x;
                editor.horizontalAlignment = SWT.CENTER;
                editor.setEditor(checkButton, treeItem, j);
                final ColumnIndicator currentColumnIndicator = (ColumnIndicator) treeColumns[j].getData();
                checkButton.setEnabled(ColumnIndicatorRule.match(branchNodes[i], currentColumnIndicator));
                checkButton.addSelectionListener(new ButtonSelectionListener(j, treeItem, branchNodes[i].getIndicatorEnum(),
                        currentColumnIndicator));
                treeItem.setButton(j, checkButton);
            }
            if (branchNodes[i].hasChildren()) {
                createChildrenNode(tree, treeItem, treeColumns, branchNodes[i].getChildren());
            }
            treeItem.setExpanded(true);
        }

    }

    private TreeColumn[] createTreeColumns(Tree tree) {
        tree.setHeaderVisible(true);
        TreeColumn[] treeColumn = new TreeColumn[columnIndicators.length + 1];
        treeColumn[0] = new TreeColumn(tree, SWT.CENTER);
        treeColumn[0].setWidth(200);
        for (int i = 0; i < this.columnIndicators.length; i++) {
            treeColumn[i + 1] = new TreeColumn(tree, SWT.CENTER);
            treeColumn[i + 1].setWidth(100);
            treeColumn[i + 1].setText(columnIndicators[i].getTdColumn().getName());
            treeColumn[i + 1].setData(columnIndicators[i]);
            columnIndicators[i].copyOldIndicatorEnum();
        }
        return treeColumn;
    }

    public ColumnIndicator[] getResult() {
        return columnIndicators;
    }

}
