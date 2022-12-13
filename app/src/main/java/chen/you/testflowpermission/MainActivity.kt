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
import com.tbruyelle.rxpermissions3.RxPermissions
import io.reactivex.rxjava3.core.Observable
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val flowPermission by lazy { FlowPermission(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.lifecycleOwner = this

        binding.bt.setOnClickListener {
            RxPermissions(this).requestEach(Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.READ_PHONE_STATE,
                Manifest.permission.CAMERA)
                .subscribe {
                    Log.d("FlowPermission", "RxPermission1 RequestEach callback $it")
                }
            Observable.timer(3, TimeUnit.SECONDS).subscribe {
                RxPermissions(this).requestEach(Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.RECORD_AUDIO,
                    Manifest.permission.READ_PHONE_STATE,
                    Manifest.permission.CAMERA)
                    .subscribe {
                        Log.d("FlowPermission", "RxPermission2 RequestEach callback $it")
                    }
            }
        }

        binding.bt0.setOnClickListener {
            RxPermissions(this).request(Manifest.permission.READ_PHONE_STATE, Manifest.permission.CAMERA)
                .subscribe {
                    Log.d("FlowPermission", "RxPermission RequestEach callback $it")
                }
        }

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
                .onCompletion { Log.d("FlowPermission", "testRequestEach onComplete...") }
                .collect { Log.d("FlowPermission", "collect permission = $it")}
        }
    }

    //返回Flow<Boolean> 请求的权限是否全部放行
    private fun testRequest() {
        lifecycleScope.launch {
            flowPermission.request(Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .onCompletion { Log.d("FlowPermission", "testRequestEach onComplete...") }
                .collect { Log.d("FlowPermission", "collect result = $it")}
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        binding.unbind()
    }
}