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

package org.addhen.smssync.data.twitter;

import android.content.SharedPreferences;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Implementation of {@link SessionManager} that persists sessions
 *
 * @author Ushahidi Team <team@ushahidi.com>
 */
public class PersistedSessionManager<T extends Session> implements SessionManager<T> {

    private static final int NUM_SESSIONS = 1;

    private static final String ACTIVE_SESSION_KEY = "active_session";

    private final SharedPreferences mSharedPreferences;

    private final String mPrefKeyActiveSession;

    private final String mPrefKeySession;

    private volatile boolean restorePending = true;

    private final SerializationStrategy<T> mSerializer;

    private final ConcurrentHashMap<Long, T> mSessionMap;

    private final AtomicReference<T> mActiveSessionRef;

    public PersistedSessionManager(SharedPreferences sharedPreferences,
            SerializationStrategy<T> serializer, String prefKeyActiveSession,
            String prefKeySession) {
        this(sharedPreferences, serializer, new ConcurrentHashMap<Long, T>(NUM_SESSIONS),
                prefKeyActiveSession, prefKeySession);
    }

    private PersistedSessionManager(SharedPreferences sharedPreferences,
            SerializationStrategy<T> serializer, ConcurrentHashMap<Long, T> sessionMap,
            String prefKeyActiveSession, String prefKeySession) {
        mSharedPreferences = sharedPreferences;
        mSerializer = serializer;
        mSessionMap = sessionMap;
        mActiveSessionRef = new AtomicReference<>();
        mPrefKeyActiveSession = prefKeyActiveSession;
        mPrefKeySession = prefKeySession;
    }

    void restoreAllSessionsIfNecessary() {
        // Only restore once
        if (restorePending) {
            restoreAllSessions();
        }
    }

    private synchronized void restoreAllSessions() {
        if (restorePending) {
            restoreActiveSession();
            restoreSessions();
            restorePending = false;
        }
    }

    private void restoreSessions() {
        T session;

        final Map<String, ?> preferences = mSharedPreferences.getAll();
        for (Map.Entry<String, ?> entry : preferences.entrySet()) {
            if (isSessionPreferenceKey(entry.getKey())) {
                session = mSerializer.deserialize((String) entry.getValue());
                if (session != null) {
                    internalSetSession(session.getId(), session, false);
                }
            }
        }

    }

    private void restoreActiveSession() {
        final T session = mSerializer
                .deserialize(mSharedPreferences.getString(ACTIVE_SESSION_KEY, ""));
        if (session != null) {
            internalSetSession(session.getId(), session, false);
        }
    }

    boolean isSessionPreferenceKey(String preferenceKey) {
        return preferenceKey.startsWith(mPrefKeySession);
    }

    @Override
    public T getActiveSession() {
        return mActiveSessionRef.get();
    }


    @Override
    public void setActiveSession(T session) {
        if (session == null) {
            throw new IllegalArgumentException("Session must not be null!");
        }
        restoreAllSessionsIfNecessary();
        internalSetSession(session.getId(), session, true);
    }

    @Override
    public void clearActiveSession() {
        restoreAllSessionsIfNecessary();
        if (mActiveSessionRef.get() != null) {
            clearSession(mActiveSessionRef.get().getId());
        }
    }

    @Override
    public T getSession(long id) {
        restoreAllSessionsIfNecessary();
        return mSessionMap.get(id);
    }

    @Override
    public void setSession(long id, T session) {
        if (session == null) {
            throw new IllegalArgumentException("Session must not be null!");
        }
        restoreAllSessionsIfNecessary();
        internalSetSession(id, session, false);
    }

    @Override
    public void clearSession(long id) {
        restoreAllSessionsIfNecessary();
        if (mActiveSessionRef.get() != null && mActiveSessionRef.get().getId() == id) {
            synchronized (this) {
                mActiveSessionRef.set(null);
                mSharedPreferences.edit().remove(mPrefKeyActiveSession).commit();
            }
        }

        mSessionMap.remove(id);
    }

    @Override
    public Map<Long, T> getSessionMap() {
        restoreAllSessionsIfNecessary();
        return Collections.unmodifiableMap(mSessionMap);
    }

    private void internalSetSession(long id, T session, boolean forceUpdate) {
        mSessionMap.put(id, session);
        mSharedPreferences.edit().putString(getPrefKey(id), mSerializer.serialize(session))
                .commit();

        final T activeSession = mActiveSessionRef.get();
        if (activeSession == null || forceUpdate) {
            synchronized (this) {
                mActiveSessionRef.compareAndSet(activeSession, session);
                mSharedPreferences.edit()
                        .putString(ACTIVE_SESSION_KEY, mSerializer.serialize(session));
            }
        }
    }

    String getPrefKey(long id) {
        return mPrefKeySession + "_" + id;
    }
}
