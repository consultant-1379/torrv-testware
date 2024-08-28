package com.ericsson.nms.rv.taf.test.networkexplorer.operators;

public enum Category {
        Private("Private"), Public("Public");

        String value;

        Category(String value) {
            this.value = value;
        }

        public String toString() {
            return this.value;
        }
    }
