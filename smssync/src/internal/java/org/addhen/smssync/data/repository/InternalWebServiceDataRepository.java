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

package org.addhen.smssync.data.repository;

import org.addhen.smssync.data.entity.mapper.WebServiceDataMapper;
import org.addhen.smssync.domain.entity.SyncSchemeEntity;
import org.addhen.smssync.domain.entity.WebServiceEntity;
import org.addhen.smssync.domain.repository.WebServiceRepository;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import rx.Observable;

/**
 * @author Ushahidi Team <team@ushahidi.com>
 */
@Singleton
public class InternalWebServiceDataRepository implements WebServiceRepository {

    private WebServiceDataMapper mWebServiceDataMapper;

    @Inject
    public InternalWebServiceDataRepository(WebServiceDataMapper webServiceDataMapper) {
        mWebServiceDataMapper = webServiceDataMapper;
    }

    @Override
    public Observable<List<WebServiceEntity>> getByStatus(WebServiceEntity.Status status) {
        if (status.equals(WebServiceEntity.Status.ENABLED)) {
            return Observable.defer(() -> {
                List<WebServiceEntity> webServiceEntities = new ArrayList<>();
                webServiceEntities.add(getWebServiceEntityTwo());
                return Observable.just(webServiceEntities);
            });
        }

        return Observable.defer(() -> {
            List<WebServiceEntity> webServiceEntities = new ArrayList<>();
            webServiceEntities.add(getWebServiceEntityThree());
            return Observable.just(webServiceEntities);
        });
    }

    @Override
    public List<WebServiceEntity> syncGetByStatus(WebServiceEntity.Status status) {
        if (status.equals(WebServiceEntity.Status.ENABLED)) {
            List<WebServiceEntity> webServiceEntities = new ArrayList<>();
            webServiceEntities.add(getWebServiceEntityTwo());
            return webServiceEntities;

        }

        List<WebServiceEntity> webServiceEntities = new ArrayList<>();
        webServiceEntities.add(getWebServiceEntityThree());
        return webServiceEntities;

    }

    @Override
    public Observable<Boolean> testWebService(WebServiceEntity webServiceEntity) {
        return Observable.defer(() -> {
            return Observable.just(Boolean.TRUE);
        });
    }

    @Override
    public Observable<List<WebServiceEntity>> getEntities() {
        return Observable.defer(() -> {
            return Observable.just(getWebServiceEntities());
        });
    }

    @Override
    public Observable<WebServiceEntity> getEntity(Long entityId) {
        return Observable.defer(() -> {
            return Observable.just(getWebServiceEntityOne());
        });
    }

    @Override
    public Observable<Long> addEntity(WebServiceEntity entity) {
        return Observable.defer(() -> {
            return Observable.just(1l);
        });
    }

    @Override
    public Observable<Long> updateEntity(WebServiceEntity entity) {
        return Observable.defer(() -> {
            return Observable.just(1l);
        });
    }

    @Override
    public Observable<Long> deleteEntity(Long id) {
        return Observable.defer(() -> {
            return Observable.just(1l);
        });
    }

    private List<WebServiceEntity> getWebServiceEntities() {
        List<WebServiceEntity> webServiceEntities = new ArrayList<>();
        webServiceEntities.add(getWebServiceEntityOne());
        webServiceEntities.add(getWebServiceEntityTwo());
        webServiceEntities.add(getWebServiceEntityThree());
        return webServiceEntities;
    }

    private WebServiceEntity getWebServiceEntityOne() {
        WebServiceEntity webServiceEntity = new WebServiceEntity();
        webServiceEntity._id = 1l;
        webServiceEntity.setUrl("http://ushahidi-platform-api-release.herokuapp.com/smssync");
        webServiceEntity.setTitle("Demo title");
        webServiceEntity.setKeywords("Nairobi, Austin, Auckland");
        webServiceEntity.setSecret("smssync");
        webServiceEntity.setKeywordStatus(WebServiceEntity.KeywordStatus.ENABLED);
        webServiceEntity.setStatus(WebServiceEntity.Status.ENABLED);
        webServiceEntity.setSyncScheme(new SyncSchemeEntity());
        return webServiceEntity;
    }

    private WebServiceEntity getWebServiceEntityTwo() {
        WebServiceEntity webServiceEntity = new WebServiceEntity();
        webServiceEntity._id = 2l;
        webServiceEntity.setUrl("http://ushahidi-platform-api-release.herokuapp.com/smssync");
        webServiceEntity.setTitle("Demo title Two");
        webServiceEntity.setKeywords("Lion, Rhino, Leopard, Buffalo");
        webServiceEntity.setSecret("smssync");
        webServiceEntity.setStatus(WebServiceEntity.Status.ENABLED);
        webServiceEntity.setSyncScheme(new SyncSchemeEntity());
        return webServiceEntity;
    }

    private WebServiceEntity getWebServiceEntityThree() {
        WebServiceEntity webServiceEntity = new WebServiceEntity();
        webServiceEntity._id = 3l;
        webServiceEntity.setUrl("http://ushahidi-platform-api-release.herokuapp.com/smssync");
        webServiceEntity.setTitle("Demo title Three");
        webServiceEntity.setSecret("smssync");
        webServiceEntity.setStatus(WebServiceEntity.Status.ENABLED);
        webServiceEntity.setSyncScheme(new SyncSchemeEntity());
        return webServiceEntity;
    }
}
