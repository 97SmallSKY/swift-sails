package com.m3u8.download.video.m3u8.download;


import com.m3u8.bean.po.DownloadSegment;
import com.m3u8.bean.po.DownloadTask;
import com.m3u8.download.video.m3u8.Exception.M3u8Exception;
import com.m3u8.download.video.m3u8.uiEnum.DownloadStatusEnum;
import com.m3u8.download.video.m3u8.listener.DownloadListener;
import com.m3u8.download.video.m3u8.utils.*;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.io.*;
import java.net.*;
import java.security.Security;
import java.util.*;

/**
 * 获取m3u8片段信息
 *
 * @author Small_Tsk
 * @create 2023-06-19
 **/
public class M3U8SegmentInfoExtractor {

    /**
     *
     * 解决java不支持AES/CBC/PKCS7Padding模式解密
     *
     */
    static {
        Security.addProvider(new BouncyCastleProvider());
    }

    public M3U8SegmentInfoExtractor(String downloadUrl) {
        this.downloadUrl = downloadUrl;
        requestHeaderMap.put("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/78.0.3904.108 Safari/537.36");
    }

    //要下载的m3u8链接
    private String downloadUrl;

    //重试次数
    private int retryCount = 80;

    // 链接连接超时时间（单位：毫秒）
    private long timeoutMillisecond = 1000L;

    // 合并后的视频文件名称
    private String fileName;

    // 解密算法名称
    private String method;

    // 密钥
    private String key = "";

    // 密钥字节
    private byte[] keyBytes = new byte[16];

    //key是否为字节
    private boolean isByte = false;

    //IV
    private String iv = "";

    //所有ts片段下载链接
    private Set<String> tsSet = new LinkedHashSet<>();

    //自定义请求头
    private Map<String, Object> requestHeaderMap = new HashMap<>();
    ;

    //监听事件
    private Set<DownloadListener> listenerSet = new HashSet<>(5);

    //代理设置
    private Proxy proxy;

    private int downloadId;
    private HashMap<String, String> urlTimeMap = new HashMap<>();

    /**
     * 获取所有的ts片段下载链接
     *
     * @return
     */
    public HashMap<Class, Object> getTsUrl() {
        checkField();
        StringBuilder content = getUrlContent(downloadUrl, false);
        //判断是否是m3u8链接
        if (!content.toString().contains("#EXTM3U"))
            throw new M3u8Exception(downloadUrl + "不是m3u8链接！");
        String[] split = content.toString().split("\\n");
        String keyUrl = "";
        boolean isKey = false;
        for (String s : split) {
            //如果含有此字段，则说明只有一层m3u8链接
            if (s.contains("#EXT-X-KEY") || s.contains("#EXTINF")) {
                isKey = true;
                keyUrl = downloadUrl;
                break;
            }
            //如果含有此字段，则说明ts片段链接需要从第二个m3u8链接获取
            if (s.contains(".m3u8")) {
                if (StringUtils.isUrl(s)) {
                    return null;
                }
                String relativeUrl = downloadUrl.substring(0, downloadUrl.lastIndexOf("/") + 1);
                if (s.startsWith("/"))
                    s = s.replaceFirst("/", "");
                keyUrl = mergeUrl(relativeUrl, s);
                break;
            }
        }
        if (StringUtils.isEmpty(keyUrl)) {
            throw new M3u8Exception("未发现key链接！");
        }
        //获取密钥
        String key1 = isKey ? getKey(keyUrl, content) : getKey(keyUrl, null);
        if (StringUtils.isNotEmpty(key1)) {
            key = key1;
            Log.i("需要解密");
        } else {
            key = null;
            Log.i("不需要解密");
        }
        DownloadTask downloadTask = new DownloadTask();
        downloadTask.setDownloadUrl(downloadUrl);
        downloadTask.setStatus(DownloadStatusEnum.CONNECTING.get());
        downloadTask.setByte(isByte);
        downloadTask.setM3u8Key(key);
        downloadTask.setIsValid("1");
        downloadTask.setKeyBytes(keyBytes.toString());

        List<DownloadSegment> downloadSegmentList = new ArrayList<>();
        int i = 0;
        for (String s : tsSet) {
            DownloadSegment downloadSegment = new DownloadSegment();
            downloadSegment.setSegmentNumber(++i);
            downloadSegment.setIsValid("1");
            downloadSegment.setSegmentPath(s);
            downloadSegmentList.add(downloadSegment);
            downloadSegment.setSize(s);
        }
        HashMap<Class, Object> map = new HashMap<>();
        map.put(DownloadTask.class, downloadTask);
        map.put(DownloadSegment.class, downloadSegmentList);
        return map;
    }

    /**
     * 获取ts解密的密钥，并把ts片段加入set集合
     *
     * @param url     密钥链接，如果无密钥的m3u8，则此字段可为空
     * @param content 内容，如果有密钥，则此字段可以为空
     * @return ts是否需要解密，null为不解密
     */
    private String getKey(String url, StringBuilder content) {
        StringBuilder urlContent;
        if (content == null || StringUtils.isEmpty(content.toString()))
            urlContent = getUrlContent(url, false);
        else urlContent = content;
        if (!urlContent.toString().contains("#EXTM3U"))
            throw new M3u8Exception(downloadUrl + "不是m3u8链接！");
        String[] split = urlContent.toString().split("\\n");
        for (String s : split) {
            //如果含有此字段，则获取加密算法以及获取密钥的链接
            if (s.contains("EXT-X-KEY")) {
                String[] split1 = s.split(",");
                for (String s1 : split1) {
                    if (s1.contains("METHOD")) {
                        method = s1.split("=", 2)[1];
                        continue;
                    }
                    if (s1.contains("URI")) {
                        key = s1.split("=", 2)[1];
                        continue;
                    }
                    if (s1.contains("IV"))
                        iv = s1.split("=", 2)[1];

                }
            }
        }
        String relativeUrl = url.substring(0, url.lastIndexOf("/") + 1);
        //将ts片段链接加入set集合
        for (int i = 0; i < split.length; i++) {
            String s = split[i];
            if (s.contains("#EXTINF")) {
                String s1 = split[++i];
                urlTimeMap.put(s1, s.split(":")[1] + 's');
                tsSet.add(StringUtils.isUrl(s1) ? s1 : mergeUrl(relativeUrl, s1));
            }
        }
        if (!StringUtils.isEmpty(key)) {
            key = key.replace("\"", "");
            return getUrlContent(StringUtils.isUrl(key) ? key : mergeUrl(relativeUrl, key), true).toString().replaceAll("\\s+", "");
        }
        return null;
    }

    /**
     * 模拟http请求获取内容
     *
     * @param urls  http链接
     * @param isKey 这个url链接是否用于获取key
     * @return 内容
     */
    public StringBuilder getUrlContent(String urls, boolean isKey) {
        int count = 1;
        HttpURLConnection httpURLConnection = null;
        StringBuilder content = new StringBuilder();
        while (count <= retryCount) {
            try {
                URL url = new URL(urls);
                if (proxy == null) {
                    httpURLConnection = (HttpURLConnection) url.openConnection();
                } else {
                    httpURLConnection = (HttpURLConnection) url.openConnection(proxy);
                }
                httpURLConnection.setConnectTimeout((int) timeoutMillisecond);
                httpURLConnection.setReadTimeout((int) timeoutMillisecond);
                httpURLConnection.setUseCaches(false);
                httpURLConnection.setDoInput(true);
                for (Map.Entry<String, Object> entry : requestHeaderMap.entrySet())
                    httpURLConnection.addRequestProperty(entry.getKey(), entry.getValue().toString());
                String line;
                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                if (isKey) {
                    byte[] bytes = new byte[128];
                    int len;
                    len = inputStream.read(bytes);
                    isByte = true;
                    if (len == 1 << 4) {
                        keyBytes = Arrays.copyOf(bytes, 16);
                        content.append("isByte");
                    } else
                        content.append(new String(Arrays.copyOf(bytes, len)));
                    return content;
                }
                while ((line = bufferedReader.readLine()) != null)
                    content.append(line).append("\n");
                bufferedReader.close();
                inputStream.close();
                Log.i(content);
                break;
            } catch (Exception e) {
                Log.d("第" + count + "获取链接重试！\t" + urls);
                count++;
//                    e.printStackTrace();
            } finally {
                if (httpURLConnection != null) {
                    httpURLConnection.disconnect();
                }
            }
        }
        if (count > retryCount) {
            for (DownloadListener downloadListener : listenerSet) {
                downloadListener.connectionFailed();
            }
            throw new M3u8Exception("连接超时！");

        }
        return content;
    }

    /**
     * 字段校验
     */
    private void checkField() {
        if ("m3u8".compareTo(MediaFormat.getMediaFormat(downloadUrl)) != 0)
            throw new M3u8Exception(downloadUrl + "不是一个完整m3u8链接！");
        if (retryCount < 0)
            throw new M3u8Exception("重试次数不能小于0！");
        if (timeoutMillisecond < 0)
            throw new M3u8Exception("超时时间不能小于0！");
        method = "";
        key = "";
        isByte = false;
        iv = "";
        tsSet.clear();
    }

    private String mergeUrl(String start, String end) {
        if (end.startsWith("/"))
            end = end.replaceFirst("/", "");
        int position = 0;
        String subEnd, tempEnd = end;
        while ((position = end.indexOf("/", position)) != -1) {
            subEnd = end.substring(0, position + 1);
            if (start.endsWith(subEnd)) {
                tempEnd = end.replaceFirst(subEnd, "");
                break;
            }
            ++position;
        }
        return start + tempEnd;
    }

    public Set<DownloadListener> getListenerSet() {
        return listenerSet;
    }

    public void setListenerSet(Set<DownloadListener> listenerSet) {
        this.listenerSet = listenerSet;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public byte[] getKeyBytes() {
        return keyBytes;
    }

    public void setKeyBytes(byte[] keyBytes) {
        this.keyBytes = keyBytes;
    }

    public Set<String> getTsSet() {
        return tsSet;
    }

    public void setTsSet(Set<String> tsSet) {
        this.tsSet = tsSet;
    }

    public int getRetryCount() {
        return retryCount;
    }

    public void setRetryCount(int retryCount) {
        this.retryCount = retryCount;
    }

    public long getTimeoutMillisecond() {
        return timeoutMillisecond;
    }

    public void setTimeoutMillisecond(long timeoutMillisecond) {
        this.timeoutMillisecond = timeoutMillisecond;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public void setLogLevel(int level) {
        Log.setLevel(level);
    }

    public Map<String, Object> getRequestHeaderMap() {
        return requestHeaderMap;
    }

    public void addRequestHeaderMap(Map<String, Object> requestHeaderMap) {
        this.requestHeaderMap.putAll(requestHeaderMap);
    }

    public void addListener(DownloadListener downloadListener) {
        synchronized (this) {
            listenerSet.add(downloadListener);
        }
    }


    public Proxy getProxy() {
        return proxy;
    }

    public void setProxy(int port) {
        this.proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress("127.0.0.1", port));
    }

    public void setProxy(String address, int port) {
        this.proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(address, port));
    }

    public void setProxy(Proxy.Type type, String address, int port) {
        this.proxy = new Proxy(type, new InetSocketAddress(address, port));
    }


    public int getDownloadId() {
        return downloadId;
    }

    public void setDownloadId(int downloadId) {
        this.downloadId = downloadId;
    }
}
