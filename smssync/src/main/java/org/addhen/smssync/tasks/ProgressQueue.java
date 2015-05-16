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

package org.addhen.smssync.tasks;

import java.util.LinkedList;

/**
 * ProgressQueue
 *
 * Sequentially invokes next ProgressTask in the queue
 *
 * new Queue().add( new TaskOne(this), new TaskTwo(this), new TaskThree(this)) .execute();
 */
public class ProgressQueue implements ProgressCallback {

    private final LinkedList<ProgressTask> queue = new LinkedList<ProgressTask>();

    public ProgressQueue(ProgressTask... tasks) {
        add(tasks);
    }

    public ProgressQueue add(ProgressTask... tasks) {
        for (ProgressTask task : tasks) {
            task.register(this);
            queue.add(task);
        }
        return this;
    }

    public ProgressQueue add(ProgressTask task) {
        queue.add(task);
        return this;
    }

    public void execute() {
        if (queue.size() > 0) {
            queue.remove().execute((String) null);
        }
    }
}