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



import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import java.io.IOException;

/**
 *@author mike
 * Collection of useful xml related functions
 */
public final class XmlUtils {

    /**
     * Reads the text between a tag
     * @param parser The source Parser
     * @return The text between the tags and the parser will have been moved on to the next tag
     * @throws IOException
     * @throws XmlPullParserException
     */
    public static String readText(XmlPullParser parser) throws IOException, XmlPullParserException {
        if(parser == null) throw new IllegalArgumentException("The parser must not be null");
        String result = "";
        if (parser.next() == XmlPullParser.TEXT) {
            result = parser.getText();
            parser.nextTag();
        }
        return result.trim();
    }


    /**
     * Get the attribute value and if it isnt null will trim it
     * @param parser The parser to use
     * @param namespace the namespace to use
     * @param value that attribute name to search for
     * @return The attribute value trimmed if it was present or null if it wasnt
     */
    public static String getAttributeAndTrim(XmlPullParser parser, String namespace, String value){
        if(parser == null) throw new IllegalArgumentException("the parser must not be null");
        String temp = parser.getAttributeValue(namespace, value);
        if(temp != null){
            temp = temp.trim();
        }
        return temp;
    }

    /**
     * Ignores an entire tag and child tags until the next tag on the same level is encountered.
     * @param parser The parser to use
     * @throws XmlPullParserException
     * @throws IOException
     */
    public static void ignore(XmlPullParser parser) throws XmlPullParserException, IOException {
        if(parser == null) throw new IllegalArgumentException("the parser must be null");
        if (parser.getEventType() != XmlPullParser.START_TAG) {
            throw new IllegalStateException();
        }
        int depth = 1;
        while (depth != 0) {
            switch (parser.next()) {
                case XmlPullParser.END_TAG:
                    depth--;
                    break;
                case XmlPullParser.START_TAG:
                    depth++;
                    break;
            }
        }

    }
}
