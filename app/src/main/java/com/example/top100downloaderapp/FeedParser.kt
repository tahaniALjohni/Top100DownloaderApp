package com.example.top100downloaderapp


import android.util.Log
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserFactory
import kotlin.collections.ArrayList


class FeedParser {
    private val tagFeedParser = "FeedParser"
    private val listApplications = ArrayList<FeedEntry>()

    fun parseXmlData(xmlData: String): Boolean {
        Log.d(tagFeedParser, "parse called with $xmlData")
        var status = true
        var inEntry = false
        var textValue = ""

        try {
            val factory = XmlPullParserFactory.newInstance()
            factory.isNamespaceAware = true
            val xpp = factory.newPullParser()
            xpp.setInput(xmlData.reader())
            var eventType = xpp.eventType
            var currentRecord = FeedEntry()
            while (eventType != XmlPullParser.END_DOCUMENT) {

                val tagName = xpp.name?.toLowerCase()
                Log.d(tagFeedParser, "parse: tag for " + tagName)
                when (eventType) {
                    XmlPullParser.START_TAG -> {
                        Log.d(tagFeedParser, "parse: Starting tag for " + tagName)
                        if (tagName == "entry") {
                            inEntry = true
                        }
                    }
                    XmlPullParser.TEXT -> textValue = xpp.text
                    XmlPullParser.END_TAG -> {
                        Log.d(tagFeedParser, "parse: Ending tag for " + tagName)
                        if (inEntry) {
                            when (tagName) {
                                "entry" -> {
                                    listApplications.add(currentRecord)
                                    inEntry = false
                                    currentRecord = FeedEntry()
                                }
                                "name" -> currentRecord.name = textValue
                            }
                        }
                    }
                }
                eventType = xpp.next()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            status = false
        }
        return status
    }

    fun getParsedList(): ArrayList<FeedEntry> {
        return listApplications
    }
}