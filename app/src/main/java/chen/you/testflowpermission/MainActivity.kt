package chen.you.testflowpermission

import android.Manifest
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import chen.you.flows.R
import chen.you.flows.databinding.ActivityMainBinding
import chen.you.flowpermission.FlowPermission
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    //初始化时supportFragmentManager添加FlowPermissionFragment, 须lazy来初始化或者用局部变量, 同RxPermission
    private val flowPermission by lazy { FlowPermission(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.lifecycleOwner = this

        binding.bt1.setOnClickListener {
            testRequestEach()
        }

        binding.bt2.setOnClickListener {
            testRequest()
        }
    }

    //遍历所有Flow<Permission>, 也需要在manifest中配置
    private fun testRequestEach() {
        lifecycleScope.launch {
            flowPermission.requestEach(Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.READ_PHONE_STATE,
                Manifest.permission.CAMERA)
                .onCompletion { Log.i("youxiaochen", "testRequestEach onComplete...") }
                .collect { Log.i("youxiaochen", "collect permission = $it")}
        }
    }

    //返回Flow<Boolean> 请求的权限是否全部放行
    private fun testRequest() {
        lifecycleScope.launch {
            flowPermission.request(Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .onCompletion { Log.i("youxiaochen", "testRequestEach onComplete...") }
                .collect { Log.i("youxiaochen", "collect result = $it")}
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        binding.unbind()
    }
}