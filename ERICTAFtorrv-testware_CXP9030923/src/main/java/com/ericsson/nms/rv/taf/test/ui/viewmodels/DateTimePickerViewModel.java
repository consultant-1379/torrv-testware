package com.ericsson.nms.rv.taf.test.ui.viewmodels;

import com.ericsson.cifwk.taf.ui.core.SelectorType;
import com.ericsson.cifwk.taf.ui.core.UiComponent;
import com.ericsson.cifwk.taf.ui.sdk.GenericViewModel;
import com.ericsson.cifwk.taf.ui.sdk.TextBox;


public class DateTimePickerViewModel extends GenericViewModel {

    public TextBox getHourInput() {
        return this.getViewComponent(SelectorType.XPATH, "//div[contains(@class, 'ebDateTimePicker-timeHolder')][not(ancestor::div[contains(@style,'display: none')])]//table[contains(@class, 'ebSpinner')][1]//input[contains(@class, 'ebInput')]", TextBox.class);
    }

    public TextBox getMinInput() {
        return this.getViewComponent(SelectorType.XPATH, "//div[contains(@class, 'ebDateTimePicker-timeHolder')][not(ancestor::div[contains(@style,'display: none')])]//table[contains(@class, 'ebSpinner')][2]//input[contains(@class, 'ebInput')]", TextBox.class);
    }

    public TextBox getSecInput() {
        return this.getViewComponent(SelectorType.XPATH, "//div[contains(@class, 'ebDateTimePicker-timeHolder')][not(ancestor::div[contains(@style,'display: none')])]//table[contains(@class, 'ebSpinner')][3]//input[contains(@class, 'ebInput')]", TextBox.class);
    }

    public UiComponent getMonthYear() {
        return this.getViewComponent(SelectorType.XPATH, "//div[contains(@class, 'ebDatePicker-monthYear')][not(ancestor::div[contains(@style,'display: none')])]", UiComponent.class);
    }

    public UiComponent getPrevYear() {
        return this.getViewComponent(SelectorType.XPATH, "//div[contains(@class, 'ebDatePicker-prevYear')][not(ancestor::div[contains(@style,'display: none')])]", UiComponent.class);
//      return this.getViewComponent(".ebDatePicker-prevYear", UiComponent.class);
    }

    public UiComponent getNextYear() {
        return this.getViewComponent(SelectorType.XPATH, "//div[contains(@class, 'ebDatePicker-nextYear')][not(ancestor::div[contains(@style,'display: none')])]", UiComponent.class);
//        return this.getViewComponent(".ebDatePicker-nextYear", UiComponent.class);
    }

    public UiComponent getPrevMonth() {
        return this.getViewComponent(SelectorType.XPATH, "//div[contains(@class, 'ebDatePicker-prevMonth')][not(ancestor::div[contains(@style,'display: none')])]", UiComponent.class);
//        return this.getViewComponent(".ebDatePicker-prevMonth", UiComponent.class);
    }

    public UiComponent getNextMonth() {
        return this.getViewComponent(SelectorType.XPATH, "//div[contains(@class, 'ebDatePicker-nextMonth')][not(ancestor::div[contains(@style,'display: none')])]", UiComponent.class);
//        return this.getViewComponent(".ebDatePicker-nextMonth", UiComponent.class);
    }

    public UiComponent getDayPicker(String day) {
        return this.getViewComponent(SelectorType.XPATH, "//span[contains(@class, 'ebDatePicker-day')][not(ancestor::div[contains(@style,'display: none')])][normalize-space(text())='" + day + "']", UiComponent.class);
    }

    public UiComponent getOkComponent() {
        return this.getViewComponent(SelectorType.XPATH, "//button[normalize-space(text())='OK' and not(ancestor::div[contains(@style,'display: none')])]", UiComponent.class);
    }
    
}