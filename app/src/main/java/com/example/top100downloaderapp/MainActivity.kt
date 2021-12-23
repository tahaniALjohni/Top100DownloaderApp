package com.example.top100downloaderapp


import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.*
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL

class FeedEntry {
    var name: String = ""

    override fun toString(): String {
        return """
            name = $name
           """.trimIndent()
    }
}

class MainActivity : AppCompatActivity() {
    private val tAGMainActivity = "MainActivity"
    lateinit var tvFeed : TextView
    lateinit var rvMainFeed :RecyclerView
    lateinit var feedBtn:Button
    lateinit var itemsList:ArrayList<String>
    var feedURL = "http://ax.itunes.apple.com/WebObjects/MZStoreServices.woa/ws/RSS/topfreeapplications/limit=10/xml"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Log.d(tAGMainActivity, "onCreate called")
        tvFeed = findViewById<TextView>(R.id.tvFeedTitle)
        feedBtn = findViewById(R.id.fetchBtn)
        feedBtn.setOnClickListener{
            requestApi(feedURL)
            initRecyclerView()
        }
        Log.d(tAGMainActivity, "onCreate: done")
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.feed_menu,menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.top10MenuItem -> { feedURL ="http://ax.itunes.apple.com/WebObjects/MZStoreServices.woa/ws/RSS/topfreeapplications/limit=10/xml" }
            R.id.top100MenuItem -> {feedURL = "http://ax.itunes.apple.com/WebObjects/MZStoreServices.woa/ws/RSS/topfreeapplications/limit=100/xml"}
            else -> return super.onOptionsItemSelected(item)
        }
        return super.onOptionsItemSelected(item)
    }

    private fun initRecyclerView() {
        rvMainFeed = findViewById(R.id.rvMainFeed)
        rvMainFeed.layoutManager = LinearLayoutManager(this)
        rvMainFeed.setHasFixedSize(true)
    }

    private fun downloadXML(urlPath: String?): String {
        val xmlResult = StringBuilder()

        try {
            val url = URL(urlPath)
            val connection: HttpURLConnection = url.openConnection() as HttpURLConnection
            val response = connection.responseCode
            Log.d(tAGMainActivity, "downloadXML: The response code was $response")

            val reader = BufferedReader(InputStreamReader(connection.inputStream))

            val inputBuffer = CharArray(500)
            var charsRead = 0
            while (charsRead >= 0) {
                charsRead = reader.read(inputBuffer)
                if (charsRead > 0) {
                    xmlResult.append(String(inputBuffer, 0, charsRead))
                }
            }
            reader.close()

            Log.d(tAGMainActivity, "Received ${xmlResult.length} bytes")
            return xmlResult.toString()

        } catch (e: MalformedURLException) {
            Log.e(tAGMainActivity, "downloadXML: Invalid URL ${e.message}")
        } catch (e: IOException) {
            Log.e(tAGMainActivity, "downloadXML: IO Exception reading data: ${e.message}")
        } catch (e: SecurityException) {
            e.printStackTrace()
            Log.e(tAGMainActivity, "downloadXML: Security exception.  Needs permissions? ${e.message}")
        } catch (e: Exception) {
            Log.e(tAGMainActivity, "Unknown error: ${e.message}")
        }
        return ""
    }

    private fun requestApi(url:String){
        var listItems = ArrayList<FeedEntry>()
        CoroutineScope(Dispatchers.IO).launch {
            val rssFeed = async {
                downloadXML(url)
            }.await()
            if (rssFeed.isEmpty()) {
                Log.e(tAGMainActivity, "requestApi fun: Error downloading")
            } else {
                val parseApplications = async {
                    FeedParser()
                }.await()

                parseApplications.parseXmlData(rssFeed)
                listItems = parseApplications.getParsedList()
                withContext(Dispatchers.Main) {
                    rvMainFeed.adapter = FeedAdapter(listItems)
                }
            }
        }
    }
}