# FlowPermission
### 协程Flow之FlowPermission权限请求

##使用
```
allprojects {
    repositories {
        ...
        maven { url 'https://jitpack.io' }
    }  
}

dependencies {
	implementation 'com.github.youxiaochen:FlowPermission:1.1.0'
}
```

#####使用

```
lifecycleScope.launch {
    flowPermission.request(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
    .collect { Log.i("youxiaochen", "collect result = $it")}
}

```

