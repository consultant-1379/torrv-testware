package com.ericsson.nms.rv.taf.test.ui.viewmodels;

import java.util.List;

import com.ericsson.cifwk.taf.ui.core.SelectorType;
import com.ericsson.cifwk.taf.ui.core.UiComponent;
import com.ericsson.cifwk.taf.ui.sdk.*;

public class LogviewerViewModel extends GenericViewModel {

    public TextBox getSearchInput() {
        return getViewComponent(".eaLogViewer-rSearch-searchInput",
                TextBox.class);
    }

    public Button getSearchButton() {
        return getViewComponent(".eaLogViewer-rSearch-searchIcon", Button.class);
    }

    public UiComponent getTable() {
        return getViewComponent(".eaLogViewer-wTable-table", UiComponent.class);
    }

    public UiComponent getTableHeader() {
        return getViewComponent(".eaLogViewer-wTable-table thead tr",
                UiComponent.class);
    }

    public UiComponent getRefreshComponent() {
        return getViewComponent(".eaLogViewer-rTools-actionsRefresh",
                UiComponent.class);
    }

    public UiComponent getSelectColumnComponent() {
        return getViewComponent(".eaLogViewer-wColumnSettings-icon",
                UiComponent.class);
    }

    public List<UiComponent> getColumnsCandidates() {
        return getViewComponents(".eaLogViewer-wColumnSettingsItem",
                UiComponent.class);
    }

    public List<UiComponent> getTableHeaders() {
        return getViewComponents(".eaLogViewer-wTable-table thead tr th",
                UiComponent.class);
    }

    public List<UiComponent> getTableRows() {
        return this.getViewComponents(".eaLogViewer-wTable-table tbody tr",
                UiComponent.class);
    }

    public List<UiComponent> getTableRowsSummary() {
        return this.getViewComponents(".eaLogViewer-wTable-tableItem",
                UiComponent.class);
    }

    public UiComponent getStartTimeInput() {
        return this
                .getViewComponent(
                        SelectorType.XPATH,
                        "//*[contains(@class,'eaLogViewer-rSearch-range-wDatepickerInput-input')][contains(@placeholder,'Start time')]",
                        UiComponent.class);
    }

    public UiComponent getEndTimeInput() {
        return this
                .getViewComponent(
                        SelectorType.XPATH,
                        "//*[contains(@class,'eaLogViewer-rSearch-range-wDatepickerInput-input')][contains(@placeholder,'End time')]",
                        UiComponent.class);
    }

    public UiComponent getDateInputDialog() {
        return this.getViewComponent(
                ".eaLogViewer-wDatepickerInputDialog-cellContent",
                UiComponent.class);
    }

    public UiComponent getHourInput() {
        return this
                .getViewComponent(
                        SelectorType.XPATH,
                        "//div[contains(@class, 'ebDateTimePicker-timeHolder')]//table[contains(@class, 'ebSpinner')][1]//input[contains(@class, 'ebInput')]",
                        UiComponent.class);
    }

    public UiComponent getMinInput() {
        return this
                .getViewComponent(
                        SelectorType.XPATH,
                        "//div[contains(@class, 'ebDateTimePicker-timeHolder')]//table[contains(@class, 'ebSpinner')][2]//input[contains(@class, 'ebInput')]",
                        UiComponent.class);
    }

    public UiComponent getSecInput() {
        return this
                .getViewComponent(
                        SelectorType.XPATH,
                        "//div[contains(@class, 'ebDateTimePicker-timeHolder')]//table[contains(@class, 'ebSpinner')][3]//input[contains(@class, 'ebInput')]",
                        UiComponent.class);
    }

    public UiComponent getMonthYear() {
        return this
                .getViewComponent(
                        SelectorType.XPATH,
                        "//div[contains(@class, 'eaLogViewer-wDatepickerInputDialog-cellContent')]//div[contains(@class, 'ebDatePicker-monthYear')]",
                        UiComponent.class);
    }

    public UiComponent getPrevYear() {
        return this.getViewComponent(".ebDatePicker-prevYear",
                UiComponent.class);
    }

    public UiComponent getNextYear() {
        return this.getViewComponent(".ebDatePicker-nextYear",
                UiComponent.class);
    }

    public UiComponent getPrevMonth() {
        return this.getViewComponent(".ebDatePicker-prevMonth",
                UiComponent.class);
    }

    public UiComponent getNextMonth() {
        return this.getViewComponent(".ebDatePicker-nextMonth",
                UiComponent.class);
    }

    public UiComponent getDayPicker(String day) {
        return this.getViewComponent(SelectorType.XPATH,
                "//span[contains(@class, 'ebDatePicker-day')][normalize-space(text())='"
                        + day + "']", UiComponent.class);
    }

    public UiComponent getButtonToClearSearchText() {
        return this
                .getViewComponent(
                        SelectorType.XPATH,
                        "//div[contains(@class, 'eaLogViewer-rSearch')]//form/span[contains(@class, 'eaLogViewer-rSearch-searchCancel')]",
                        UiComponent.class);
    }

    public UiComponent getOkComponent() {
        return this
                .getViewComponent(
                        SelectorType.XPATH,
                        "//div[contains(@class, 'wDatepickerInputDialog-cellActions')]/button[normalize-space(text())='OK']",
                        UiComponent.class);
    }
}