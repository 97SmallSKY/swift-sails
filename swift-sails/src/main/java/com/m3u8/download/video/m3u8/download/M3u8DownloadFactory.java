package com.m3u8.download.video.m3u8.download;


import com.m3u8.bean.po.DownloadTask;
import com.m3u8.download.video.m3u8.Exception.M3u8Exception;
import com.m3u8.download.video.m3u8.listener.DownloadListener;
import com.m3u8.download.video.m3u8.utils.*;
import com.m3u8.download.video.utils.cmd.ExecuteCmdCommand;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Security;
import java.security.spec.AlgorithmParameterSpec;
import java.util.*;
import java.util.concurrent.*;

import static com.m3u8.download.video.m3u8.utils.Constant.FILESEPARATOR;


/**
 * @author liyaling
 * @email ts_liyaling@qq.com
 * @date 2019/12/14 16:05
 */

public class M3u8DownloadFactory {

    private M3u8Download m3u8Download;

    /**
     *
     * 解决java不支持AES/CBC/PKCS7Padding模式解密
     *
     */
    static {
        Security.addProvider(new BouncyCastleProvider());
    }

    public M3u8DownloadFactory() {

    }


    public class M3u8Download {

        //要下载的m3u8链接
        private final String DOWNLOADURL;

        //优化内存占用
        private final BlockingQueue<byte[]> BLOCKING_QUEUE = new LinkedBlockingQueue<>();

        //线程数
        private int threadCount = 1;

        //重试次数
        private int retryCount = 80;

        // 链接连接超时时间（单位：毫秒）
        private long timeoutMillisecond = 1000L;

        // 合并后的文件存储目录
        private String dir;

        // 合并后的视频文件名称
        private String fileName;

        // 已完成ts片段个数
        private int finishedCount = 0;

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

        //解密后的片段
        private Set<File> finishedFiles = new ConcurrentSkipListSet<>(Comparator.comparingInt(o -> Integer.parseInt(o.getName().replace(".xyz", ""))));

        //已经下载的文件大小
        private BigDecimal downloadBytes;
        // 总文件大小
        private BigDecimal totalFileSize;
        // 连接失败的连接
        private ConcurrentHashMap<Integer, String> connectFailUrl = new ConcurrentHashMap();

        {
            BigDecimal bigDecimal = new BigDecimal(0);
            totalFileSize = bigDecimal;
            downloadBytes = bigDecimal;
        }

        //监听间隔
        private volatile long interval = 0L;

        //自定义请求头
        private Map<String, Object> requestHeaderMap = new HashMap<>();
        ;

        //监听事件
        private Set<DownloadListener> listenerSet = new HashSet<>(5);

        //代理设置
        private Proxy proxy;

        //        后缀
        private String suffix;

        private int downloadId;

        private HashMap<String, String> urlTimeMap = new HashMap<>();
        // 开始下载时间
        private final long downloadStartMillis = System.currentTimeMillis();
        // 存放临时文件
        private String TEMP_PATH;
        // 线程池
        private ExecutorService fixedThreadPool;
        // 是否监听
        private volatile boolean isListening = true;
        // 已完成的url
        private Set<String> finishedUrlSet;

        private boolean pause;


        /**
         * 开始下载视频
         */
        public void start() {
            checkField();
            String tsUrl = getTsUrl();
            if (StringUtils.isEmpty(tsUrl))
                Log.i("不需要解密");
            startDownload();
        }


        /**
         * 下载视频
         */
        public synchronized void startDownload() {
            pause = false;
            finishedUrlSet = new LinkedHashSet<>(tsSet.size());

            //线程池
            fixedThreadPool = Executors.newFixedThreadPool(threadCount);
            int i = 0;
            //如果生成目录不存在，则创建
            File file1 = new File(dir);
            if (!file1.exists())
                file1.mkdirs();
            // 获取文件总大小
//            getM3u8FileSize();
            //执行多线程下载
            for (String s : tsSet) {
                if (!finishedUrlSet.contains(s)) {
                    fixedThreadPool.execute(getThread(s, ++i));
                }
            }
            fixedThreadPool.shutdown();
            //下载过程监视
            new Thread(() -> {
                int consume = 0;
                //轮询是否下载成功
                while (!fixedThreadPool.isTerminated() && isListening) {
                    try {

                        consume++;
                        Thread.sleep(1000L);
//                        Log.i("已用时" + consume + "秒！\t下载速度：" + StringUtils.convertToDownloadSpeed(new BigDecimal(downloadBytes.toString()).subtract(bigDecimal), 3) + "/s");

                        String downloadFile = StringUtils.convertToDownloadSpeed(downloadBytes, 2);
                        Log.i("文件大小：" + downloadFile);

                        Log.i("已用时" + consume + "秒！\t" +
                                "下载速度：" +
                                StringUtils.convertToDownloadSpeed(
                                        downloadBytes.divide(new BigDecimal(consume), 2, BigDecimal.ROUND_HALF_UP), 3)
                                + "/s"
                        );
                        Log.i("\t已完成" + finishedCount + "个，还剩" + (tsSet.size() - finishedCount) + "个！");
                        BigDecimal percent = new BigDecimal(finishedCount)
                                .divide(new BigDecimal(tsSet.size()), 4, BigDecimal.ROUND_HALF_UP)
                                .multiply(new BigDecimal(100)).setScale(3, BigDecimal.ROUND_HALF_UP);
                        Log.i("\t下载进度：" + percent
                        );

                        listenerEvent();
                        if (pause) {
                            if (Thread.currentThread().isInterrupted()) {
                                break;
                            }
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                if (pause) {
                    return;
                }
                Log.i("下载完成，正在合并文件！共" + finishedFiles.size() + "个！" + StringUtils.convertToDownloadSpeed(downloadBytes, 3));
                //开始合并视频
                mergeTs();
                //删除多余的ts片段
                deleteFiles();
                Log.i("视频合并完成，欢迎使用!");
//                         打开视频文件
                File mergeFile = new File(dir + FILESEPARATOR + fileName + suffix);
                ExecuteCmdCommand command = new ExecuteCmdCommand();
                Log.i("正在打开文件：" + mergeFile.getAbsolutePath());
                command.exec("cmd /C start PotPlayerMini64.exe " + "\"" + mergeFile.getAbsolutePath() + "\"");
            }).start();
            startListener(fixedThreadPool);
        }

        private void startListener(ExecutorService fixedThreadPool) {
            new Thread(() -> {
                for (DownloadListener downloadListener : listenerSet)
                    downloadListener.start(new DownloadTask());
                //轮询是否下载成功
                while (!fixedThreadPool.isTerminated() && isListening) {
                    try {
                        Thread.sleep(interval);
                        listenerEvent();

                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    if (pause) {
                        if (Thread.currentThread().isInterrupted()) {
                            break;
                        }
                    }
                }
                for (DownloadListener downloadListener : listenerSet) {
                    if (pause) {
                        downloadListener.pause();
                    } else {
                        downloadListener.end();
                    }
                }

            }).start();
            new Thread(() -> {
                while (!fixedThreadPool.isTerminated() && isListening) {
                    listenerEvent();
                    if (pause) {
                        if (Thread.currentThread().isInterrupted()) {
                            break;
                        }
                    }
                }
            }).start();

            new Thread(() -> {
                while (!fixedThreadPool.isTerminated() && isListening) {
                    listenerEvent();
                    if (pause) {
                        if (Thread.currentThread().isInterrupted()) {
                            break;
                        }
                    }
                }
            }).start();
        }

        /**
         * 合并下载好的ts片段
         */
        private void mergeTs() {
            long start11 = System.currentTimeMillis();
            try {
                File mergeFile = new File(dir + FILESEPARATOR + fileName + suffix);
                System.gc();
                System.out.println("文件名：" + mergeFile.getAbsolutePath());
                if (mergeFile.exists())
                    mergeFile.delete();
                else mergeFile.createNewFile();
                FileOutputStream fileOutputStream = new FileOutputStream(mergeFile);
                byte[] b = new byte[4096];
                for (File f : finishedFiles) {
                    FileInputStream fileInputStream = new FileInputStream(f);
                    int len;
                    while ((len = fileInputStream.read(b)) != -1) {
                        fileOutputStream.write(b, 0, len);
                    }
                    fileInputStream.close();
                    fileOutputStream.flush();
                }
                fileOutputStream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            long start12 = System.currentTimeMillis();
            System.out.println("正常合并用时：" + (start12 - start11) / 1000 + "  s");
        }

        /**
         * 删除下载好的片段
         */
        private void deleteFiles() {
            File file = new File(dir);
            for (File f : file.listFiles()) {
                if (f.getName().endsWith(".xy") || f.getName().endsWith(".xyz"))
                    f.delete();
            }
        }

        /**
         * 开启下载线程
         *
         * @param urls ts片段链接
         * @param i    ts片段序号
         * @return 线程
         */
        private Thread getThread(String urls, int i) {
            return new Thread(() -> {
                int count = 1;
                HttpURLConnection httpURLConnection = null;
                //xy为未解密的ts片段，如果存在，则删除
                File file2 = new File(TEMP_PATH + FILESEPARATOR + i + ".xy");
                if (file2.exists())
                    file2.delete();
                System.out.println("临时目录:" + file2.getParentFile().getAbsolutePath());
                OutputStream outputStream = null;
                InputStream inputStream1 = null;
                FileOutputStream outputStream1 = null;
                byte[] bytes;
                try {
                    bytes = BLOCKING_QUEUE.take();
                } catch (InterruptedException e) {
                    bytes = new byte[Constant.BYTE_COUNT];
                }
                //重试次数判断
                while (count <= retryCount) {
                    if (pause) {
                        break;
                    }
                    try {
                        //模拟http请求获取ts片段文件
                        URL url = new URL(urls);
                        if (proxy == null) {
                            httpURLConnection = (HttpURLConnection) url.openConnection();
                        } else {
                            httpURLConnection = (HttpURLConnection) url.openConnection(proxy);
                        }
                        httpURLConnection.setConnectTimeout((int) timeoutMillisecond);
                        for (Map.Entry<String, Object> entry : requestHeaderMap.entrySet())
                            httpURLConnection.addRequestProperty(entry.getKey(), entry.getValue().toString());
                        httpURLConnection.setUseCaches(false);
                        httpURLConnection.setReadTimeout((int) timeoutMillisecond);
                        httpURLConnection.setDoInput(true);

                        InputStream inputStream = httpURLConnection.getInputStream();
                        try {
                            outputStream = new FileOutputStream(file2);
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                            continue;
                        }
                        int len;
                        //将未解密的ts片段写入文件
                        while ((len = inputStream.read(bytes)) != -1) {
                            outputStream.write(bytes, 0, len);
                            synchronized (this) {
                                downloadBytes = downloadBytes.add(new BigDecimal(len));
                            }
                        }
                        outputStream.flush();
                        inputStream.close();
                        inputStream1 = new FileInputStream(file2);
                        int available = inputStream1.available();
                        if (bytes.length < available)
                            bytes = new byte[available];
                        inputStream1.read(bytes);
                        File file = new File(TEMP_PATH + FILESEPARATOR + i + ".xyz");
                        outputStream1 = new FileOutputStream(file);
                        // 开始解密ts片段，这里我们把ts后缀改为了xyz，改不改都一样
                        byte[] decrypt = decrypt(bytes, available, key, iv, method);
                        if (decrypt == null)
                            outputStream1.write(bytes, 0, available);
                        else outputStream1.write(decrypt);
                        finishedUrlSet.add(urls);
                        finishedFiles.add(file);
                        finishedCount++;
                        // 写入数据库
//                        DownloadDetailPO po = new DownloadDetailPO();
//                        po.setIsValid("1");
//                        po.setDownloadId(downloadId);
//                        po.setDuration(null);
//                        po.setUrl(urls);
//                        po.setDuration(urlTimeMap.get(urls));
                        break;
                    } catch (Exception e) {
                        if (e instanceof InvalidKeyException || e instanceof InvalidAlgorithmParameterException) {
                            Log.e("解密失败！");
                            break;
                        }
                        Log.d("第" + count + "获取链接重试！\t" + urls);
                        count++;
//                        e.printStackTrace();
                    } finally {
                        try {
                            if (inputStream1 != null)
                                inputStream1.close();
                            if (outputStream1 != null)
                                outputStream1.close();
                            if (outputStream != null)
                                outputStream.close();
                            BLOCKING_QUEUE.put(bytes);
                        } catch (IOException | InterruptedException e) {
                            e.printStackTrace();
                        }
                        if (httpURLConnection != null) {
                            httpURLConnection.disconnect();
                        }
                    }
                }
                if (count > retryCount) {
                    //自定义异常
                    connectFailUrl.put(i, urls);
                    throw new M3u8Exception("连接超时！");
                }
            });
        }


        /**
         * 获取所有的ts片段下载链接
         *
         * @return 链接是否被加密，null为非加密
         */
        private String getTsUrl() {
            StringBuilder content = getUrlContent(DOWNLOADURL, false);
            //判断是否是m3u8链接
            if (!content.toString().contains("#EXTM3U"))
                throw new M3u8Exception(DOWNLOADURL + "不是m3u8链接！");
            String[] split = content.toString().split("\\n");
            String keyUrl = "";
            boolean isKey = false;
            for (String s : split) {
                //如果含有此字段，则说明只有一层m3u8链接
                if (s.contains("#EXT-X-KEY") || s.contains("#EXTINF")) {
                    isKey = true;
                    keyUrl = DOWNLOADURL;
                    break;
                }
                //如果含有此字段，则说明ts片段链接需要从第二个m3u8链接获取
                if (s.contains(".m3u8")) {
                    if (StringUtils.isUrl(s))
                        return s;
                    String relativeUrl = DOWNLOADURL.substring(0, DOWNLOADURL.lastIndexOf("/") + 1);
                    if (s.startsWith("/"))
                        s = s.replaceFirst("/", "");
                    keyUrl = mergeUrl(relativeUrl, s);
                    break;
                }
            }
            if (StringUtils.isEmpty(keyUrl))
                throw new M3u8Exception("未发现key链接！");
            //获取密钥
            String key1 = isKey ? getKey(keyUrl, content) : getKey(keyUrl, null);
            if (StringUtils.isNotEmpty(key1))
                key = key1;
            else key = null;
            return key;
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
                throw new M3u8Exception(DOWNLOADURL + "不是m3u8链接！");
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
         * 获取下载文件总大小
         */
        public void getM3u8FileSize() {
            ExecutorService executorService = Executors.newFixedThreadPool(10);
            List<Future<Long>> futures = new ArrayList<>();

            for (String url : tsSet) {
                Future<Long> future = executorService.submit(() -> {
                    long fileSize = getFileSize(url);
                    return fileSize;
                });
                futures.add(future);
            }


            for (Future<Long> future : futures) {
                try {
                    totalFileSize = totalFileSize.add(BigDecimal.valueOf(future.get()));
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
            }

            System.out.println("Total file size: " + totalFileSize + " bytes");

            executorService.shutdown();
        }

        private long getFileSize(String url) throws IOException {
            HttpURLConnection httpURLConnection = (HttpURLConnection) new URL(url).openConnection();
            httpURLConnection.setConnectTimeout((int) timeoutMillisecond);
            httpURLConnection.setReadTimeout((int) timeoutMillisecond);
            httpURLConnection.setUseCaches(false);
            httpURLConnection.setDoInput(true);
            for (Map.Entry<String, Object> entry : requestHeaderMap.entrySet())
                httpURLConnection.addRequestProperty(entry.getKey(), entry.getValue().toString());
            return httpURLConnection.getContentLengthLong();
        }


        /**
         * 解密ts
         *
         * @param sSrc   ts文件字节数组
         * @param length
         * @param sKey   密钥
         * @return 解密后的字节数组
         */
        private byte[] decrypt(byte[] sSrc, int length, String sKey, String iv, String method) throws Exception {
            if (StringUtils.isNotEmpty(method) && !method.contains("AES"))
                throw new M3u8Exception("未知的算法！");
            // 判断Key是否正确
            if (StringUtils.isEmpty(sKey))
                return null;
            // 判断Key是否为16位
            if (sKey.length() != 16 && !isByte) {
                throw new M3u8Exception("Key长度不是16位！");
            }
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS7Padding");
            SecretKeySpec keySpec = new SecretKeySpec(isByte ? keyBytes : sKey.getBytes(StandardCharsets.UTF_8), "AES");
            byte[] ivByte;
            if (iv.startsWith("0x"))
                ivByte = StringUtils.hexStringToByteArray(iv.substring(2));
            else ivByte = iv.getBytes();
            if (ivByte.length != 16)
                ivByte = new byte[16];
            //如果m3u8有IV标签，那么IvParameterSpec构造函数就把IV标签后的内容转成字节数组传进去
            AlgorithmParameterSpec paramSpec = new IvParameterSpec(ivByte);
            cipher.init(Cipher.DECRYPT_MODE, keySpec, paramSpec);
            return cipher.doFinal(sSrc, 0, length);
        }

        /**
         * 字段校验
         */
        private void checkField() {
            if ("m3u8".compareTo(MediaFormat.getMediaFormat(DOWNLOADURL)) != 0)
                throw new M3u8Exception(DOWNLOADURL + "不是一个完整m3u8链接！");
            if (threadCount <= 0)
                throw new M3u8Exception("同时下载线程数只能大于0！");
            if (retryCount < 0)
                throw new M3u8Exception("重试次数不能小于0！");
            if (timeoutMillisecond < 0)
                throw new M3u8Exception("超时时间不能小于0！");
            if (StringUtils.isEmpty(dir))
                throw new M3u8Exception("视频存储目录不能为空！");
            if (StringUtils.isEmpty(fileName))
                throw new M3u8Exception("视频名称不能为空！");
            finishedCount = 0;
            method = "";
            key = "";
            isByte = false;
            iv = "";
            tsSet.clear();
            finishedFiles.clear();
            downloadBytes = new BigDecimal(0);
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


        /**
         * 监听事件
         */
        private void listenerEvent() {

            for (DownloadListener downloadListener : listenerSet) {
                // 耗时
                long end = System.currentTimeMillis();
                downloadListener.timeConsuming(StringUtils.formatMilliseconds(end - downloadStartMillis));
                // 当前下载文件大小
                BigDecimal downloadedFileSize = new BigDecimal(downloadBytes.toString());
                // 速度
                BigDecimal speed = BigDecimal.ZERO;
                try {
                    Thread.sleep(1000L);
                    speed = new BigDecimal(downloadBytes.toString()).subtract(downloadedFileSize);
                    downloadListener.speed(StringUtils.convertToDownloadSpeed(speed, 1) + "/s");
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                // 已下载文件
                downloadListener.downloadedFile(StringUtils.convertToDownloadSpeed(downloadedFileSize, 1));
                // 下载进度
                BigDecimal percent = new BigDecimal(finishedCount).divide
                                (new BigDecimal(tsSet.size()), 3, BigDecimal.ROUND_HALF_UP)
                        .multiply(new BigDecimal(100)).setScale(3, BigDecimal.ROUND_HALF_UP);
                downloadListener.percent(percent.doubleValue());

                if (percent.compareTo(BigDecimal.ZERO) == 1) {
                    BigDecimal fileSize = downloadedFileSize.divide(percent, 1, RoundingMode.HALF_UP);
                    // 总文件大小
                    downloadListener.fileSize(StringUtils.convertToDownloadSpeed(fileSize, 1));
                    // 预计完成时间
                    if (speed.compareTo(BigDecimal.ZERO) == 1) {
                        downloadListener.expectedCompletionTime(StringUtils.formatMilliseconds(
                                fileSize.subtract(downloadedFileSize).divide(speed, 3, RoundingMode.HALF_UP)
                                        .multiply(BigDecimal.valueOf(1000)).longValue()
                        ));
                    }
                }


            }
        }


        public boolean isPause() {
            return pause;
        }

        public void setPause(boolean pause) {
            this.pause = pause;
        }

        public BlockingQueue<byte[]> getBLOCKING_QUEUE() {
            return BLOCKING_QUEUE;
        }

        public Set<File> getFinishedFiles() {
            return finishedFiles;
        }

        public void setFinishedFiles(Set<File> finishedFiles) {
            this.finishedFiles = finishedFiles;
        }

        public BigDecimal getDownloadBytes() {
            return downloadBytes;
        }

        public void setDownloadBytes(BigDecimal downloadBytes) {
            this.downloadBytes = downloadBytes;
        }

        public Set<DownloadListener> getListenerSet() {
            return listenerSet;
        }

        public void setListenerSet(Set<DownloadListener> listenerSet) {
            this.listenerSet = listenerSet;
        }

        public Set<String> getFinishedUrlSet() {
            return finishedUrlSet;
        }

        public void setFinishedUrlSet(Set<String> finishedUrlSet) {
            this.finishedUrlSet = finishedUrlSet;
        }

        public void setFinishedCount(int finishedCount) {
            this.finishedCount = finishedCount;
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

        public String getSuffix() {
            return suffix;
        }

        public long getDownloadStartMillis() {
            return downloadStartMillis;
        }

        public ExecutorService getFixedThreadPool() {
            return fixedThreadPool;
        }

        public void setFixedThreadPool(ExecutorService fixedThreadPool) {
            this.fixedThreadPool = fixedThreadPool;
        }

        public boolean isListening() {
            return isListening;
        }

        public void setListening(boolean listening) {
            isListening = listening;
        }

        public String getTEMP_PATH() {
            return TEMP_PATH;
        }

        public void setTEMP_PATH(String TEMP_PATH) {
            this.TEMP_PATH = TEMP_PATH;
        }

        public String getDOWNLOADURL() {
            return DOWNLOADURL;
        }

        public int getThreadCount() {
            return threadCount;
        }

        public void setThreadCount(int threadCount) {
            if (BLOCKING_QUEUE.size() < threadCount) {
                for (int i = BLOCKING_QUEUE.size(); i < threadCount * Constant.FACTOR; i++) {
                    try {
                        BLOCKING_QUEUE.put(new byte[Constant.BYTE_COUNT]);
                    } catch (InterruptedException ignored) {
                    }
                }
            }
            this.threadCount = threadCount;
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

        public String getDir() {
            return dir;
        }

        public void setDir(String dir) {
            this.dir = dir;
        }

        public String getFileName() {
            return fileName;
        }

        public void setFileName(String fileName) {
            this.fileName = fileName;
        }

        public int getFinishedCount() {
            return finishedCount;
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

        public void setInterval(long interval) {
            this.interval = interval;
        }

        public void addListener(DownloadListener downloadListener) {
            synchronized (this) {
                listenerSet.add(downloadListener);
            }
        }

        private M3u8Download(String DOWNLOADURL) {
            this.DOWNLOADURL = DOWNLOADURL;
            requestHeaderMap.put("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/78.0.3904.108 Safari/537.36");
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

        public void setSuffix(String suffix) {
            this.suffix = suffix;
        }

        public int getDownloadId() {
            return downloadId;
        }

        public void setDownloadId(int downloadId) {
            this.downloadId = downloadId;
        }

    }

    /**
     * 获取实例
     *
     * @param downloadUrl 要下载的链接
     * @return 返回m3u8下载实例
     */
    public M3u8Download getInstance(String downloadUrl) {
        if (m3u8Download == null) {
            synchronized (M3u8Download.class) {
                if (m3u8Download == null) {
                    m3u8Download = new M3u8Download(downloadUrl);
                }
            }
        }
        return m3u8Download;
    }


}
