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
package org.talend.dataprofiler.core.model.nodes.indicator;

import org.talend.dataprofiler.core.model.nodes.indicator.impl.IndicatorCategoryNode;
import org.talend.dataprofiler.core.model.nodes.indicator.tpye.IndicatorEnum;

/**
 * This class for the indicator tree building.
 * 
 */
public final class IndicatorTreeModelBuilder {

    private IndicatorTreeModelBuilder() {
    }

    private static IndicatorCategoryNode[] indicatorCategoryNodes;

    public static IIndicatorNode[] buildIndicatorCategory() {
        if (indicatorCategoryNodes != null) {
            return indicatorCategoryNodes;
        }
        // build Basic Statistic categoryNode
        IndicatorEnum[] simpleIndicatorEnums = new IndicatorEnum[] { IndicatorEnum.RowCountIndicatorEnum,
                IndicatorEnum.NullCountIndicatorEnum, IndicatorEnum.DistinctCountIndicatorEnum,
                IndicatorEnum.UniqueIndicatorEnum, IndicatorEnum.DuplicateCountIndicatorEnum,
                IndicatorEnum.BlankCountIndicatorEnum };
        IndicatorCategoryNode simpleCategoryNode = new IndicatorCategoryNode("Simple Statistics", simpleIndicatorEnums);
//        simpleCategoryNode.creatChildren(simpleIndicatorEnums);

        // build Text statistics categoryNode
        IndicatorCategoryNode textCategoryNode = new IndicatorCategoryNode(IndicatorEnum.TextIndicatorEnum);
//        textCategoryNode.createChildren(IndicatorEnum.TextIndicatorEnum);

        // build Summary Statistic categoryNode
        IndicatorCategoryNode boxCategoryNode = new IndicatorCategoryNode(IndicatorEnum.BoxIIndicatorEnum);
//        boxCategoryNode.createChildren(IndicatorEnum.BoxIIndicatorEnum);

        // build Nominal Statistic categoryNode
        IndicatorEnum[] advanceIndicatorEnums = new IndicatorEnum[] { IndicatorEnum.ModeIndicatorEnum,
                IndicatorEnum.FrequencyIndicatorEnum };
        IndicatorCategoryNode advanceCategoryNode = new IndicatorCategoryNode("Advanced statistics", advanceIndicatorEnums);

        indicatorCategoryNodes = new IndicatorCategoryNode[] { simpleCategoryNode, textCategoryNode, boxCategoryNode,
                advanceCategoryNode };
        return indicatorCategoryNodes;
    }

}
