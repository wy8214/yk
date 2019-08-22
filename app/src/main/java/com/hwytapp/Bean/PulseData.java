package com.hwytapp.Bean;

import com.xuhao.android.libsocket.sdk.client.bean.IPulseSendable;

import org.json.JSONObject;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.Charset;

public class PulseData implements IPulseSendable {
    private String str = "pulse";

    @Override
    public byte[] parse() {

        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("action", "client");
            jsonObject.put("cmd", "client_pulse");
            str = new String(jsonObject.toString().getBytes(), "UTF-8");

        }catch (Exception e)
        {}

        byte[] body = str.getBytes(Charset.defaultCharset());
        ByteBuffer bb = ByteBuffer.allocate(4 + body.length);
        bb.order(ByteOrder.BIG_ENDIAN);
        bb.putInt(body.length);
        bb.put(body);
        return bb.array();

    }
}

