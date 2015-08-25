/*
 * Copyright (c) 2010 - 2015 Ushahidi Inc
 * All rights reserved
 * Contact: team@ushahidi.com
 * Website: http://www.ushahidi.com
 * GNU Lesser General Public License Usage
 * This file may be used under the terms of the GNU Lesser
 * General Public License version 3 as published by the Free Software
 * Foundation and appearing in the file LICENSE.LGPL included in the
 * packaging of this file. Please review the following information to
 * ensure the GNU Lesser General Public License version 3 requirements
 * will be met: http://www.gnu.org/licenses/lgpl.html.
 *
 * If you have questions regarding the use of this file, please contact
 * Ushahidi developers at team@ushahidi.com.
 */

package org.addhen.smssync.presentation.view.webservice;

import com.addhen.android.raiburari.presentation.ui.view.LoadDataView;

import org.addhen.smssync.presentation.model.WebServiceModel;

import java.util.List;

/**
 * List deployment view
 *
 * @author Ushahidi Team <team@ushahidi.com>
 */
public interface ListWebServiceView extends LoadDataView {

    /**
     * Render a web service model list in the UI.
     *
     * @param webServiceModel The collection of {@link WebServiceModel} that will be shown.
     */
    void renderWebServiceList(List<WebServiceModel> webServiceModel);
}
