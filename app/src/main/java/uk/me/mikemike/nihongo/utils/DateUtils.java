/**
 * NihonGO!
 *
 * Copyright (c) 2017 Michael Hall <the.guitar.dude@gmail.com>
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 * 3. Neither the name of mosquitto nor the names of its
 *    contributors may be used to endorse or promote products derived from
 *    this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
package uk.me.mikemike.nihongo.utils;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by mike on 11/21/17.
 * Provides some methods to manage dates
 */
public class DateUtils {

    /**
     * Add days to a date, this will roll over months and years
     * @param d date that will be added from
     * @param days how many days to add, can be negative
     * @return a new date object representing the new date
     */
    public static Date addDaysToDate(Date d, int days){
        if(d == null) throw new IllegalArgumentException("Date cannot be null");
        Calendar c =  Calendar.getInstance();
        c.setTime(d);
        c.add(Calendar.DATE, days);
        return c.getTime();
    }

    public static long getDayDifference(Date d1, Date d2){
        if(d1 == null || d2 == null) throw new IllegalArgumentException("Either date cannot be null");
        long timedifference = d1.getTime() - d2.getTime();
        return (timedifference / (10000 * 60 * 60 * 24) ) ;
     }

}
