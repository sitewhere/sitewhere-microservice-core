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
package com.sitewhere.microservice;

import ch.qos.cal10n.BaseName;
import ch.qos.cal10n.Locale;
import ch.qos.cal10n.LocaleData;

/**
 * Localized messages for microservice core classes.
 */
@BaseName("microservice")
@LocaleData({ @Locale("en_US") })
public enum MicroserviceMessages {

    INSTANCE_BOOTSTRAP_CONFIRMED,

    INSTANCE_BOOTSTRAP_MARKER_NOT_FOUND,

    INSTANCE_VERIFY_BOOTSTRAPPED,

    LIFECYCLE_STATUS_FAILED_NO_KAFKA,

    LIFECYCLE_STATUS_SEND_EXCEPTION,

    LIFECYCLE_STATUS_SENT,

    METRICS_REPORTING_DISABLED;
}
