# play-integrity-api-android
<p>
Play Integrity API（https://developer.android.com/google/play/integrity/overview）
</p>
<p>
的Android代码示例，见Sample模块，起因是谷歌要求从SafetyNet API迁移至Play Integrity API
</p>
<p>
后端代码见https://github.com/HairySnow/play-integrity-api-java
</p>
<p>
通过对Google Play Integrity API进行封装，见Play-integrity-api模块
</p>
<p>
需要传入context、后端接收integrityToken的url（必须）
</p>
<p>
可选：超时时间、是否打印日志、想给后端接收integrityToken的url多传的参数、sslSocketFactory、trustManager
</p>
![image](https://user-images.githubusercontent.com/24764220/203707051-afdfb4fd-9062-4b30-b0aa-cac72899a075.png)

Thanks:https://github.com/1nikolas/play-integrity-checker-app
