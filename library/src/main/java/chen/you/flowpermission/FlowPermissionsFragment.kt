package chen.you.flowpermission

import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.MainThread
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

/**
 * FlowPermissionsFragment Build.VERSION_CODES.M权限申请
 * author: you : 2021/11/14
 */
class FlowPermissionsFragment : Fragment() {

    private lateinit var launcher: ActivityResultLauncher<Array<String>>

    //请求回调
    private var requestFlow: MutableSharedFlow<Permission>? = null

    //已请求过的权限
    private var requestedPermissions = ArrayList<Permission>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        launcher = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { result ->
//            Log.i("youxiaochen", "onActivityResult $result")
            if (result.isNotEmpty()) {
                lifecycleScope.launch {
                    val iterator = requestedPermissions.iterator()
                    while (iterator.hasNext()) {
                        requestFlow?.emit(iterator.next())
                        iterator.remove()
                    }
                    result.forEach {
                        requestFlow?.emit(Permission(it.key, it.value, shouldShowRequestPermissionRationale(it.key)))
                    }
                    requestFlow = null
                }
            }
        }
    }

    @MainThread
    fun requestPermissions(permissions: Set<String>): Flow<Permission> {
        require(permissions.isNotEmpty()) { "FlowPermission request requires at least one input permission" }
        //如果前面的请求没有处理完再发出权限请求时ActivityResultLauncher会立刻回调空的Map<String, Boolean>
        if (requestFlow != null) return emptyFlow()
        val unrequestedPermissions = ArrayList<String>()
        val requestedPermissions = ArrayList<Permission>()
        permissions.forEach {
            when {
                isGranted(it) -> requestedPermissions.add(Permission(it, true))
                isRevoked(it) -> requestedPermissions.add(Permission(it, false))
                else -> unrequestedPermissions.add(it)
            }
        }
        return if (unrequestedPermissions.isEmpty()) {//无需要请求的权限
            requestedPermissions.asFlow()
        } else {
            this.requestedPermissions.addAll(requestedPermissions)
            launcher.launch(unrequestedPermissions.toTypedArray())
            requestFlow = MutableSharedFlow()
            requestFlow!!.take(permissions.size)
        }
    }

    internal fun isGranted(permission: String): Boolean {
        val act = checkNotNull(activity) { "This fragment must be attached to an activity" }
        return Build.VERSION.SDK_INT < Build.VERSION_CODES.M
                || act.checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED
    }

    internal fun isRevoked(permission: String): Boolean {
        val act = checkNotNull(activity) { "This fragment must be attached to an activity" }
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                && act.packageManager.isPermissionRevokedByPolicy(permission, act.packageName)
    }
}