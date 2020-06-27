package com.hcmut.admin.bktrafficsystem.ui.map;

import android.view.View;

import java.util.HashMap;
import java.util.Map;

public class BottomTab {
    private Map<Integer, View> tabs;
    private int currentTabId;

    private BottomTab(Map<Integer, View> tabs, int currentTabId) {
        this.tabs = tabs;
        this.currentTabId =currentTabId;
    }

    public void showTab(int id) {
        View currentTab = tabs.get(currentTabId);
        if (currentTab != null) {
            currentTab.setVisibility(View.GONE);
        }
        View showTab = tabs.get(id);
        if (showTab != null) {
            showTab.setVisibility(View.VISIBLE);
        }
        currentTabId = id;
    }

    public static class Builder {
        private Map<Integer, View> tabs = new HashMap<>();
        private int currentTabId;

        public Builder(int currentTabId) {
            this.currentTabId = currentTabId;
        }

        public Builder addTab(int id, View tab) {
            tabs.put(id, tab);
            return this;
        }

        public BottomTab build() {
            BottomTab bottomTab = new BottomTab(tabs, currentTabId);
            bottomTab.showTab(currentTabId);
            return bottomTab;
        }
    }
}
