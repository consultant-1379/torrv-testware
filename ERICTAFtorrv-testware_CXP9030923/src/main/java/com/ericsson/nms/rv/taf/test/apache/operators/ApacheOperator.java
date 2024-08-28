package com.ericsson.nms.rv.taf.test.apache.operators;

import com.ericsson.nms.rv.taf.test.apache.operators.dto.ApacheResponse;

/**
 * Created by ewandaf on 21/05/14.
 */
public interface ApacheOperator {
    ApacheResponse login(String userId, String password);

    ApacheResponse logout();
}
