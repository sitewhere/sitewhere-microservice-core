/**
 * Copyright © 2014-2021 The SiteWhere Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.sitewhere.grpc.client;

import ch.qos.cal10n.BaseName;
import ch.qos.cal10n.Locale;
import ch.qos.cal10n.LocaleData;

/**
 * Localized messages for gRPC client classes.
 */
@BaseName("grpc-client")
@LocaleData({ @Locale("en_US") })
public enum GrpcClientMessages {

    API_CHANNEL_ALREADY_ACTIVE,

    API_CHANNEL_EXCEPTION_ON_CREATE,

    API_CHANNEL_FAILED_INIT,

    API_CHANNEL_FAILED_START,

    API_CHANNEL_INIT_AFTER_MS_ADDED,

    API_CHANNEL_INTERRUPTED_WAITING_FOR_MS,

    API_CHANNEL_REMOVED_AFTER_MS_REMOVED,

    API_CHANNEL_UNABLE_TO_INIT,

    API_CHANNEL_UNABLE_TO_REMOVE,

    API_CHANNEL_UNHANDLED_EXCEPTION_ON_CREATE,

    API_CHANNEL_WAITING_FOR_AVAILABLE;
}