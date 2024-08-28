/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2022
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/

package com.ericsson.oss.mediation.modeling.ebstools.etcm.validator.impl;

import java.util.HashSet;
import java.util.Set;

import com.ericsson.oss.mediation.modeling.schema.gen.net_etcm.Counter;

public final class EtcmValidatorUtil {
    private static final Set<String> VALID_SESSION_AGGREGATE_COUNTERS = new HashSet<>();

    static {
        VALID_SESSION_AGGREGATE_COUNTERS.add("pmEbsDrbExclThpDlRelativeHighVol");
        VALID_SESSION_AGGREGATE_COUNTERS.add("pmEbsDrbExclThpDlRelativeLowVol");
        VALID_SESSION_AGGREGATE_COUNTERS.add("pmEbsDrbExclThpDlShortTimeHighVol");
        VALID_SESSION_AGGREGATE_COUNTERS.add("pmEbsDrbExclThpDlShortTimeLowVol");
        VALID_SESSION_AGGREGATE_COUNTERS.add("pmEbsDrbExclThpDlSmallVol");
        VALID_SESSION_AGGREGATE_COUNTERS.add("pmEbsMacDrbThpDlMbbHighVolDistr");
        VALID_SESSION_AGGREGATE_COUNTERS.add("pmEbsMacDrbThpDlMbbLowVolDistr");
        VALID_SESSION_AGGREGATE_COUNTERS.add("pmEbsMacUeThpUlMbbHighVolDistr");
        VALID_SESSION_AGGREGATE_COUNTERS.add("pmEbsMacUeThpUlMbbLowVolDistr");
        VALID_SESSION_AGGREGATE_COUNTERS.add("pmEbsUeExclThpUlRelativeHighVol");
        VALID_SESSION_AGGREGATE_COUNTERS.add("pmEbsUeExclThpUlRelativeLowVol");
        VALID_SESSION_AGGREGATE_COUNTERS.add("pmEbsUeExclThpUlShortTimeHighVol");
        VALID_SESSION_AGGREGATE_COUNTERS.add("pmEbsUeExclThpUlShortTimeLowVol");
        VALID_SESSION_AGGREGATE_COUNTERS.add("pmEbsUeExclThpUlSmallVol");
        VALID_SESSION_AGGREGATE_COUNTERS.add("pmEbsUeExclThpVoNR");
        VALID_SESSION_AGGREGATE_COUNTERS.add("pmEbsnDrbExclThpDlRelativeHighVol");
        VALID_SESSION_AGGREGATE_COUNTERS.add("pmEbsnDrbExclThpDlRelativeLowVol");
        VALID_SESSION_AGGREGATE_COUNTERS.add("pmEbsnDrbExclThpDlShortTimeHighVol");
        VALID_SESSION_AGGREGATE_COUNTERS.add("pmEbsnDrbExclThpDlShortTimeLowVol");
        VALID_SESSION_AGGREGATE_COUNTERS.add("pmEbsnDrbExclThpDlSmallVol");
        VALID_SESSION_AGGREGATE_COUNTERS.add("pmEbsnMacDrbThpDlMbbHighVolDistr");
        VALID_SESSION_AGGREGATE_COUNTERS.add("pmEbsnMacDrbThpDlMbbLowVolDistr");
        VALID_SESSION_AGGREGATE_COUNTERS.add("pmEbsnMacUeThpUlMbbHighVolDistr");
        VALID_SESSION_AGGREGATE_COUNTERS.add("pmEbsnMacUeThpUlMbbLowVolDistr");
        VALID_SESSION_AGGREGATE_COUNTERS.add("pmEbsnUeExclThpUlRelativeHighVol");
        VALID_SESSION_AGGREGATE_COUNTERS.add("pmEbsnUeExclThpUlRelativeLowVol");
        VALID_SESSION_AGGREGATE_COUNTERS.add("pmEbsnUeExclThpUlShortTimeHighVol");
        VALID_SESSION_AGGREGATE_COUNTERS.add("pmEbsnUeExclThpUlShortTimeLowVol");
        VALID_SESSION_AGGREGATE_COUNTERS.add("pmEbsnUeExclThpUlSmallVol");
        VALID_SESSION_AGGREGATE_COUNTERS.add("pmEbsnUeExclThpVoNR");
        VALID_SESSION_AGGREGATE_COUNTERS.add("pmEbsnMacUeThpUlMbbHighVol2Distr");
        VALID_SESSION_AGGREGATE_COUNTERS.add("pmEbsnMacUeThpUlMbbLowVol2Distr");
        VALID_SESSION_AGGREGATE_COUNTERS.add("pmEbsnUeExclThpUlRelativeHighVol2");
        VALID_SESSION_AGGREGATE_COUNTERS.add("pmEbsnUeExclThpUlRelativeLowVol2");
    }

    private EtcmValidatorUtil() {
    }

    public static Set<String> getValidSessionAggregateCounters() {
        return VALID_SESSION_AGGREGATE_COUNTERS;
    }

    public static boolean isValidSessionAggregator(final Counter counter) {
        return counter.getCounterdata().getSessionaggregation() == null || VALID_SESSION_AGGREGATE_COUNTERS.contains(counter.getName().getFormat());
    }
}
