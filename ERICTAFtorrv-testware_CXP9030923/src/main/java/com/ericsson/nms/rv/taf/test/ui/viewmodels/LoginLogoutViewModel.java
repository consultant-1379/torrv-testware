package com.ericsson.nms.rv.taf.test.ui.viewmodels;

import com.ericsson.cifwk.taf.ui.core.UiComponentMapping;
import com.ericsson.cifwk.taf.ui.sdk.*;

public class LoginLogoutViewModel extends GenericViewModel {

	@UiComponentMapping(id = "loginUsername")
	private TextBox usernameInput;

	@UiComponentMapping(id = "loginPassword")
	private TextBox passwordInput;

	@UiComponentMapping(id = "submit")
	private Button submitButton;

	@UiComponentMapping(".eaContainer-LogoutButton-link")
	private Link logoutButton;

	@UiComponentMapping(id = "loginNoticeOk")
	private Button noticeOkButton;

	public Button getNoticeOkButton() {
		return noticeOkButton;
	}

	public TextBox getUsernameInput() {
		return usernameInput;
	}

	public TextBox getPasswordInput() {
		return passwordInput;
	}

	public Button getSubmitButton() {
		return submitButton;
	}

	public Link getLogoutButton() {
		return logoutButton;
	}
}