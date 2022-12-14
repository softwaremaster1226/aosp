/*
 * Copyright (C) 2015 The Android Open Source Project
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

package com.kr.messaging.sms;

/**
 * A generic Exception for errors in sending SMS
 */
class SmsException extends Exception {
    private static final long serialVersionUID = 1L;

    /**
     * Creates a new SmsException.
     */
    public SmsException() {
        super();
    }

    /**
     * Creates a new SmsException with the specified detail message.
     *
     * @param message the detail message.
     */
    public SmsException(String message) {
        super(message);
    }

    /**
     * Creates a new SmsException with the specified cause.
     *
     * @param cause the cause.
     */
    public SmsException(Throwable cause) {
        super(cause);
    }

    /**
     * Creates a new SmsException with the specified detail message and cause.
     *
     * @param message the detail message.
     * @param cause the cause.
     */
    public SmsException(String message, Throwable cause) {
        super(message, cause);
    }
}
