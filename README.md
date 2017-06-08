# TCompress
###  Hello,这是一个Android图片压缩库
  压缩方面：尺寸、采样、质量三个方面进行压缩</br>
  优化：三星等部分手机的图片角度处理</br>
  扩展：压缩后图片最大宽高设定（压缩后尺寸接近设定最大值）、质量、格式、配置设定</br>
###  预览图:<br>
  <img width="350"  src="https://github.com/HoldMyOwn/TCompress/blob/master/preview/a.jpg"/><br>
###  创建对象:
<pre>
        //可以构造者方式设置,也可以创建对象设置属性值(不设定采用默认配置)
        Compress compress = new Compress.Builder()
                .setMaxWidth(700)
                .setMaxHeight(900)
                .setQuality(80)
                .setFormat(Bitmap.CompressFormat.JPEG)
                .setConfig(Bitmap.Config.RGB_565)
                .build();
        //  Compress compress = new Compress();
        //  compress.setConfig(Bitmap.Config.RGB_565);
        //  compress.setFormat(Bitmap.CompressFormat.WEBP);
        //  compress.setQuality(80);
        //  compress.setMaxWidth(800);
        //  compress.setMaxHeight(800);
</pre>
###  同步压缩图片:
<pre>
        //支持四种压缩转化，文件、Bitmap到压缩后的文件、Bitmap
        File compressedFile = compress.compressedToFile(mFile);
        
        if (compressedFile == null) {
            //请查看文件权限问题（其他问题基本不存在，可以查看日志详情）
            return;
        }
        showData(compressedFile);

        //另外三种
        File compressedFile1 = compress.compressedToFile(mBitmap);
        Bitmap compressedBitmap = compress.compressedToBitmap(mFile);
        Bitmap compressedBitmap1 = compress.compressedToBitmap(mBitmap);

      


</pre>
###  异步压缩图片:
<pre>
        //泛型设置回调方法第二个参数类型。如果不指定泛型，也可以根据方法名的ToFile、ToBitmap进行强转
        //文件压缩到指定文件
        compress.compressToFileAsync(mFile, new Compress.onCompressListener&lt;File>() {
            //onCompressStart是非抽象方法，可选监听 可以开启提示框等 默认不重写
            @Override
            public void onCompressStart() {
            //   showToast("开始压缩");
            }

            //compressFinish是抽象方法，必选监听 默认重写
            @Override
            public void compressFinish(boolean success, File file) {
                if (success) {
                    showData(file);
                }
                else {
                    //请查看文件权限问题（其他问题基本不存在，可以查看日志详情）
                }
            }
        });
        //----------------其他三种异步压缩类似-------------
        //  otherThreeAsync();
</pre>
###   具体细节用法,下载查看Demo
###   模板依赖:&nbsp;&nbsp;项目里面的TCompress模板
###   gradle依赖:&nbsp;&nbsp;&nbsp;compile&nbsp;'com.jkt:tcompress:1.2.0'





