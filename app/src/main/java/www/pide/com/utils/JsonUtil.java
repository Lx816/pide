package www.pide.com.utils;

import com.google.gson.Gson;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.util.zip.GZIPInputStream;

public class JsonUtil {
	public static final int BUFFER = 1024; 
    private static Gson gson = new Gson();

    public static <T> T fromJson(String result,Class<T> clazz) {
	return gson.fromJson(result, clazz);
    }
    public static <T> T fromJson(String result, Type clazz) {
	return gson.fromJson(result, clazz);
    }

    public static String toJson(Object src) {
	return gson.toJson(src);
    }

    public static <T> String toJson(Object src, Class<T> typeOfSrc) {
	return gson.toJson(src, typeOfSrc);
    }

    /**
     * 数据解压缩
     * @param data
     * @return
     * @throws Exception
     */
    public static byte[] decompress(byte[] data) throws Exception {
        ByteArrayInputStream bais = new ByteArrayInputStream(data);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        decompress(bais, baos);
        data = baos.toByteArray();
        baos.flush();
        baos.close();
        bais.close();
        return data;
    }
    /**
     * 数据解压缩
     * @param is
     * @param os
     * @throws Exception
     */
    public static void decompress(InputStream is, OutputStream os)
            throws Exception {
        GZIPInputStream gis = new GZIPInputStream(is);
        int count;
        byte data[] = new byte[BUFFER];
        while ((count = gis.read(data, 0, BUFFER)) != -1) {
            os.write(data, 0, count);
        }
        gis.close();
    }



}
