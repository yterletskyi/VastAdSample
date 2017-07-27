package com.publisher.sample.sdk;

import android.util.Log;

import com.jamesmurty.utils.XMLBuilder;
import com.publisher.sample.sdk.model.preload.response.Ad;
import com.publisher.sample.sdk.model.preload.response.PlayPercentage;
import com.publisher.sample.sdk.model.preload.response.PreloadResponse;

import java.net.FileNameMap;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Created by yterletskyi on 26.07.17.
 */

public class VastCreator {

    private static final String REQUIRED_FIELD = "REQUIRED FIELD";

    public void build(PreloadResponse result) {
        Ad ad = result.ads.get(0);

        String impressionIrl = null;

        Map<Double, String> quartiles = new HashMap<>();
        for (PlayPercentage playPercentage : ad.adMarkup.tpat.playPercentage) {
            int urlIndex = 0;
            quartiles.put(playPercentage.checkpoint, playPercentage.urls.get(urlIndex));
        }

        FileNameMap fileNameMap = URLConnection.getFileNameMap();
        String mimeType = fileNameMap.getContentTypeFor(ad.adMarkup.url);

        try {

            XMLBuilder xmlBuilder = XMLBuilder.create("VAST").a("version", "2.0")
                    .e("Ad").a("id", ad.adMarkup.id)
                    .e("InLine")
                    .e("AdSystem").t("").up()
                    .e("AdTitle").t(REQUIRED_FIELD).up()
                    .e("Impression").cdata(REQUIRED_FIELD)
                    .e("Creatives")
                    .e("Creative")
                    .e("Linear")
                    .e("Duration").t(REQUIRED_FIELD).up()
                    .e("TrackingEvents")
                    .e("Tracking").a("event", "start").cdata(quartiles.get(0.0)).up()
                    .e("Tracking").a("event", "firstQuartile").cdata(quartiles.get(0.25)).up()
                    .e("Tracking").a("event", "midpoint").cdata(quartiles.get(0.5)).up()
                    .e("Tracking").a("event", "thirdQuartile").cdata(quartiles.get(0.75)).up()
                    .e("Tracking").a("event", "complete").cdata(quartiles.get(1.0)).up()
                    .e("Tracking").a("event", "mute").cdata(ad.adMarkup.tpat.mute.get(0)).up()
                    .e("Tracking").a("event", "unmute").cdata(ad.adMarkup.tpat.unmute.get(0)).up()
                    .e("Tracking").a("event", "closeLinear").cdata(ad.adMarkup.tpat.videoClose.get(0)).up()
                    .up()
                    .e("MediaFiles")
                    .e("MediaFile")
                    .a("delivery", REQUIRED_FIELD + " {streaming | progressive}")
                    .a("width", String.valueOf(ad.adMarkup.videoWidth))
                    .a("height", String.valueOf(ad.adMarkup.videoHeight))
                    .a("type", mimeType)
                    .cdata(ad.adMarkup.url);

            Properties outputProperties = new Properties();
            outputProperties.put(javax.xml.transform.OutputKeys.OMIT_XML_DECLARATION, "yes");

            String xml = xmlBuilder.asString(outputProperties);
            Log.i("info", xml);


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
