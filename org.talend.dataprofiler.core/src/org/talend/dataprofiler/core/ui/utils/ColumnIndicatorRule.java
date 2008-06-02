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
package org.talend.dataprofiler.core.ui.utils;

import org.talend.cwm.relational.TdColumn;
import org.talend.dataprofiler.core.model.ColumnIndicator;
import org.talend.dataprofiler.core.model.nodes.indicator.IIndicatorNode;
import org.talend.dataprofiler.core.model.nodes.indicator.tpye.IndicatorEnum;
import org.talend.dataquality.indicators.DataminingType;
import org.talend.utils.sql.Java2SqlType;

/**
 * DOC zqin class global comment. Detailled comment <br/>
 * 
 * $Id: talend.epf 1 2006-09-29 17:06:40Z zqin $
 * 
 */
public class ColumnIndicatorRule {

    public static boolean match(IIndicatorNode node, ColumnIndicator columnIndicator) {

        IndicatorEnum indicatorType = node.getIndicatorEnum();
        TdColumn column = columnIndicator.getTdColumn();
        int javaType = column.getJavaType();

        DataminingType dataminingType = columnIndicator.getDataminingType();

        if (indicatorType == null) {

            for (IIndicatorNode one : node.getChildren()) {
                if (match(one, columnIndicator)) {
                    return true;
                }
            }

            return false;
        }

        switch (indicatorType) {
        case RowCountIndicatorEnum:

            return true;
        case NullCountIndicatorEnum:

            return true;
        case DistinctCountIndicatorEnum:
        case UniqueIndicatorEnum:
        case DuplicateCountIndicatorEnum:

            if (dataminingType == DataminingType.NOMINAL) {
                return true;
            }

            break;
        case BlankCountIndicatorEnum:
        case TextIndicatorEnum:
        case MinLengthIndicatorEnum:
        case MaxLengthIndicatorEnum:
        case AverageLengthIndicatorEnum:

            if (Java2SqlType.isTextInSQL(javaType)) {
                if (dataminingType == DataminingType.NOMINAL || dataminingType == DataminingType.UNSTRUCTURED_TEXT) {
                    return true;
                }
            }

            break;

        case ModeIndicatorEnum:
        case FrequencyIndicatorEnum:
            if (dataminingType == DataminingType.NOMINAL || dataminingType == DataminingType.INTERVAL) {
                return true;
            }

            break;

        case MeanIndicatorEnum:
        case MedianIndicatorEnum:
        case BoxIIndicatorEnum:
        case IQRIndicatorEnum:
        case LowerQuartileIndicatorEnum:
        case UpperQuartileIndicatorEnum:
        case RangeIndicatorEnum:

            if (Java2SqlType.isNumbericInSQL(javaType) || Java2SqlType.isDateInSQL(javaType)) {
                if (dataminingType == DataminingType.INTERVAL) {
                    return true;
                }
            }
            break;

        case MinValueIndicatorEnum:
        case MaxValueIndicatorEnum:

            if (dataminingType == DataminingType.INTERVAL) {
                return true;
            }

            break;

        default:
            return false;
        }

        return false;
    }

}
