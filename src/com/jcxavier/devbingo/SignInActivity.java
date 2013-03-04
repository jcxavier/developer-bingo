/*
 * Copyright (C) 2012 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.jcxavier.devbingo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;

import com.google.android.gms.plus.PlusClient;
import com.google.android.gms.plus.model.people.Person;
import com.jcxavier.devbingo.PlusClientFragment.OnSignedInListener;

/**
 * Example of signing in a user with Google+, and how to make a call to a Google+ API endpoint.
 */
public class SignInActivity extends FragmentActivity
implements View.OnClickListener, OnSignedInListener {

	public static final int REQUEST_CODE_PLUS_CLIENT_FRAGMENT = 0;
	private PlusClientFragment mSignInFragment;

	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.sign_in_activity);

		mSignInFragment =
				PlusClientFragment.getPlusClientFragment(this, MomentUtil.VISIBLE_ACTIVITIES);

		findViewById(R.id.sign_in_button).setOnClickListener(this);
		findViewById(R.id.revoke_access_button).setOnClickListener(this);
	}

	@Override
	public void onClick(final View view) {
		switch(view.getId()) {
		case R.id.sign_in_button:
			mSignInFragment.signIn(REQUEST_CODE_PLUS_CLIENT_FRAGMENT);
			break;
		case R.id.revoke_access_button:
			resetAccountState();
			mSignInFragment.revokeAccessAndDisconnect();
			break;
		}
	}

	@Override
	protected void onActivityResult(final int requestCode, final int responseCode, final Intent intent) {
		if (requestCode == 1001 && responseCode == 101) {
			resetAccountState();
			mSignInFragment.signOut();
		} else {
			mSignInFragment.handleOnActivityResult(requestCode, responseCode, intent);
		}
	}

	@Override
	public void onSignedIn(final PlusClient plusClient) {
		Person currentPerson = plusClient.getCurrentPerson();
		if (currentPerson != null) {
			Intent i = new Intent(this, GameActivity.class);
			i.putExtra("name", currentPerson.getDisplayName());
			i.putExtra("photo_url", currentPerson.getImage().getUrl());

			String brag;
			if (currentPerson.hasBraggingRights()) {
				brag = currentPerson.getBraggingRights();
			} else if (currentPerson.hasAboutMe()) {
				brag = currentPerson.getAboutMe();
			} else if (currentPerson.hasTagline()) {
				brag = currentPerson.getTagline();
			} else {
				brag = "";
			}

			i.putExtra("brag", brag);
			i.putExtra("id", currentPerson.getId());
			i.putExtra("profile_url", currentPerson.getUrl());
			startActivityForResult(i, 1001);
		} else {
			resetAccountState();
		}
	}

	private void resetAccountState() {
		// mSignInStatus.setText(getString(R.string.signed_out_status));
	}
}
