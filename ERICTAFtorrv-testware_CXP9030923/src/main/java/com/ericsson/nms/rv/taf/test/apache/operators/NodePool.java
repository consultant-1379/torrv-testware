package com.ericsson.nms.rv.taf.test.apache.operators;

import java.util.concurrent.ConcurrentLinkedQueue;

import com.ericsson.nms.rv.taf.test.apache.operators.dto.Node;

/**
 * Created by ewandaf on 07/07/14.
 */
public final class NodePool {
    private final ConcurrentLinkedQueue<Node> pool = new ConcurrentLinkedQueue<>();

    // private Lock lock = new ReentrantLock();

    public Node borrowObject() {
        return pool.poll();
    }

    public void returnObject(Node dataRecord) {
        if (dataRecord == null) {
            return;
        } else {
            pool.offer(dataRecord);
        }
    }

    public int size() {
        return pool.size();
    }

    public String toString() {
        return "[" + super.toString() + "]";
    }
}
