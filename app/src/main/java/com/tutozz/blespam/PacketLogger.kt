package com.tutozz.blespam

import android.os.Handler
import android.os.Looper
import android.widget.ScrollView
import android.widget.TextView
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.atomic.AtomicLong

object PacketLogger {

    private val handler = Handler(Looper.getMainLooper())
    private val txCount = AtomicLong(0)
    private val rxCount = AtomicLong(0)
    private val logBuilder = StringBuilder()
    private val maxLines = 100
    private var lineCount = 0

    private var logView: TextView? = null
    private var scrollView: ScrollView? = null
    private var counterView: TextView? = null

    private val timeFormat = SimpleDateFormat("HH:mm:ss.SSS", Locale.getDefault())

    fun attach(log: TextView, scroll: ScrollView, counter: TextView) {
        logView = log
        scrollView = scroll
        counterView = counter
        logBuilder.clear()
        lineCount = 0
        txCount.set(0)
        rxCount.set(0)
        log.text = "Ready. Waiting for packets...\n"
    }

    fun detach() {
        logView = null
        scrollView = null
        counterView = null
    }

    fun logTx(spammerName: String, payloadHex: String) {
        val tx = txCount.incrementAndGet()
        val time = timeFormat.format(Date())
        val shortHex = if (payloadHex.length > 24) payloadHex.take(24) + "..." else payloadHex
        val line = "[$time] TX #$tx [$spammerName]\n  → $shortHex\n"
        appendLog(line)
        handler.post {
            counterView?.text = "TX: $tx | RX: ${rxCount.get()}"
        }
    }

    fun logRx(deviceName: String, rssi: Int, payloadHex: String) {
        val rx = rxCount.incrementAndGet()
        val time = timeFormat.format(Date())
        val shortHex = if (payloadHex.length > 24) payloadHex.take(24) + "..." else payloadHex
        val line = "[$time] RX #$rx [$deviceName] RSSI:${rssi}dBm\n  ← $shortHex\n"
        appendLog(line)
        handler.post {
            counterView?.text = "TX: ${txCount.get()} | RX: $rx"
        }
    }

    fun logEvent(msg: String) {
        val time = timeFormat.format(Date())
        appendLog("[$time] *** $msg ***\n")
    }

    private fun appendLog(line: String) {
        handler.post {
            if (lineCount >= maxLines) {
                val text = logBuilder.toString()
                val firstNewline = text.indexOf('\n')
                if (firstNewline >= 0) {
                    logBuilder.delete(0, firstNewline + 1)
                    lineCount--
                }
            }
            logBuilder.append(line)
            lineCount++
            logView?.text = logBuilder.toString()
            scrollView?.post {
                scrollView?.fullScroll(ScrollView.FOCUS_DOWN)
            }
        }
    }

    fun reset() {
        txCount.set(0)
        rxCount.set(0)
        logBuilder.clear()
        lineCount = 0
        handler.post {
            logView?.text = "Ready. Waiting for packets...\n"
            counterView?.text = "TX: 0 | RX: 0"
        }
    }
}
