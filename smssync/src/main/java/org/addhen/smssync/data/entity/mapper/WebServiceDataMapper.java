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

package org.addhen.smssync.data.entity.mapper;

import org.addhen.smssync.data.entity.SyncScheme;
import org.addhen.smssync.data.entity.WebService;
import org.addhen.smssync.domain.entity.SyncSchemeEntity;
import org.addhen.smssync.domain.entity.WebServiceEntity;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

/**
 * @author Ushahidi Team <team@ushahidi.com>
 */
public class WebServiceDataMapper {

    @Inject
    public WebServiceDataMapper() {
        // Do nothing
    }

    public WebServiceEntity map(WebService webService) {
        WebServiceEntity webServiceEntity = null;
        if (webService != null) {
            webServiceEntity = new WebServiceEntity();
            webServiceEntity._id = webService._id;
            webServiceEntity.setSecret(webService.getSecret());
            webServiceEntity.setTitle(webService.getTitle());
            webServiceEntity.setUrl(webService.getUrl());
            webServiceEntity.setKeywords(webService.getKeywords());
            webServiceEntity.setStatus(map(webService.getStatus()));
            webServiceEntity.setKeywordStatus(map(webService.getKeywordStatus()));
            webServiceEntity.setSyncScheme(
                    new SyncSchemeEntity(webService.getSyncScheme().toJSONString()));
        }
        return webServiceEntity;
    }

    public WebService map(WebServiceEntity webServiceEntity) {
        WebService webService = null;
        if (webServiceEntity != null) {
            webService = new WebService();
            webService._id = webServiceEntity._id;
            webService.setSecret(webServiceEntity.getSecret());
            webService.setTitle(webServiceEntity.getTitle());
            webService.setUrl(webServiceEntity.getUrl());
            webService.setKeywords(webServiceEntity.getKeywords());
            webService.setStatus(map(webServiceEntity.getStatus()));
            webService
                    .setSyncScheme(new SyncScheme(webServiceEntity.getSyncScheme().toJSONString()));
            webService.setKeywordStatus(map(webServiceEntity.getKeywordStatus()));
        }
        return webService;
    }

    public List<WebServiceEntity> map(List<WebService> webServices) {
        List<WebServiceEntity> webServiceEntities = new ArrayList<>();
        WebServiceEntity webServiceEntity;
        for (WebService webService : webServices) {
            webServiceEntity = map(webService);
            if (webServiceEntity != null) {
                webServiceEntities.add(webServiceEntity);
            }
        }
        return webServiceEntities;
    }

    public WebService.Status map(WebServiceEntity.Status status) {
        if (status != null) {
            return WebService.Status.valueOf(status.name());
        }
        return WebService.Status.DISABLED;
    }

    public WebServiceEntity.Status map(WebService.Status status) {
        if (status != null) {
            return WebServiceEntity.Status.valueOf(status.name());
        }
        return WebServiceEntity.Status.DISABLED;
    }

    public WebService.KeywordStatus map(WebServiceEntity.KeywordStatus keywordStatus) {
        if (keywordStatus != null) {
            return WebService.KeywordStatus.valueOf(keywordStatus.name());
        }
        return WebService.KeywordStatus.DISABLED;
    }

    public WebServiceEntity.KeywordStatus map(WebService.KeywordStatus keywordStatus) {
        if (keywordStatus != null) {
            return WebServiceEntity.KeywordStatus.valueOf(keywordStatus.name());
        }
        return WebServiceEntity.KeywordStatus.DISABLED;
    }
}
