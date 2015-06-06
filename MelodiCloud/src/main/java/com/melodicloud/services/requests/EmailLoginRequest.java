package com.melodicloud.services.requests;

import com.melodicloud.common.Global;
import com.melodicloud.dto.BaseDto;

/**
 * Created by AhmedSalem on 1/25/15.
 */
public class EmailLoginRequest extends BaseRequest<BaseDto> {

    private final String mEmail;
    private final String mPassword;

    public EmailLoginRequest(String email, String password) {
        mEmail = email;
        mPassword = password;
    }


    @Override
    public BaseDto loadDataFromNetwork() throws Exception {

        boolean success = Global.soundcloud.login(mEmail, mPassword);

        if (success)
            return null;
        else
            throw new IllegalArgumentException();
    }

    @Override
    public String createCacheKey() {
        return "login." + mEmail + mPassword;
    }
}
