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

package org.addhen.smssync.presentation.model.mapper;

import org.addhen.smssync.data.entity.WebService;
import org.addhen.smssync.domain.entity.SyncSchemeEntity;
import org.addhen.smssync.domain.entity.WebServiceEntity;
import org.addhen.smssync.presentation.model.SyncSchemeModel;
import org.addhen.smssync.presentation.model.WebServiceModel;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

/**
 * @author Ushahidi Team <team@ushahidi.com>
 */
public class WebServiceModelDataMapper {

    @Inject
    public WebServiceModelDataMapper() {
        // Do nothing
    }

    public WebServiceEntity map(WebServiceModel webService) {
        WebServiceEntity webServiceEntity = null;
        if (webService != null) {
            webServiceEntity = new WebServiceEntity();
            webServiceEntity._id = webService._id;
            webServiceEntity.setTitle(webService.getTitle());
            webServiceEntity.setUrl(webService.getUrl());
            webServiceEntity.setSecret(webService.getSecret());
            webServiceEntity
                    .setStatus(WebServiceEntity.Status.valueOf(webService.getStatus().name()));
            webServiceEntity
                    .setSyncScheme(new SyncSchemeEntity(webService.getSyncScheme().toJSONString()));
        }
        return webServiceEntity;
    }

    public WebServiceModel map(WebService webServiceEntity) {
        WebServiceModel webService = null;
        if (webServiceEntity != null) {
            webService = new WebServiceModel();
            webService._id = webServiceEntity._id;
            webService.setSecret(webServiceEntity.getSecret());
            webService.setTitle(webServiceEntity.getTitle());
            webService.setUrl(webServiceEntity.getUrl());
            webService
                    .setStatus(WebServiceModel.Status.valueOf(webServiceEntity.getStatus().name()));
            webService
                    .setSyncScheme(
                            new SyncSchemeModel(webServiceEntity.getSyncScheme().toJSONString()));
        }
        return webService;
    }

    public List<WebServiceModel> map(List<WebService> webServices) {
        List<WebServiceModel> webServiceEntities = new ArrayList<>();
        WebServiceModel webServiceEntity;
        for (WebService webService : webServices) {
            webServiceEntity = map(webService);
            if (webServiceEntity != null) {
                webServiceEntities.add(webServiceEntity);
            }
        }
        return webServiceEntities;
    }
}
