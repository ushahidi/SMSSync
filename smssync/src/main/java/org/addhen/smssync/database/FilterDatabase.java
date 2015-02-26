package org.addhen.smssync.database;

import org.addhen.smssync.models.Filter;

import java.util.List;

public interface FilterDatabase {

    void fetchAll(BaseDatabseHelper.DatabaseCallback<List<Filter>> callback);

    void fetchById(Long id, BaseDatabseHelper.DatabaseCallback<Filter> callback);

    void fetchByStatus(Filter.Status status, BaseDatabseHelper.DatabaseCallback<List<Filter>> callback);

    void put(Filter filter, BaseDatabseHelper.DatabaseCallback<Void> callback);

    void put(List<Filter> filterLists, BaseDatabseHelper.DatabaseCallback<Void> callback);

    void deleteAllBlackList(BaseDatabseHelper.DatabaseCallback<Void> callback);
    void deleteAllWhiteList(BaseDatabseHelper.DatabaseCallback<Void> callback);
    void deleteById(Long id,BaseDatabseHelper.DatabaseCallback<Void> callback);

    void total(BaseDatabseHelper.DatabaseCallback<Integer> callback);

}
